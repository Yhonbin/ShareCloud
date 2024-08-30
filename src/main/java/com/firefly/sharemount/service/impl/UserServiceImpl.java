package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.mapper.UserMapper;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
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

}
