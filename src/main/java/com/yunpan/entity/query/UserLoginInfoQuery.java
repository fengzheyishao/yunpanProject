package com.yunpan.entity.query;

import java.util.Date;


/**
 * @Description: 用户登录信息表Query
 * @auther: lnorly
 * @Date: 2025/02/19
 */
public class UserLoginInfoQuery extends BaseParam{
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 登录日期（年月日）
	 */
	private Date loginDate;

	private String loginDateStart;

	private String loginDateEnd;

	/**
	 * 登录次数
	 */
	private Integer loginCount;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId () {
		return this.id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId () {
		return this.userId;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public Date getLoginDate () {
		return this.loginDate;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	public Integer getLoginCount () {
		return this.loginCount;
	}

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy () {
		return this.userIdFuzzy;
	}

	public void setLoginDateStart(String loginDateStart) {
		this.loginDateStart = loginDateStart;
	}

	public String getLoginDateStart () {
		return this.loginDateStart;
	}

	public void setLoginDateEnd(String loginDateEnd) {
		this.loginDateEnd = loginDateEnd;
	}

	public String getLoginDateEnd () {
		return this.loginDateEnd;
	}


}