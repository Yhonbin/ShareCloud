package com.firefly.sharemount.controller.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws IOException {
        HttpSession httpSession = request.getSession();
        BigInteger userId = (BigInteger) httpSession.getAttribute("userId");
        if (userId == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            // 设置响应内容，结束请求
            out.write("请先登录");
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

}
