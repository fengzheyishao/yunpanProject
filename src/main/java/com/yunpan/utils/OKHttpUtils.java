package com.yunpan.utils;

import com.yunpan.enums.ResponseCodeEnum;
import com.yunpan.exception.BusinessException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class OKHttpUtils {
    private static Logger logger = LoggerFactory.getLogger(OKHttpUtils.class);
    /**
     * 超时时间
     */
    private static final int TIME_OUT_SECONDS = 8;

    private static OkHttpClient.Builder getClientBuilder() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(false);
        clientBuilder.connectTimeout(TIME_OUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        clientBuilder.readTimeout(TIME_OUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        return clientBuilder;
    }

    public static Request.Builder getRequestBuilder(Map<String, String> header){
        Request.Builder requestBuilder = new Request.Builder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String key = entry.getKey();
            String value;
            if (header.get(key) == null) {
                value = "";
            } else {
                value = header.get(key);
            }
            requestBuilder.addHeader(key, value);
        }
        return requestBuilder;
    }

    public static FormBody.Builder getBuilder(Map<String, String> params){
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value;
            if (params.get(key) == null) {
                value = "";
            } else {
                value = params.get(key);
            }
            builder.add(key, value);
        }
        return builder;
    }

    public static String getRequest(String url) throws BusinessException {
        ResponseBody responseBody = null;
        try {
            OkHttpClient.Builder okHttpClient = getClientBuilder();
            Request.Builder requestBuilder = getRequestBuilder(null);
            OkHttpClient client = okHttpClient.build();

            Request request = requestBuilder.url(url).build();
            Response response = client.newCall(request).execute();
            responseBody = response.body();
            String responseStr = responseBody.string();
            logger.info("postRequest请求地址: {}, 返回信息: {}", url, responseStr);
            return responseStr;
        } catch (SocketTimeoutException | ConnectException e) {
            logger.error("OKhttp POST 请求超时,url:{}", url, e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Exception e) {
            logger.error("OKhttp GET 请求异常", e);
            return null;
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }

    }

    public static String getPost(String url, Map<String, String> params) throws BusinessException {
        ResponseBody responseBody = null;
        try {
            if (params == null) {
                params = new HashMap<>();
            }
            OkHttpClient.Builder okHttpClient = getClientBuilder();

            FormBody.Builder builder = getBuilder(params);
            RequestBody requestBody = builder.build();

            Request.Builder requestBuilder = getRequestBuilder(null);
            OkHttpClient client = okHttpClient.build();

            Request request = requestBuilder.url(url).post(requestBody).build();
            Response response = client.newCall(request).execute();
            responseBody = response.body();
            String responseStr = responseBody.string();
            logger.info("postRequest请求地址: {}, 返回信息: {}", url, responseStr);
            return responseStr;
        } catch (SocketTimeoutException | ConnectException e) {
            logger.error("OKhttp POST 请求超时,url:{}", url, e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Exception e) {
            logger.error("OKhttp POST 请求异常", e);
            return null;
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }

    }

}
