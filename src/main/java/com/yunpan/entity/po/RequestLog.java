package com.yunpan.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.yunpan.enums.DateTimePatternEnum;
import com.yunpan.utils.DateUtils;


/**
 * @Description: 请求日志信息表Po
 * @auther: lnorly
 * @Date: 2025/02/27
 */
public class RequestLog implements Serializable {
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 请求URL
	 */
	private String url;

	/**
	 * 请求方法（GET/POST/PUT/DELETE等）
	 */
	private String method;

	/**
	 * 请求头（JSON格式）
	 */
	private String requestHeaders;

	/**
	 * 请求体（JSON格式）
	 */
	private String requestBody;

	/**
	 * 响应体（JSON格式）
	 */
	private String responseBody;

	/**
	 * 响应状态码
	 */
	private Integer responseStatus;

	private Integer logStatus;

	/**
	 * 请求时间戳
	 */
	private Date timestamp;

	public Integer getLogStatus() {
		return logStatus;
	}

	public void setLogStatus(Integer logStatus) {
		this.logStatus = logStatus;
	}
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

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl () {
		return this.url;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod () {
		return this.method;
	}

	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public String getRequestHeaders () {
		return this.requestHeaders;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getRequestBody () {
		return this.requestBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public String getResponseBody () {
		return this.responseBody;
	}

	public void setResponseStatus(Integer responseStatus) {
		this.responseStatus = responseStatus;
	}

	public Integer getResponseStatus () {
		return this.responseStatus;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp () {
		return this.timestamp;
	}

	@Override
	public String toString() {
		return "主键ID:"+(id == null ? "空" :id) + ",用户id:"+(userId == null ? "空" :userId) + ",请求URL:"+(url == null ? "空" :url) + ",请求方法（GET/POST/PUT/DELETE等）:"+(method == null ? "空" :method) + ",请求头（JSON格式）:"+(requestHeaders == null ? "空" :requestHeaders) + ",请求体（JSON格式）:"+(requestBody == null ? "空" :requestBody) + ",响应体（JSON格式）:"+(responseBody == null ? "空" :responseBody) + ",响应状态码:"+(responseStatus == null ? "空" :responseStatus) + ",请求时间戳:"+(timestamp == null ? "空" :DateUtils.format(timestamp, DateTimePatternEnum.YYYY_MM_DD.getPattern()));
	}
}
