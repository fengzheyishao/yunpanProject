package com.yunpan.aspect;

import com.yunpan.entity.constants.Constants;
import com.yunpan.entity.dto.SessionWebUserDto;
import com.yunpan.entity.po.RequestLog;
import io.netty.util.internal.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

@Aspect
@Component("requestLogAspect")
public class RequestLogAspect {
    @Resource
    private RequestLogRepository requestLogRepository;

    @Around("execution(* com.yunpan.controller.*.*(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        StringBuilder headersString = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headersString.append(headerName).append(": ").append(headerValue).append("\n");
        }

        // 读取并拼接请求体内容
        StringBuilder sb = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        String userId = null;
        for (Object arg : args) {
            if (arg instanceof HttpSession) {
                SessionWebUserDto sessionWebUserDto = getUserInfoFromSession((HttpSession)arg);
                if (sessionWebUserDto != null) userId = sessionWebUserDto.getUserId();
            }
            sb.append(arg.toString()).append("\n");
        }

        String requestBody = sb.toString();

        RequestLog requestLog = new RequestLog();
        if (!StringUtil.isNullOrEmpty(userId)) {
            requestLog.setUserId(userId);
        }
        requestLog.setUrl(url);
        requestLog.setMethod(method);
        requestLog.setRequestHeaders(headersString.toString());
        requestLog.setRequestBody(requestBody);
        requestLog.setTimestamp(new Date());

        Object result;
        try {
            result = joinPoint.proceed();

            if (result instanceof ResponseEntity<?>) {
                ResponseEntity<?> response = (ResponseEntity<?>) result;
                // 避免response或其body为空导致的空指针
                if (response != null) {
                    requestLog.setResponseStatus(response.getStatusCodeValue());
                    // 如果可能得到null，则toString前加判空处理
                    Object body = response.getBody();
                    requestLog.setResponseBody(body == null ? "" : body.toString());
                } else {
                    // 如果无法获取到ResponseEntity，按需处置
                    requestLog.setResponseStatus(200);
                    requestLog.setResponseBody("");
                }
            } else {
                // 如果返回结果并非ResponseEntity，按需处置
                requestLog.setResponseStatus(200);
                requestLog.setResponseBody(
                        result == null ? "" : result.toString()
                );
            }

        } catch (Throwable throwable) {
            // 异常处理
            requestLog.setResponseStatus(500);
            requestLog.setResponseBody(String.valueOf(throwable.getMessage()));
            throw throwable;
        } finally {
            requestLogRepository.save(requestLog);
        }

        return result;
    }

    protected SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        return (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
    }
}
