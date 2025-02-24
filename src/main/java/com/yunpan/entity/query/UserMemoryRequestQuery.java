package com.yunpan.entity.query;

import java.util.Date;


/**
 * @Description: 用户内存申请信息表Query
 * @auther: lnorly
 * @Date: 2025/02/24
 */
public class UserMemoryRequestQuery extends BaseParam{
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
	 * 申请时间
	 */
	private Date requestTime;

	private String requestTimeStart;

	private String requestTimeEnd;

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
	private Date approvalTime;

	private String approvalTimeStart;

	private String approvalTimeEnd;

	/**
	 * 拒绝原因
	 */
	private String rejectionReason;

	private String rejectionReasonFuzzy;

	/**
	 * 备注
	 */
	private String notes;

	private String notesFuzzy;

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

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy () {
		return this.userIdFuzzy;
	}

	public void setRequestTimeStart(String requestTimeStart) {
		this.requestTimeStart = requestTimeStart;
	}

	public String getRequestTimeStart () {
		return this.requestTimeStart;
	}

	public void setRequestTimeEnd(String requestTimeEnd) {
		this.requestTimeEnd = requestTimeEnd;
	}

	public String getRequestTimeEnd () {
		return this.requestTimeEnd;
	}

	public void setApprovalTimeStart(String approvalTimeStart) {
		this.approvalTimeStart = approvalTimeStart;
	}

	public String getApprovalTimeStart () {
		return this.approvalTimeStart;
	}

	public void setApprovalTimeEnd(String approvalTimeEnd) {
		this.approvalTimeEnd = approvalTimeEnd;
	}

	public String getApprovalTimeEnd () {
		return this.approvalTimeEnd;
	}

	public void setRejectionReasonFuzzy(String rejectionReasonFuzzy) {
		this.rejectionReasonFuzzy = rejectionReasonFuzzy;
	}

	public String getRejectionReasonFuzzy () {
		return this.rejectionReasonFuzzy;
	}

	public void setNotesFuzzy(String notesFuzzy) {
		this.notesFuzzy = notesFuzzy;
	}

	public String getNotesFuzzy () {
		return this.notesFuzzy;
	}


}