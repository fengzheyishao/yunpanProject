package com.yunpan.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunpan.enums.DateTimePatternEnum;
import com.yunpan.utils.DateUtils;


/**
 * @Description: 用户登录信息表Po
 * @auther: lnorly
 * @Date: 2025/02/19
 */
public class UserLoginInfo implements Serializable {
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 登录日期（年月日）
	 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-8")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date loginDate;

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

	@Override
	public String toString() {
		return "主键ID:"+(id == null ? "空" :id) + ",用户ID:"+(userId == null ? "空" :userId) + ",登录日期（年月日）:"+(loginDate == null ? "空" :DateUtils.format(loginDate, DateTimePatternEnum.YYYY_MM_DD.getPattern())) + ",登录次数:"+(loginCount == null ? "空" :loginCount);
	}
}
