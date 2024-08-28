package com.firefly.sharecloud.controller;

import com.firefly.sharecloud.dto.RegisterAndLoginDto;
import com.firefly.sharecloud.dto.RegisterDTO;
import com.firefly.sharecloud.pojo.Result;
import com.firefly.sharecloud.pojo.User;
import com.firefly.sharecloud.service.UserService;
import com.firefly.sharecloud.utils.RegexUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Api(tags = "用户管理接口")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterDTO registerDto, HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        String verifyWay = registerDto.getVerifyWay();
        User user = userService.findByUserName(username);
        Object cacheCode = httpSession.getAttribute("code");
        String verification = registerDto.getVerification();

        if (verification == null || !verification.equals(cacheCode)) {
            //不一致，返回错误信息
            return Result.error(401, "验证码错误");
        }
        if (user == null) {
            userService.register(username, password, verifyWay);
        } else {
            return Result.error(401, "用户名已被注册");
        }

        return Result.success();


    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody RegisterAndLoginDto registerAndLoginDto, HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        return Result.error(501,"未实现");
    }
}
