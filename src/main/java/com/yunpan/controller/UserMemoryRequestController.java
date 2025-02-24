package com.yunpan.controller;

import com.yunpan.annotation.VerifyParam;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.po.UserMemoryRequest;
import com.yunpan.entity.query.UserMemoryRequestQuery;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.service.UserMemoryRequestService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * @Description: 用户内存申请信息表ServiceImpl
 * @auther: lnorly
 * @Date: 2025/02/24
 */
@RestController()
@RequestMapping("/userMemoryRequest")
public class UserMemoryRequestController extends ABaseController {

	@Resource
	private UserMemoryRequestService userMemoryRequestService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(HttpSession session, UserMemoryRequestQuery query) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		query.setUserId(sessionWebUserDto.getUserId());
		return getSuccessResponseVO(userMemoryRequestService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserMemoryRequest bean) {
		userMemoryRequestService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserMemoryRequest> listBean) {
		userMemoryRequestService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserMemoryRequest> listBean) {
		userMemoryRequestService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id查询
	 */
	@RequestMapping("/getUserMemoryRequestById")
	public ResponseVO getUserMemoryRequestById(Long id) {
		return getSuccessResponseVO(this.userMemoryRequestService.getUserMemoryRequestById(id));
	}

	/**
	 * 根据Id更新
	 */
	@RequestMapping("/updateUserMemoryRequestById")
	public ResponseVO updateUserMemoryRequestById(UserMemoryRequest bean, Long id) {
		this.userMemoryRequestService.updateUserMemoryRequestById(bean, id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id删除
	 */
	@RequestMapping("/deleteUserMemoryRequestById")
	public ResponseVO deleteUserMemoryRequestById(Long id) {
		this.userMemoryRequestService.deleteUserMemoryRequestById(id);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/addUserMemoryApply")
	public ResponseVO addUserMemoryApply(HttpSession session,
										 @PathVariable("requestSize") @VerifyParam(required = true) Long requestSize,
										 @PathVariable("notes") String notes) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		UserMemoryRequest userMemoryRequest = new UserMemoryRequest();
		Date date = new Date();
		userMemoryRequest.setUserId(sessionWebUserDto.getUserId());
		userMemoryRequest.setRequestSize(requestSize);
		userMemoryRequest.setRequestTime(date);
		userMemoryRequest.setStatus(0);
		if (notes != null ) userMemoryRequest.setNotes(notes);
		this.userMemoryRequestService.add(userMemoryRequest);
		return getSuccessResponseVO("新增成功");
	}
}
