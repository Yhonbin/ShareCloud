package com.firefly.sharemount.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        HttpSession httpSession = request.getSession();
        BigInteger userId = (BigInteger) httpSession.getAttribute("userId");
        if (userId == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }

}
