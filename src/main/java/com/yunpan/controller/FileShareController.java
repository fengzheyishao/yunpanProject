package com.yunpan.controller;

import com.yunpan.annotation.GlobalInterceptor;
import com.yunpan.annotation.VerifyParam;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.po.FileShare;
import com.yunpan.entity.query.FileShareQuery;
import com.yunpan.entity.vo.PaginationResultVO;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.service.FileShareService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Description: ServiceImpl
 * @auther: lnorly
 * @Date: 2024/09/23
 */
@RestController("fileShareController")
@RequestMapping("/share")
public class FileShareController extends ABaseController {

	@Resource
	private FileShareService fileShareService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadShareList")
	@GlobalInterceptor
	public ResponseVO loadShareList(HttpSession session, Integer pageNo, Integer pageSize) {
		FileShareQuery query = new FileShareQuery();
		query.setUserId(getUserInfoFromSession(session).getUserId());
		query.setPageNo(pageNo);
		query.setQueryFileName(true);
		query.setPageSize(pageSize);
		query.setOrderBy("share_time desc");
		PaginationResultVO resultVO = fileShareService.findListByPage(query);
		return getSuccessResponseVO(resultVO);
	}

	@RequestMapping("/loadExpireShareList")
	public ResponseVO loadExpireShareList(HttpSession session, String expireTimeEnd) {
		FileShareQuery query = new FileShareQuery();
		query.setUserId(getUserInfoFromSession(session).getUserId());
		query.setQueryFileName(true);
		query.setExpireTimeEnd(expireTimeEnd);
		query.setOrderBy("expire_time desc");
		PaginationResultVO resultVO = fileShareService.findListByPage(query);
		return getSuccessResponseVO(resultVO);
	}

	@RequestMapping("/shareFile")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO shareFile(HttpSession session,
								@VerifyParam(required = true) String fileId,
								@VerifyParam(required = true) Integer validType,
								String code){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		FileShare fileShare = new FileShare();
		fileShare.setFileId(fileId);
		fileShare.setValidType(validType);
		fileShare.setCode(code);
		fileShare.setUserId(userId);
		fileShareService.saveShare(fileShare);
		return getSuccessResponseVO(fileShare);
	}

	@RequestMapping("/cancelShare")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO cancelShare(HttpSession session,
								@VerifyParam(required = true) String shareIds){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		fileShareService.deleteFileShareBatch(shareIds.split(","), userId);
		return getSuccessResponseVO(null);
	}


}