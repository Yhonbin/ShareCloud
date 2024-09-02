package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.component.RedisTemplateComponent;
import com.firefly.sharemount.config.ApplicationConfiguration;
import com.firefly.sharemount.mapper.UserInfoMapper;
import com.firefly.sharemount.mapper.UserMapper;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
import com.firefly.sharemount.pojo.dto.UserDTO;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.JwtUtil;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private RedisTemplateComponent redisTemplateComponent;

    @Override
    public User findByName(String username) {
        return userMapper.findByName(username);
    }

    @Override
    public UserDTO getUserDTO(User user) {
        UserDTO ret = new UserDTO();
        ret.setId(user.getId());
        ret.setName(user.getName());
        ret.setIsGroup(userInfoMapper.findByUserId(user.getId()) == null);
        return ret;
    }

    @Override
    public UserDTO getUserDTO(BigInteger id) {
        return getUserDTO(userMapper.getById(id));
    }

    @Override
    public boolean isGroup(BigInteger userId) {
        UserInfo userInfo = userInfoMapper.findByUserId(userId);
        return userInfo == null;
    }

    @Override
    public BigInteger getUserId(HttpServletRequest request) {
        return JwtUtil.getUserId(request.getHeader("Authorization"));
    }


}
