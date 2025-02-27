package com.yunpan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 请求日志信息表Mapper
 * @auther: lnorly
 * @Date: 2025/02/27
 */
public interface RequestLogMapper<T, P> extends BaseMapper {

	/**
	 * 根据Id查询
	 */
	T selectById(@Param("id") Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateById(@Param("bean") T t, @Param("id") Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteById(@Param("id") Long id);

	Integer deleteByQuery(@Param("query") P p);
}
