package com.yunpan.controller;

import com.yunpan.annotation.GlobalInterceptor;
import com.yunpan.annotation.VerifyParam;
import com.yunpan.component.RedisComponent;
import com.yunpan.entity.dto.SysSettingsDto;
import com.yunpan.entity.dto.UserMemoryRequestDto;
import com.yunpan.entity.po.UserMemoryRequest;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.query.RequestLogQuery;
import com.yunpan.entity.query.UserInfoQuery;
import com.yunpan.entity.query.UserMemoryRequestQuery;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.entity.vo.UserInfoVO;
import com.yunpan.enums.MemoryRequestStatusEnum;
import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.exception.BusinessException;
import com.yunpan.service.FileInfoService;
import com.yunpan.service.RequestLogService;
import com.yunpan.service.UserInfoService;
import com.yunpan.service.UserMemoryRequestService;
import com.yunpan.utils.CopyTools;
import com.yunpan.utils.StringTools;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController("adminController")
@RequestMapping("/admin")
public class AdminController extends CommonFileController{
    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RequestLogService requestLogService;

    @Resource
    private UserMemoryRequestService userMemoryRequestService;

    @RequestMapping("/getSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getSysSetting() {
        SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
        return getSuccessResponseVO(sysSettingsDto);
    }

    @RequestMapping("/saveSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO saveSysSettings(@VerifyParam(required = true) String registerEmailTitle,
                                      @VerifyParam(required = true) String registerEmailContent,
                                      @VerifyParam(required = true) Long maxMemory,
                                      @VerifyParam(required = true) Integer signIn,
                                      @VerifyParam(required = true) String everySignInText) {
        SysSettingsDto sysSettingsDto = new SysSettingsDto();
        sysSettingsDto.setRegisterEmailTitle(registerEmailTitle);
        sysSettingsDto.setRegisterEmailContent(registerEmailContent);
        sysSettingsDto.setSignIn(signIn);
        sysSettingsDto.setMaxMemory(maxMemory);
        sysSettingsDto.setEverySignInText(everySignInText);
        redisComponent.saveSysSettingsDto(sysSettingsDto);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadUserList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadUserList(UserInfoQuery userInfoQuery) {
        userInfoQuery.setOrderBy("join_time desc");
        PaginationResultVO resultVO = userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, UserInfoVO.class));
    }

    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserStatus(@VerifyParam(required = true) String userId,
                                       @VerifyParam(required = true) Integer status) {
        userInfoService.updateUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/updateUserSpace")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserSpace(@VerifyParam(required = true) String userId,
                                       @VerifyParam(required = true) Integer changeSpace) {
        userInfoService.changeUserSpace(userId, changeSpace);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadDataList(FileInfoQuery query) {
        query.setOrderBy("last_update_time desc");
        query.setQueryNickName(true);
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getFolderInfo(@VerifyParam(required = true) String path) {
        return super.getFolderInfo(path, null);
    }

    @RequestMapping("/getFile/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getFile(HttpServletResponse response,
                        @PathVariable("userId") @VerifyParam(required = true) String userId,
                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        super.getFile(response, fileId, userId);
    }

    @RequestMapping("/ts/getVideoInfo/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getVideoInfo(HttpServletResponse response,
                             @PathVariable("userId") @VerifyParam(required = true) String userId,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        super.getFile(response, fileId, userId);
    }

    @RequestMapping("/createDownloadUrl/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO createDownloadUrl(HttpServletResponse response,
                                        @PathVariable("userId") @VerifyParam(required = true) String userId,
                                        @PathVariable("fileId") @VerifyParam(required = true) String fileId){
        return super.createDownloadUrl(fileId, userId);
    }

    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void download(HttpServletRequest request,
                         HttpServletResponse response,
                         @VerifyParam(required = true) @PathVariable("code") String code) throws Exception{
        super.download(request, response, code);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO delFile(@VerifyParam(required = true) String fileIdAndUserIds) {
        String[] fileIdAndUserIdsArray = fileIdAndUserIds.split(",");
        for (String fileIdAndUserId : fileIdAndUserIdsArray) {
            String[] itemArray = fileIdAndUserId.split("_");
            fileInfoService.deleteFileBatch(itemArray[0], itemArray[1], true);
        }
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getMemoryApplyList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getMemoryApplyList(UserMemoryRequestQuery query) {
        query.setQueryNickName(true);
        query.setOrderBy("request_time desc");
        return getSuccessResponseVO(userMemoryRequestService.findListByPage(query));
    }

    @RequestMapping("/updateUserMemory")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserMemory(@RequestBody UserMemoryRequestDto userMemoryRequestDto) {
        if (userMemoryRequestDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        UserMemoryRequest userMemoryRequest = new UserMemoryRequest();
        userMemoryRequest.setStatus(userMemoryRequestDto.getStatus());
        userMemoryRequest.setRequestTime(new Date());
        if (!StringTools.isEmpty(userMemoryRequestDto.getRejectionReason()))
            userMemoryRequest.setRejectionReason(userMemoryRequestDto.getRejectionReason());
        for (Long userId: userMemoryRequestDto.getUserIds()) {
            userMemoryRequestService.adminUserMemoryApply(userMemoryRequest, userId);
        }
        return getSuccessResponseVO("审核成功");
    }

    @RequestMapping("/updateQueryAllUserMemory")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateQueryAllUserMemory(@RequestBody UserMemoryRequestDto userMemoryRequestDto) {
        UserMemoryRequest userMemoryRequest = new UserMemoryRequest();
        userMemoryRequest.setStatus(userMemoryRequestDto.getStatus());
        userMemoryRequest.setRequestTime(new Date());
        if (!StringTools.isEmpty(userMemoryRequestDto.getRejectionReason()))
            userMemoryRequest.setRejectionReason(userMemoryRequestDto.getRejectionReason());
        UserMemoryRequestQuery query = userMemoryRequestDto.getQuery();
        if (query == null) {
            query = new UserMemoryRequestQuery();
        }
        query.setStatus(MemoryRequestStatusEnum.PENDING.getCode());
        userMemoryRequestService.adminUserMemoryApplyBatch(userMemoryRequest, query);
        return getSuccessResponseVO("审核成功");
    }

    @RequestMapping("/deleteUserMemoryById")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO deleteUserMemoryById(@RequestBody UserMemoryRequestDto userMemoryRequestDto) {
        for (Long id: userMemoryRequestDto.getUserIds()) {
            this.userMemoryRequestService.deleteUserMemoryRequestById(id);
        }
        return getSuccessResponseVO("删除成功");
    }

    @RequestMapping("/selectLog")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO selectLog(RequestLogQuery requestLogQuery) {
        requestLogQuery.setOrderBy("timestamp desc");
        return getSuccessResponseVO(this.requestLogService.findListByPage(requestLogQuery));
    }

    @RequestMapping("/deleteRequestLogById")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO deleteRequestLogById(String ids) {
        String[] idArray = ids.split(",");
        for (String id: idArray) {
            this.requestLogService.deleteRequestLogById(Long.parseLong(id));
        }
        return getSuccessResponseVO("删除成功");
    }

    @RequestMapping("/deleteRequestLogByQuery")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO deleteRequestLogByQuery(RequestLogQuery query) {
        this.requestLogService.deleteRequestLogByQuery(query);
        return getSuccessResponseVO("删除成功");
    }
}
