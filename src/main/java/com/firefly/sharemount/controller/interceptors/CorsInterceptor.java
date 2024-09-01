package com.firefly.sharemount.controller.interceptors;

import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CorsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        response.setHeader("Access-Control-Allow-Origin", "*"); // 允许任何域名使用
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // 允许的请求方法
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization"); // 允许的请求头
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 是否允许发送Cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        return true;
    }
}
