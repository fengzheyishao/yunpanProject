package com.yunpan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {
    /**
     * 是否检查参数
     * @return
     */
    boolean checkParams() default false;
    /**
     * 登录拦截器
     * @return
     */
    boolean checkLogin() default true;
    /**
     * 校验超级管理员
     * @return
     */
    boolean checkAdmin() default false;
}
