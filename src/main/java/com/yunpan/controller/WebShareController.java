package com.yunpan.controller;

import com.yunpan.annotation.GlobalInterceptor;
import com.yunpan.annotation.VerifyParam;
import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.SessionShareDto;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.po.DownloadFile;
import com.yunpan.entity.po.FileInfo;
import com.yunpan.entity.po.FileShare;
import com.yunpan.entity.po.UserInfo;
import com.yunpan.entity.query.DownloadFileQuery;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.vo.*;
import com.yunpan.enums.FileDelFlagEnums;
import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.exception.BusinessException;
import com.yunpan.service.DownloadFileService;
import com.yunpan.service.FileInfoService;
import com.yunpan.service.FileShareService;
import com.yunpan.service.UserInfoService;
import com.yunpan.utils.CopyTools;
import com.yunpan.utils.StringTools;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@RestController("webShareController")
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController{

    @Resource
    private FileShareService fileShareService;
    @Resource
    private FileInfoService fileInfoService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private DownloadFileService downloadFileService;

    @RequestMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareLoginInfo(HttpSession session,
                                        @VerifyParam(required = true) String shareId) {
        SessionShareDto sessionShareDto = getShareInfoFromSession(session);
        if (sessionShareDto == null) {
            return getSuccessResponseVO(null);
        }
        ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);
        //判断是否是当前用户分享的文件
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        if (sessionWebUserDto != null && sessionShareDto.getShareId().
                equals(sessionWebUserDto.getUserId())) {
            shareInfoVO.setCurrentUser(true);
        } else {
            shareInfoVO.setCurrentUser(false);
        }
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    @RequestMapping("/getShareInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareInfo(@VerifyParam(required = true) String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO loadFileList(HttpSession session,
                                   @VerifyParam(required = true) String shareId,
                                   String filePid) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);

        FileInfoQuery query = new FileInfoQuery();

        if (!StringTools.isEmpty(filePid) && !Constants.ZERO_STR.equals(filePid)) {
            fileInfoService.checkFootFilePid(sessionShareDto.getFileId(), sessionShareDto.getShareUserId(), filePid);
            query.setFilePid(filePid);
        } else {
            query.setFileId(sessionShareDto.getFileId());
        }
        query.setUserId(sessionShareDto.getShareUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVO.class));
    }

    @RequestMapping("/shareDownloadInfo")
    public ResponseVO shareDownloadInfo(HttpSession session, DownloadFileQuery query) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        FileShare fileShare = fileShareService.getFileShareByShareId(query.getShareId());
        if (fileShare == null) {
            throw new BusinessException("未找到分享ID");
        } else if (!sessionWebUserDto.getIsAdmin() && !sessionWebUserDto.getUserId().equals(fileShare.getUserId())) {
            throw new BusinessException("不是本人查询");
        }

        query.setOrderBy("downloadTime desc");
        PaginationResultVO paginationResultVO = downloadFileService.findListByPage(query);
        return getSuccessResponseVO(paginationResultVO);
    }

    private ShareInfoVO getShareInfoCommon(String shareId) {
        FileShare share = fileShareService.getFileShareByShareId(shareId);
        if (share == null || (share.getExpireTime()!=null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        ShareInfoVO shareInfoVO = CopyTools.copy(share, ShareInfoVO.class);
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(share.getFileId(), share.getUserId());
        if (fileInfo == null || FileDelFlagEnums.DEL.getFlag().equals(fileInfo.getDelFlag())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        shareInfoVO.setFileName(fileInfo.getFileName());
        UserInfo userInfo = userInfoService.getUserInfoByUserId(fileInfo.getUserId());
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        shareInfoVO.setUserId(userInfo.getUserId());
        return shareInfoVO;
    }

    @RequestMapping("/checkShareCode")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO checkShareCode(HttpSession session,
                                     @VerifyParam(required = true) String shareId,
                                     @VerifyParam(required = true) String code) {
        SessionShareDto sessionShareDto = fileShareService.checkShareCode(shareId, code);
        session.setAttribute(Constants.SESSION_SHARE_KEY, sessionShareDto);
        return getSuccessResponseVO(null);
    }

    private SessionShareDto checkShare(HttpSession session, String shareId) {
        SessionShareDto sessionShareDto = getShareInfoFromSession(session);
        if (sessionShareDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_903.getMsg());
        }
        if (sessionShareDto.getExpireTime()!=null && new Date().after(sessionShareDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        return sessionShareDto;
    }

    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getFolderInfo(HttpSession session,
                                    @VerifyParam(required = true) String shareId,
                                    @VerifyParam(required = true) String path) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        return super.getFolderInfo(path, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/getFile/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getFile(HttpServletResponse response,
                        HttpSession session,
                        @PathVariable("fileId")String shareId,
                        @PathVariable("fileId")String fileId) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        super.getFile(response, fileId, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getVideoInfo(HttpServletResponse response,
                        HttpSession session,
                        @PathVariable("shareId")String shareId,
                        @PathVariable("fileId")String fileId) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        super.getFile(response, fileId, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO createDownloadUrl(HttpSession session,
                             @PathVariable("shareId")String shareId,
                             @PathVariable("fileId")String fileId) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        downloadFileService.add(shareId, fileId, sessionWebUserDto);
        return super.createDownloadUrl(fileId, sessionShareDto.getShareUserId());
    }

    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void download(HttpServletRequest request,
                               HttpServletResponse response,
                               @VerifyParam(required = true) @PathVariable("code")String code) throws Exception{
        super.download(request, response, code);
    }

    @RequestMapping("/saveShare")
    @GlobalInterceptor(checkParams = true, checkLogin = true)
    public ResponseVO saveShare(HttpSession session,
                          @VerifyParam(required = true) String shareId,
                          @VerifyParam(required = true) String shareFileIds,
                          @VerifyParam(required = true) String myFolderId) {
        SessionShareDto sessionShareDto = checkShare(session, shareId);
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        if (sessionShareDto.getShareUserId().equals(sessionWebUserDto.getUserId())) {
            throw new BusinessException("自己无法保存到自己的网盘");
        }
        fileInfoService.saveShare(sessionShareDto.getFileId(), shareFileIds, myFolderId, sessionShareDto.getShareUserId(), sessionWebUserDto.getUserId());
        return getSuccessResponseVO(null);
    }


}
