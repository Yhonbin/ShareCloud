package com.firefly.sharemount.controller;

import com.firefly.sharemount.pojo.dto.LoginDTO;
import com.firefly.sharemount.pojo.dto.RegisterDTO;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
import com.firefly.sharemount.service.IdentityCheckingService;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.RegexUtil;
import com.firefly.sharemount.utils.Sha256Util;
import io.swagger.annotations.Api;
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

    @Resource
    private IdentityCheckingService identityCheckingService;

    @PostMapping("/send")
    public Result<Object> sendEmailCode(String email, HttpServletRequest request) {

        if (RegexUtil.isEmailInvalid(email)) {
            return Result.error(401,"邮箱格式错误");
        }
        identityCheckingService.sendEmailCode(email);

        return Result.success();
    }

    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterDTO registerDto, HttpServletRequest request) {

        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        String verifyWay = registerDto.getVerifyWay();
        User user = userService.findByName(username);


        String verification = registerDto.getVerification();
        if (user == null) {
            userService.register(username, password, verifyWay);
        } else {
            return Result.error(401, "用户名已被注册");
        }

        return Result.success();
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        String loginName = loginDTO.getUserLoginName();
        String password = loginDTO.getPassword();
        UserInfo userInfo;
        if (!RegexUtil.isPhoneInvalid(loginName)) {
            userInfo = userService.findByUserPhoneNumber(loginName);
        } else if (!RegexUtil.isEmailInvalid(loginName)) {
            userInfo = userService.findByUserEmail(loginName);
        } else {
            User user = userService.findByName(loginName);
            if (user == null) return Result.error(401, "用户名或密码错误");
            userInfo = userService.findByUserId(user.getId());
        }

        if (userInfo == null) {
            return Result.error(401, "用户名或密码错误");
        }
        String Sha256Password = Sha256Util.getSHA256StrJava(password);
        if (!Sha256Password.equals(userInfo.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        httpSession.setAttribute("userId", userInfo.getUserId());
        return Result.success();
    }
}
