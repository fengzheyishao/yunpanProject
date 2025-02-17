package com.yunpan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: Mapper
 * @auther: lnorly
 * @Date: 2024/09/23
 */
public interface FileShareMapper<T, P> extends BaseMapper {

	/**
	 * 根据ShareId查询
	 */
	T selectByShareId(@Param("shareId") String shareId);

	/**
	 * 根据ShareId更新
	 */
	Integer updateByShareId(@Param("bean") T t, @Param("shareId") String shareId);

	/**
	 * 根据ShareId删除
	 */
	Integer deleteByShareId(@Param("shareId") String shareId);

	Integer deleteFileShareBatch(@Param("shareIdArray") String[] shareIdArray, @Param("userId") String userId);

	void updateShareShowCount(@Param("shareId") String shareId);
}