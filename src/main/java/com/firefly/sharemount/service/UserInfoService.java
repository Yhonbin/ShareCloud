package com.firefly.sharemount.service;

import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;

public interface UserInfoService {

    User register(String username, String email,String phoneNumber, String password);

    User createGroup(BigInteger userId, String groupName);

    UserInfo findByUserPhoneNumber(String loginName);

    UserInfo findByUserEmail(String loginName);
    UserInfo findByUserId(BigInteger userId);

}
