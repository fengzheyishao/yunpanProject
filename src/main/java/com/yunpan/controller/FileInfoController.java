package com.yunpan.controller;

import com.yunpan.annotation.GlobalInterceptor;
import com.yunpan.annotation.VerifyParam;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.dto.UploadResultDto;
import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.vo.FileInfoVO;
import com.yunpan.entity.vo.FileTipVO;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.enums.FileCategoryEnums;
import com.yunpan.enums.FileDelFlagEnums;
import com.yunpan.enums.FileFolderTypeEnums;
import com.yunpan.service.FileInfoService;
import com.yunpan.utils.CopyTools;
import com.yunpan.utils.StringTools;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Description: ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/11
 */
@RestController("fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {

	@Resource
	private FileInfoService fileInfoService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	@GlobalInterceptor
	public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
		FileCategoryEnums categoryEnums = FileCategoryEnums.getByCode(category);
		if (categoryEnums != null) {
			query.setFileCategory(categoryEnums.getCategory());
		}
		query.setUserId(getUserInfoFromSession(session).getUserId());
		query.setOrderBy("last_update_time desc");
		query.setDelFlag(FileDelFlagEnums.USING.getFlag());
		PaginationResultVO resultVO = fileInfoService.findListByPage(query);
		return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVO.class));
	}

	@RequestMapping("/uploadFile")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO uploadFile(HttpSession session,
								 String fileId,
								 MultipartFile file,
								 @VerifyParam(required = true) String fileName,
								 @VerifyParam(required = true) String filePid,
								 @VerifyParam(required = true) String fileMd5,
								 @VerifyParam(required = true) Integer chunkIndex,
								 @VerifyParam(required = true) Integer chunks) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		UploadResultDto uploadResultDto = fileInfoService.uploadFile(sessionWebUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
		return getSuccessResponseVO(uploadResultDto);
	}

	@RequestMapping("/getImage/{imageFolder}/{imageName}")
	public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
		super.getImage(response, imageFolder, imageName);
	}

	@RequestMapping("/ts/getVideoInfo/{fileId}")
	@GlobalInterceptor(checkParams = true)
	public void getVideoInfo(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, sessionWebUserDto.getUserId());
	}

	@RequestMapping("/getFile/{fileId}")
	@GlobalInterceptor(checkParams = true)
	public void getFile(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, sessionWebUserDto.getUserId());
	}

	@RequestMapping("/newFoloder")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO newFolder(HttpSession session,
								@VerifyParam(required = true) String filePid,
								@VerifyParam(required = true) String fileName) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		FileInfo fileInfo = fileInfoService.newFolder(filePid, userId, fileName, FileFolderTypeEnums.FOLDER.getType());
		return getSuccessResponseVO(fileInfo);
	}

	@RequestMapping("/getFolderInfo")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO getFolderInfo(HttpSession session,
								@VerifyParam(required = true) String path) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		return super.getFolderInfo(path, userId);
	}

	@RequestMapping("/rename")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO rename(HttpSession session,
							 @VerifyParam(required = true) String fileId,
							 @VerifyParam(required = true) String fileName) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		FileInfo fileInfo = this.fileInfoService.rename(fileId, userId, fileName);
		return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVO.class));
	}

	@RequestMapping("/loadAllFolder")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO loadAllFolder(HttpSession session,
									@VerifyParam(required = true) String filePid,
									String currentFileIds){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFilePid(filePid);
		fileInfoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
		if (!StringTools.isEmpty(currentFileIds)) {
			fileInfoQuery.setExcludeFileIdArray(currentFileIds.split(","));
		}
		fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
		fileInfoQuery.setOrderBy("create_time desc");
		List<FileInfo> fileInfoList = this.fileInfoService.findListByParam(fileInfoQuery);
		return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FileInfoVO.class));
	}

	@RequestMapping("/changeFileFolder")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO changeFileFolder(HttpSession session,
									@VerifyParam(required = true) String fileIds,
									@VerifyParam(required = true) String filePid){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		this.fileInfoService.changeFileFolder(fileIds, filePid, userId);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/createDownloadUrl/{fileId}")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO createDownloadUrl(HttpSession session,
										@VerifyParam(required = true) @PathVariable("fileId") String fileId){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		return super.createDownloadUrl(fileId, userId);
	}

	@RequestMapping("/download/{code}")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public void download(HttpServletRequest request,
							   HttpServletResponse response,
							   @VerifyParam(required = true) @PathVariable("code") String code) throws Exception{
		super.download(request, response, code);
	}

	@RequestMapping("/delFile")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		fileInfoService.removeFile2RecycleBatch(userId, fileIds);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/getFileTipInfo")
	public ResponseVO getFileTipInfo(HttpSession session) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		FileTipVO fileTipVO = this.fileInfoService.getFileTipInfoByUserId(userId);
		return getSuccessResponseVO(fileTipVO);
	}
}