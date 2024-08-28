package com.firefly.sharecloud.service.impl;

import com.firefly.sharecloud.mapper.UserMapper;
import com.firefly.sharecloud.pojo.Result;
import com.firefly.sharecloud.pojo.User;
import com.firefly.sharecloud.service.UserService;
import com.firefly.sharecloud.utils.RegexUtil;
import com.firefly.sharecloud.utils.Sha256Util;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByUserName(String username) {

        return userMapper.findByUserName(username);
    }

    @Override
    public void register(String username, String password, String verifyWay) {

        String SHA256Password = Sha256Util.getSHA256StrJava(password);

        // 检验邮箱或个人手机号
        if (!RegexUtil.isPhoneInvalid(verifyWay)) {

            userMapper.registerViaPhone(username, SHA256Password, verifyWay);

        } else if (!RegexUtil.isEmailInvalid(verifyWay)) {

            userMapper.registerViaEmail(username, SHA256Password, verifyWay);
        } else {
            Result.error(401, "手机号或邮箱格式错误");
        }

    }
}
