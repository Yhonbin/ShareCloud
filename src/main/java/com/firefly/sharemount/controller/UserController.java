package com.firefly.sharemount.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.component.KeyValueTemplate;
import com.firefly.sharemount.pojo.dto.LoginDTO;
import com.firefly.sharemount.pojo.dto.RegisterDTO;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
import com.firefly.sharemount.service.IdentityCheckingService;
import com.firefly.sharemount.service.UserInfoService;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.RegexUtil;
import com.firefly.sharemount.utils.Sha256Util;
import io.swagger.annotations.Api;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.Set;
import java.util.UUID;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private KeyValueTemplate keyValueTemplate;

    private static final Integer TIME_OUT_MINUTE = 30;

    @Resource
    private UserService userService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private IdentityCheckingService identityCheckingService;

    @PostMapping("/email-verify")
    public Result<Object> sendEmailCode(@RequestBody JSONObject jsonObject) {
        String email = jsonObject.getString("account");
        if (RegexUtil.isEmailInvalid(email)) {
            return Result.error(404,"邮箱格式错误");
        }
        UserInfo userInfo = userInfoService.findByUserEmail(email);
        if (userInfo != null) return Result.error(401,"该邮箱已注册，请更改邮箱");
        identityCheckingService.sendEmailCode(email);
        return Result.success();
    }

    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterDTO registerDto) {
        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        String verifyNumber= registerDto.getVerifyNumber();
        String email = null, phoneNumber = null;
        if (!RegexUtil.isEmailInvalid(verifyNumber)) {
            email = verifyNumber;
        } else if (!RegexUtil.isPhoneInvalid(verifyNumber)) {
            phoneNumber = verifyNumber;
        }
        User user = userService.findByName(username);
        String verification = registerDto.getVerification();
        if (!identityCheckingService.checkEmailCode(verifyNumber,verification)) {
            return Result.error(401,"验证码错误");
        }
        if (user == null) {
            user = userInfoService.register(username,email,phoneNumber,password);
        } else {
            return Result.error(409,"该用户名已存在");
        }

        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginDTO loginDTO, HttpSession httpSession) {
        String loginName = loginDTO.getUserLoginName();
        String password = loginDTO.getPassword();
        UserInfo userInfo;
        if (!RegexUtil.isPhoneInvalid(loginName)) {
            userInfo = userInfoService.findByUserPhoneNumber(loginName);
        } else if (!RegexUtil.isEmailInvalid(loginName)) {
            userInfo = userInfoService.findByUserEmail(loginName);
        } else {
            User user = userService.findByName(loginName);
            if (user == null) return Result.error(401, "用户名或密码错误");
            userInfo = userInfoService.findByUserId(user.getId());
        }

        if (userInfo == null) {
            return Result.error(401, "用户名或密码错误");
        }
        String Sha256Password = Sha256Util.getSHA256StrJava(password);
        if (!Sha256Password.equals(userInfo.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        UUID key = UUID.randomUUID();
        httpSession.setAttribute("UUID", key);
        keyValueTemplate.set("SESSION:UUID:" + httpSession.getId(), key.toString());
        keyValueTemplate.setExpire("SESSION:UUID:" + httpSession.getId(),TIME_OUT_MINUTE-1);

        keyValueTemplate.set("SESSION:USER:" + httpSession.getId(), userInfo.getUserId().toString());
        keyValueTemplate.setExpire("SESSION:USER:" + httpSession.getId(),TIME_OUT_MINUTE);
        return Result.success();
    }

    @PostMapping("/participation")
    public Result<Object> addGroup(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String s = keyValueTemplate.get("SESSION:USER:" + session.getId());
        if (s == null) return Result.error(401,"登录过期，添加失败");
        BigInteger userId = new BigInteger(s);
        String groupName = jsonObject.get("group").toString();
        User userGroup = userInfoService.createGroup(userId,groupName);
        return Result.success(userGroup);
    }

}
