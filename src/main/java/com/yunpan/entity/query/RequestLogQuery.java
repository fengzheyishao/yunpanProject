package com.yunpan.entity.query;

import java.util.Date;


/**
 * @Description: 请求日志信息表Query
 * @auther: lnorly
 * @Date: 2025/02/27
 */
public class RequestLogQuery extends BaseParam{
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 用户id
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 请求URL
	 */
	private String url;

	private String urlFuzzy;

	/**
	 * 请求方法（GET/POST/PUT/DELETE等）
	 */
	private String method;

	private String methodFuzzy;

	/**
	 * 请求头（JSON格式）
	 */
	private String requestHeaders;

	private String requestHeadersFuzzy;

	/**
	 * 请求体（JSON格式）
	 */
	private String requestBody;

	private String requestBodyFuzzy;

	/**
	 * 响应体（JSON格式）
	 */
	private String responseBody;

	private String responseBodyFuzzy;

	/**
	 * 响应状态码
	 */
	private Integer responseStatus;

	/**
	 * 请求时间戳
	 */
	private Date timestamp;

	private String timestampStart;

	private String timestampEnd;

	private Integer logStatus;

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

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy () {
		return this.userIdFuzzy;
	}

	public void setUrlFuzzy(String urlFuzzy) {
		this.urlFuzzy = urlFuzzy;
	}

	public String getUrlFuzzy () {
		return this.urlFuzzy;
	}

	public void setMethodFuzzy(String methodFuzzy) {
		this.methodFuzzy = methodFuzzy;
	}

	public String getMethodFuzzy () {
		return this.methodFuzzy;
	}

	public void setRequestHeadersFuzzy(String requestHeadersFuzzy) {
		this.requestHeadersFuzzy = requestHeadersFuzzy;
	}

	public String getRequestHeadersFuzzy () {
		return this.requestHeadersFuzzy;
	}

	public void setRequestBodyFuzzy(String requestBodyFuzzy) {
		this.requestBodyFuzzy = requestBodyFuzzy;
	}

	public String getRequestBodyFuzzy () {
		return this.requestBodyFuzzy;
	}

	public void setResponseBodyFuzzy(String responseBodyFuzzy) {
		this.responseBodyFuzzy = responseBodyFuzzy;
	}

	public String getResponseBodyFuzzy () {
		return this.responseBodyFuzzy;
	}

	public void setTimestampStart(String timestampStart) {
		this.timestampStart = timestampStart;
	}

	public String getTimestampStart () {
		return this.timestampStart;
	}

	public void setTimestampEnd(String timestampEnd) {
		this.timestampEnd = timestampEnd;
	}

	public String getTimestampEnd () {
		return this.timestampEnd;
	}


}
