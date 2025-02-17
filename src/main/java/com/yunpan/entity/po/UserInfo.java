package com.yunpan.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunpan.enums.DateTimePatternEnum;
import com.yunpan.utils.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * @Description: Po
 * @auther: lnorly
 * @Date: 2024/09/09
 */
public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private String userId;

	/**
	 * 
	 */
	private String nickName;

	/**
	 * 
	 */
	private String email;

	/**
	 * 
	 */
	private String qqOpenId;

	/**
	 * 
	 */
	private String qqAvatar;

	/**
	 * 
	 */
	private String password;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT-8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date joinTime;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT-8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLoginTime;

	/**
	 * 
	 */
	private Integer status;

	/**
	 * 
	 */
	private Long useSpace;

	/**
	 * 
	 */
	private Long totalSpace;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId () {
		return this.userId;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNickName () {
		return this.nickName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail () {
		return this.email;
	}

	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}

	public String getQqOpenId () {
		return this.qqOpenId;
	}

	public void setQqAvatar(String qqAvatar) {
		this.qqAvatar = qqAvatar;
	}

	public String getQqAvatar () {
		return this.qqAvatar;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword () {
		return this.password;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public Date getJoinTime () {
		return this.joinTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLoginTime () {
		return this.lastLoginTime;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus () {
		return this.status;
	}

	public void setUseSpace(Long useSpace) {
		this.useSpace = useSpace;
	}

	public Long getUseSpace () {
		return this.useSpace;
	}

	public void setTotalSpace(Long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public Long getTotalSpace () {
		return this.totalSpace;
	}

	@Override
	public String toString() {
		return ":"+(userId == null ? "空" :userId) + ",:"+(nickName == null ? "空" :nickName) + ",:"+(email == null ? "空" :email) + ",:"+(qqOpenId == null ? "空" :qqOpenId) + ",:"+(qqAvatar == null ? "空" :qqAvatar) + ",:"+(password == null ? "空" :password) + ",:"+(joinTime == null ? "空" :DateUtils.format(joinTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:"+(lastLoginTime == null ? "空" :DateUtils.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:"+(status == null ? "空" :status) + ",:"+(useSpace == null ? "空" :useSpace) + ",:"+(totalSpace == null ? "空" :totalSpace);
	}
}