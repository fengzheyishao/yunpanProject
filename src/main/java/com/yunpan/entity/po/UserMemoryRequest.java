package com.yunpan.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunpan.enums.DateTimePatternEnum;
import com.yunpan.utils.DateUtils;


/**
 * @Description: 用户内存申请信息表Po
 * @auther: lnorly
 * @Date: 2025/02/24
 */
public class UserMemoryRequest implements Serializable {
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 申请时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT-8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date requestTime;

	/**
	 * 申请内存大小（字节）
	 */
	private Long requestSize;

	/**
	 * 申请状态：0-待处理，1-已批准，2-已拒绝
	 */
	private Integer status;

	/**
	 * 批准时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT-8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date approvalTime;

	/**
	 * 拒绝原因
	 */
	private String rejectionReason;

	/**
	 * 备注
	 */
	private String notes;

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

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public Date getRequestTime () {
		return this.requestTime;
	}

	public void setRequestSize(Long requestSize) {
		this.requestSize = requestSize;
	}

	public Long getRequestSize () {
		return this.requestSize;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus () {
		return this.status;
	}

	public void setApprovalTime(Date approvalTime) {
		this.approvalTime = approvalTime;
	}

	public Date getApprovalTime () {
		return this.approvalTime;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public String getRejectionReason () {
		return this.rejectionReason;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes () {
		return this.notes;
	}

	@Override
	public String toString() {
		return "主键ID:"+(id == null ? "空" :id) + ",用户ID:"+(userId == null ? "空" :userId) + ",申请时间:"+(requestTime == null ? "空" :DateUtils.format(requestTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",申请内存大小（字节）:"+(requestSize == null ? "空" :requestSize) + ",申请状态：0-待处理，1-已批准，2-已拒绝:"+(status == null ? "空" :status) + ",批准时间:"+(approvalTime == null ? "空" :DateUtils.format(approvalTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",拒绝原因:"+(rejectionReason == null ? "空" :rejectionReason) + ",备注:"+(notes == null ? "空" :notes);
	}
}