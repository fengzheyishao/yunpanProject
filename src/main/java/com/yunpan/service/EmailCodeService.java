package com.yunpan.service;

import com.yunpan.entity.po.EmailCode;
import com.yunpan.entity.query.EmailCodeQuery;
import com.yunpan.entity.vo.PaginationResultVO;
import java.util.List;
/**
 * @Description: 邮箱验证Service
 * @auther: lnorly
 * @Date: 2024/09/09
 */
public interface EmailCodeService {
	/**
	 * 根据条件查询列表
	 */
	List<EmailCode> findListByParam(EmailCodeQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(EmailCodeQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery query);

	/**
	 * 新增
	 */
	Integer add(EmailCode bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<EmailCode> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<EmailCode> listBean);

	void sendEmailCode(String email, Integer type);
	void checkCode(String email, String code);
}