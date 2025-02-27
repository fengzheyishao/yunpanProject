package com.yunpan.service;

import com.yunpan.entity.po.RequestLog;
import com.yunpan.entity.query.RequestLogQuery;
import com.yunpan.entity.vo.PaginationResultVO;
import java.util.List;
/**
 * @Description: 请求日志信息表Service
 * @auther: lnorly
 * @Date: 2025/02/27
 */
public interface RequestLogService {
	/**
	 * 根据条件查询列表
	 */
	List<RequestLog> findListByParam(RequestLogQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(RequestLogQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<RequestLog> findListByPage(RequestLogQuery query);

	/**
	 * 新增
	 */
	Integer add(RequestLog bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<RequestLog> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<RequestLog> listBean);

	/**
	 * 根据Id查询
	 */
	RequestLog getRequestLogById(Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateRequestLogById(RequestLog bean, Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteRequestLogById(Long id);

	Integer deleteRequestLogByQuery(RequestLogQuery query);
}
