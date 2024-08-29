package com.firefly.sharemount.controller;

import com.firefly.sharemount.dto.LoginDTO;
import com.firefly.sharemount.dto.RegisterDTO;
import com.firefly.sharemount.pojo.Result;
import com.firefly.sharemount.pojo.User;
import com.firefly.sharemount.pojo.UserInfo;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.CodeUtil;
import com.firefly.sharemount.utils.RegexUtil;
import com.firefly.sharemount.utils.Sha256Util;
import io.swagger.annotations.Api;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户管理接口")
@RestController
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static final Integer TIME_OUT_SECOND = 1800;

    @PostMapping("/send")
    public Result<Object> sendCode(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {

        // 生成验证码
        String code = CodeUtil.generateVerifyCode(6);
        //todo 邮箱验证码发送服务||手机号码验证码发送服务

        // 存储到redis，设置超时时间

        return Result.success();
    }

    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterDTO registerDto, HttpServletRequest request) {

        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        String verifyWay = registerDto.getVerifyWay();
        User user = userService.findByName(username);

        //redis中
        String cacheCode = stringRedisTemplate.opsForValue().get("code");

        String verification = registerDto.getVerification();
        if (cacheCode == null) {
            return Result.error(404, "验证码已过期");
        } else if (verification == null || !verification.equals(cacheCode)) {
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
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();

        operations.set("userId", userInfo.getUserId().toString(), TIME_OUT_SECOND, TimeUnit.SECONDS);
        httpSession.setMaxInactiveInterval(TIME_OUT_SECOND);
        return Result.success();
    }
}
