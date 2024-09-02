package com.firefly.sharemount.controller.interceptors;


import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.Main;
import com.firefly.sharemount.component.RedisTemplateComponent;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.JwtUtil;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplateComponent redisTemplateComponent;

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws IOException {
        System.out.println(request.getRequestURL());
        System.out.println(request.getMethod());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.info("Header Name: {}, Header Value: {}", headerName, request.getHeader(headerName));
        }

        //放行OPTIONS请求
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }

        String authToken = request.getHeader("Authorization");
        System.out.println(authToken);
        try {
            Map<String, Object> claims = JwtUtil.parseToken(authToken);
            BigInteger id = new BigInteger(claims.get("userId").toString());
            String redisToken = redisTemplateComponent.get("ShareMount-userId:" + id);
            if (redisToken == null || !redisToken.equals(authToken)) {
                throw new Exception();
            }
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            // 设置响应内容，结束请求
            out.write("Token验证失败或已过期，请重新登录！");
            out.flush();
            out.close();
            return false;
        }

        return true;
    }
}
