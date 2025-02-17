package com.yunpan.controller;

import com.yunpan.component.RedisComponent;
import com.yunpan.entity.config.Appconfig;
import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.DownloadFileDto;
import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.vo.FolderVO;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.enums.FileCategoryEnums;
import com.yunpan.enums.FileFolderTypeEnums;
import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.exception.BusinessException;
import com.yunpan.service.FileInfoService;
import com.yunpan.utils.CopyTools;
import com.yunpan.utils.StringTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

public class CommonFileController extends ABaseController{
    @Resource
    private Appconfig appconfig;
    @Resource
    private FileInfoService fileInfoService;
    @Resource
    private RedisComponent redisComponent;

    protected void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringTools.isEmpty(imageFolder) || StringTools.isEmpty(imageName)
                || !StringTools.pathIsOk(imageFolder) || !StringTools.pathIsOk(imageName)) {
            return;
        }
        String imageSuffix = StringTools.getFileNameSuffix(imageName);
        String filePath = appconfig.getProjectFloder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        imageSuffix.replace(".", "");
        response.setContentType("image/" + imageSuffix);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }

    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;
        if (fileId.endsWith(".ts")) {
            String[] tsArrays = fileId.split("_");
            String realFileId = tsArrays[0];
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (fileInfo == null) {
                return;
            }
            String fileName = fileInfo.getFilePath();
            fileName = StringTools.getFileNamePre(fileName) + "/" + fileId;
            filePath = appconfig.getProjectFloder() + Constants.FILE_FOLDER_FILE + fileName;
        }
        else {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (fileInfo == null) {
                return;
            }
            if (FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                String fileNamePre = StringTools.getFileNamePre(fileInfo.getFilePath());
                filePath = appconfig.getProjectFloder() + Constants.FILE_FOLDER_FILE + fileNamePre + "/" + Constants.M3U8_NAME;
            } else {
                filePath = appconfig.getProjectFloder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        readFile(response, filePath);
    }

    protected ResponseVO getFolderInfo(String path, String userId) {
        String[] pathArrays = path.split("/");
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileIdArray(pathArrays);
        fileInfoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        String orderBy = "field(file_id,\"" + StringUtils.join(pathArrays, "\",\"") + "\")";
        fileInfoQuery.setOrderBy(orderBy);
        List<FileInfo> fileInfoList = this.fileInfoService.findListByParam(fileInfoQuery);
        return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FolderVO.class));
    }

    protected ResponseVO createDownloadUrl(String fileId, String userId) {
        FileInfo fileInfo = this.fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
        if (fileInfo == null || FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String code = StringTools.getRandomString(Constants.LEN_50);

        DownloadFileDto downloadFileDto = new DownloadFileDto();
        downloadFileDto.setFileName(fileInfo.getFileName());
        downloadFileDto.setDownloadCode(code);
        downloadFileDto.setFileId(fileId);
        downloadFileDto.setFilePath(fileInfo.getFilePath());

        redisComponent.saveDownloadCode(code, downloadFileDto);
        return getSuccessResponseVO(code);
    }

    protected void download(HttpServletRequest request,
                               HttpServletResponse response,
                               String code) throws Exception{
        DownloadFileDto downloadFileDto = redisComponent.getDownloadCode(code);
        if (downloadFileDto == null) {
            return;
        }
        String filePath = appconfig.getProjectFloder() + Constants.FILE_FOLDER_FILE + downloadFileDto.getFilePath();
        String fileName = downloadFileDto.getFileName();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        //IE 浏览器
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        readFile(response, filePath);
    }



}
