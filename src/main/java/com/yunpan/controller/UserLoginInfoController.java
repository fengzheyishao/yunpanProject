package com.yunpan.controller;

import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.po.UserLoginInfo;
import com.yunpan.entity.query.UserLoginInfoQuery;
import com.yunpan.entity.vo.ResponseVO;
import com.yunpan.service.UserLoginInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户登录信息表ServiceImpl
 * @auther: lnorly
 * @Date: 2025/02/19
 */
@RestController()
@RequestMapping("/userLoginInfo")
public class UserLoginInfoController extends ABaseController {

	@Resource
	private UserLoginInfoService userLoginInfoService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserLoginInfoQuery query) {
		return getSuccessResponseVO(userLoginInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserLoginInfo bean) {
		userLoginInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserLoginInfo> listBean) {
		userLoginInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserLoginInfo> listBean) {
		userLoginInfoService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id查询
	 */
	@RequestMapping("/getUserLoginInfoById")
	public ResponseVO getUserLoginInfoById(Long id) {
		return getSuccessResponseVO(this.userLoginInfoService.getUserLoginInfoById(id));
	}

	/**
	 * 根据Id更新
	 */
	@RequestMapping("/updateUserLoginInfoById")
	public ResponseVO updateUserLoginInfoById(UserLoginInfo bean, Long id) {
		this.userLoginInfoService.updateUserLoginInfoById(bean, id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id删除
	 */
	@RequestMapping("/deleteUserLoginInfoById")
	public ResponseVO deleteUserLoginInfoById(Long id) {
		this.userLoginInfoService.deleteUserLoginInfoById(id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndLoginDate查询
	 */
	@RequestMapping("/getUserLoginInfoByUserIdAndLoginDate")
	public ResponseVO getUserLoginInfoByUserIdAndLoginDate(String userId, Date loginDate) {
		return getSuccessResponseVO(this.userLoginInfoService.getUserLoginInfoByUserIdAndLoginDate(userId, loginDate));
	}

	/**
	 * 根据UserIdAndLoginDate更新
	 */
	@RequestMapping("/updateUserLoginInfoByUserIdAndLoginDate")
	public ResponseVO updateUserLoginInfoByUserIdAndLoginDate(UserLoginInfo bean, String userId, Date loginDate) {
		this.userLoginInfoService.updateUserLoginInfoByUserIdAndLoginDate(bean, userId, loginDate);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndLoginDate删除
	 */
	@RequestMapping("/deleteUserLoginInfoByUserIdAndLoginDate")
	public ResponseVO deleteUserLoginInfoByUserIdAndLoginDate(String userId, Date loginDate) {
		this.userLoginInfoService.deleteUserLoginInfoByUserIdAndLoginDate(userId, loginDate);
		return getSuccessResponseVO(null);
	}

	/**
	 * 分析用户内存情况
	 */
	@RequestMapping("/getFileMemoryAnalysis")
	public ResponseVO getFileMemoryAnalysis(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		String userId = sessionWebUserDto.getUserId();
		Map map = this.userLoginInfoService.selectMemoryAnalysisByUserId(userId);
		response.setHeader("Cache-Control", "public, max-age=60");
		return getSuccessResponseVO(map);
	}
}
