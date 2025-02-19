package com.yunpan.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @Description: 用户登录信息表Mapper
 * @auther: lnorly
 * @Date: 2025/02/19
 */
public interface UserLoginInfoMapper<T, P> extends BaseMapper {

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


	/**
	 * 根据UserIdAndLoginDate查询
	 */
	T selectByUserIdAndLoginDate(@Param("userId") String userId, @Param("loginDate") Date loginDate);

	/**
	 * 根据UserIdAndLoginDate更新
	 */
	Integer updateByUserIdAndLoginDate(@Param("bean") T t, @Param("userId") String userId, @Param("loginDate") Date loginDate);

	/**
	 * 根据UserIdAndLoginDate删除
	 */
	Integer deleteByUserIdAndLoginDate(@Param("userId") String userId, @Param("loginDate") Date loginDate);

	Integer updateLoginCount(@Param("id") Long id);
}
