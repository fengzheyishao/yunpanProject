package com.yunpan.service;

import com.yunpan.entity.dto.SessionShareDto;
import com.yunpan.entity.po.FileShare;
import com.yunpan.entity.query.FileShareQuery;
import com.yunpan.entity.vo.PaginationResultVO;

import java.util.List;
/**
 * @Description: Service
 * @auther: lnorly
 * @Date: 2024/09/23
 */
public interface FileShareService {
	/**
	 * 根据条件查询列表
	 */
	List<FileShare> findListByParam(FileShareQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(FileShareQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<FileShare> findListByPage(FileShareQuery query);

	/**
	 * 新增
	 */
	Integer add(FileShare bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<FileShare> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<FileShare> listBean);

	/**
	 * 根据ShareId查询
	 */
	FileShare getFileShareByShareId(String shareId);

	/**
	 * 根据ShareId更新
	 */
	Integer updateFileShareByShareId(FileShare bean, String shareId);

	/**
	 * 根据ShareId删除
	 */
	Integer deleteFileShareByShareId(String shareId);

	void saveShare(FileShare fileShare);

	void deleteFileShareBatch(String[] shareIdArrays, String userId);

	SessionShareDto checkShareCode(String shareId, String code);

}