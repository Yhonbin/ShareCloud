package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.config.ApplicationConfiguration;
import com.firefly.sharemount.mapper.FilesystemMapper;
import com.firefly.sharemount.mapper.ParticipationMapper;
import com.firefly.sharemount.mapper.UserInfoMapper;
import com.firefly.sharemount.mapper.UserMapper;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
import com.firefly.sharemount.service.UserInfoService;
import com.firefly.sharemount.utils.Sha256Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;


@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private FilesystemMapper filesystemMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private ParticipationMapper participationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ApplicationConfiguration applicationConfiguration;

    @Override
    @Transactional
    public User register(String username, String email,String phoneNumber, String password) {
        // 在filesystem中插入新项
        String fileName = "";
        filesystemMapper.addUserRoot(fileName);
        BigInteger root = filesystemMapper.getInsertId();

        // 在user中插入新项，设root=filesystem新项主键
        userMapper.addUser(username,root);
        BigInteger userId = userMapper.getInsertId();


        // 在user_info中插入新项，设user_id=user新项主键
        applicationConfiguration.loadConfig();
        Integer allocated = (Integer) applicationConfiguration.getNestedConfig("cloud-drive.default-allocation");
        String Sha256Password = Sha256Util.getSHA256StrJava(password);
        userInfoMapper.addUserInfo(userId, Sha256Password,email,phoneNumber,allocated);


        return new User(userId,username,root);
    }

    @Override
    @Transactional
    public User createGroup(BigInteger userId, String groupName) {
        // 在filesystem中插入新项
        String fileName = "";
        filesystemMapper.addUserRoot(fileName);
        BigInteger root = filesystemMapper.getInsertId();

        // 在user中插入新项，设root=filesystem新项主键
        userMapper.addUser(groupName,root);
        BigInteger groupId = userMapper.getInsertId();

        // 在participation中插入新项，设user_id=用户ID，group_id=user新项主键
        Integer privilege = 1;
        participationMapper.addGroup(userId, groupId,privilege);

        return new User(groupId,groupName,root);
    }

    @Transactional
    public void joinGroup(BigInteger userId, BigInteger groupId) {
        // 在participation中插入新项，设user_id=用户ID，group_id=user新项主键
        Integer privilege = 0;
        participationMapper.addGroup(userId,groupId,privilege);
    }


    @Override
    public UserInfo findByUserPhoneNumber(String loginName) {
        return userInfoMapper.findByUserPhoneNumber(loginName);
    }

    @Override
    public UserInfo findByUserEmail(String loginName) {
        return userInfoMapper.findByUserEmail(loginName);
    }

    @Override
    public UserInfo findByUserId(BigInteger userId) {
        return userInfoMapper.findByUserId(userId);
    }


}
