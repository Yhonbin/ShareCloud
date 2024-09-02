package com.firefly.sharemount.config;

import com.firefly.sharemount.controller.interceptors.CorsInterceptor;
import com.firefly.sharemount.controller.interceptors.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private CorsInterceptor corsInterceptor;

    @Override
    // 注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor).addPathPatterns("/api/**");

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/email-verify","/api/user/register","/api/user/login");
    }



}
