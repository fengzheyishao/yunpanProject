package com.yunpan.controller;

import com.yunpan.annotation.GlobalInterceptor;
import com.yunpan.annotation.VerifyParam;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.query.FileInfoQuery;
import com.yunpan.entity.vo.FileInfoVO;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.enums.FileDelFlagEnums;
import com.yunpan.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ABaseController {
    @Resource
    private FileInfoService fileInfoService;

    @RequestMapping("/loadRecycleList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        FileInfoQuery query = new FileInfoQuery();
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        String userId = sessionWebUserDto.getUserId();
        query.setUserId(userId);
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setOrderBy("recovery_time desc");
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVO.class));
//        return getSuccessResponseVO(null);
    }

    @RequestMapping("/recoverFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        String userId = sessionWebUserDto.getUserId();
        fileInfoService.recoverFile2RecycleBatch(userId, fileIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        String userId = sessionWebUserDto.getUserId();
        fileInfoService.deleteFileBatch(userId, fileIds, false);
        return getSuccessResponseVO(null);
    }

}