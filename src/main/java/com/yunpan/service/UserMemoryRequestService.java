package com.yunpan.service;

import com.yunpan.entity.po.UserMemoryRequest;
import com.yunpan.entity.query.UserMemoryRequestQuery;
import com.yunpan.entity.vo.PaginationResultVO;
import java.util.List;
/**
 * @Description: 用户内存申请信息表Service
 * @auther: lnorly
 * @Date: 2025/02/24
 */
public interface UserMemoryRequestService {
	/**
	 * 根据条件查询列表
	 */
	List<UserMemoryRequest> findListByParam(UserMemoryRequestQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserMemoryRequestQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserMemoryRequest> findListByPage(UserMemoryRequestQuery query);

	/**
	 * 新增
	 */
	Integer add(UserMemoryRequest bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserMemoryRequest> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserMemoryRequest> listBean);

	/**
	 * 根据Id查询
	 */
	UserMemoryRequest getUserMemoryRequestById(Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateUserMemoryRequestById(UserMemoryRequest bean, Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteUserMemoryRequestById(Long id);


}
