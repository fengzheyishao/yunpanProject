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
 * @Date: 2024/09/23
 */
public class FileShare implements Serializable {
	/**
	 * 分享id
	 */
	private String shareId;

	/**
	 * 文件id
	 */
	private String fileId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 有效期类型 0:1 1:7 2:30 4:永远
	 */
	private Integer validType;

	/**
	 * 失效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT-8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireTime;

	/**
	 * 有效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss", timezone = "GMT-8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date shareTime;

	/**
	 * 提取码
	 */
	private String code;

	/**
	 * 浏览次数
	 */
	private Integer showCount;

	private String fileName;

	private String fileCover;

	private Integer fileCategory;

	private Integer fileType;

	private Integer folderType;


	public String getFileCover() {
		return fileCover;
	}

	public void setFileCover(String fileCover) {
		this.fileCover = fileCover;
	}

	public Integer getFileCategory() {
		return fileCategory;
	}

	public void setFileCategory(Integer fileCategory) {
		this.fileCategory = fileCategory;
	}

	public Integer getFileType() {
		return fileType;
	}

	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}

	public Integer getFolderType() {
		return folderType;
	}

	public void setFolderType(Integer folderType) {
		this.folderType = folderType;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getShareId () {
		return this.shareId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileId () {
		return this.fileId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId () {
		return this.userId;
	}

	public void setValidType(Integer validType) {
		this.validType = validType;
	}

	public Integer getValidType () {
		return this.validType;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Date getExpireTime () {
		return this.expireTime;
	}

	public void setShareTime(Date shareTime) {
		this.shareTime = shareTime;
	}

	public Date getShareTime () {
		return this.shareTime;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode () {
		return this.code;
	}

	public void setShowCount(Integer showCount) {
		this.showCount = showCount;
	}

	public Integer getShowCount () {
		return this.showCount;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "分享id:"+(shareId == null ? "空" :shareId) + ",文件id:"+(fileId == null ? "空" :fileId) + ",用户id:"+(userId == null ? "空" :userId) + ",有效期类型 0:1 1:7 2:30 4:永远:"+(validType == null ? "空" :validType) + ",失效时间:"+(expireTime == null ? "空" :DateUtils.format(expireTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",有效时间:"+(shareTime == null ? "空" :DateUtils.format(shareTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",提取码:"+(code == null ? "空" :code) + ",浏览次数:"+(showCount == null ? "空" :showCount);
	}
}