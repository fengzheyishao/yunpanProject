package com.yunpan.service;

import com.yunpan.entity.po.UserLoginInfo;
import com.yunpan.entity.query.UserLoginInfoQuery;
import com.yunpan.entity.vo.PaginationResultVO;

import java.util.Date;
import java.util.List;
/**
 * @Description: 用户登录信息表Service
 * @auther: lnorly
 * @Date: 2025/02/19
 */
public interface UserLoginInfoService {
	/**
	 * 根据条件查询列表
	 */
	List<UserLoginInfo> findListByParam(UserLoginInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserLoginInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserLoginInfo> findListByPage(UserLoginInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(UserLoginInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserLoginInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserLoginInfo> listBean);

	/**
	 * 根据Id查询
	 */
	UserLoginInfo getUserLoginInfoById(Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateUserLoginInfoById(UserLoginInfo bean, Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteUserLoginInfoById(Long id);

	/**
	 * 根据UserIdAndLoginDate查询
	 */
	UserLoginInfo getUserLoginInfoByUserIdAndLoginDate(String userId, Date loginDate);

	/**
	 * 根据UserIdAndLoginDate更新
	 */
	Integer updateUserLoginInfoByUserIdAndLoginDate(UserLoginInfo bean, String userId, Date loginDate);

	/**
	 * 根据UserIdAndLoginDate删除
	 */
	Integer deleteUserLoginInfoByUserIdAndLoginDate(String userId, Date loginDate);


}
