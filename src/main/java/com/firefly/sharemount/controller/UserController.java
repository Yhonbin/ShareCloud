package com.firefly.sharemount.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.component.RedisTemplateComponent;
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
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private RedisTemplateComponent redisTemplateComponent;

    private static final Integer TIME_OUT_MINUTE = 30;

    @Resource
    private UserService userService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private IdentityCheckingService identityCheckingService;

    @PostMapping("/email-verify")
    public Result<Object> sendEmailCode(@RequestBody JSONObject jsonObject) throws MessagingException {
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
        String verifyNumber= registerDto.getEmail();
        String email = null, phoneNumber = null;
        if (!RegexUtil.isEmailInvalid(verifyNumber)) {
            email = verifyNumber;
        } else if (!RegexUtil.isPhoneInvalid(verifyNumber)) {
            phoneNumber = verifyNumber;
        }
        User user = userService.findByName(username);
        String verification = registerDto.getVerificationCode();
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
        String loginName = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        UserInfo userInfo;
        if (loginName == null || password == null) {
            return Result.error(403, "请输入用户名和密码");
        }
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
        redisTemplateComponent.set("ShareMount:SESSION:UUID:" + httpSession.getId(), key.toString());
        redisTemplateComponent.setExpire("ShareMount:SESSION:UUID:" + httpSession.getId(),TIME_OUT_MINUTE-1, TimeUnit.MINUTES);

        redisTemplateComponent.set("ShareMount:SESSION:USER:" + httpSession.getId(), userInfo.getUserId().toString());
        redisTemplateComponent.setExpire("ShareMount:SESSION:USER:" + httpSession.getId(),TIME_OUT_MINUTE,TimeUnit.MINUTES);
        return Result.success();
    }

    @PostMapping("/participation")
    public Result<Object> addGroup(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        HttpSession session = request.getSession();
        BigInteger userId = userService.getUserId(session);

        String groupName = jsonObject.get("group").toString();
        User userGroup = userInfoService.createGroup(userId,groupName);
        return Result.success(userGroup);
    }

    @PostMapping("/join-group/{groupId}")
    public Result<Object> joinGroup(@PathVariable BigInteger groupId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        BigInteger userId = userService.getUserId(session);
        if (groupId == null) {
            return Result.error(401,"添加失败 请先输入要添加的小组信息");
        }
        if (!userInfoService.joinGroup(userId,groupId)) {
            return Result.error(404,"添加失败 您输入的小组不存在");
        }
        return Result.success();
    }

    @DeleteMapping("/exit-group/{groupId}")
    public Result<Object> exitGroup(@PathVariable BigInteger groupId,HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (groupId == null) {
            return Result.error(401,"退出失败 请先输入要退出的小组信息");
        }
        BigInteger userId = userService.getUserId(session);
        if (!userInfoService.exitGroup(userId, groupId)) {
            return Result.error(404, "退出失败 您已不在该小组");
        }
        return Result.success();
    }

    @DeleteMapping("/participation/{groupId}")
    public Result<Object> deleteGroup(@PathVariable BigInteger groupId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (groupId == null) {
            return Result.error(401,"删除失败 请先确定要删除的小组信息");
        }
        BigInteger userId = userService.getUserId(session);
        return Result.error(501,"未实现");
    }
}
