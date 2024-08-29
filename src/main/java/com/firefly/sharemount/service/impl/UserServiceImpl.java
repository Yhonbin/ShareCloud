package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.mapper.UserMapper;
import com.firefly.sharemount.pojo.Result;
import com.firefly.sharemount.pojo.User;
import com.firefly.sharemount.pojo.UserInfo;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.RegexUtil;
import com.firefly.sharemount.utils.Sha256Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByName(String username) {

        return userMapper.findByName(username);
    }

    @Override
    public UserInfo findByUserPhoneNumber(String loginName) {
        return userMapper.findByUserPhoneNumber(loginName);
    }

    @Override
    public UserInfo findByUserEmail(String loginName) {
        return userMapper.findByUserEmail(loginName);
    }

    @Override
    public UserInfo findByUserId(BigInteger userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public void register(String username, String password, String verifyWay) {
        BigInteger id = userMapper.addUser(username);

        String SHA256Password = Sha256Util.getSHA256StrJava(password);
        String email = null, phoneNumber = null;
        // 检验邮箱或个人手机号
        if (!RegexUtil.isPhoneInvalid(verifyWay)) {
            phoneNumber = verifyWay;
        } else if (!RegexUtil.isEmailInvalid(verifyWay)) {
            email = verifyWay;
        } else {
            Result.error(401, "手机号或邮箱格式错误");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(id);
        userInfo.setPassword(SHA256Password);
        userInfo.setEmail(email);
        userInfo.setPhoneNumber(phoneNumber);
        userMapper.register(userInfo);
    }


}
