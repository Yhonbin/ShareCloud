package com.firefly.sharemount.service;

import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;

import java.math.BigInteger;


public interface UserService {


    User findByName(String name);
}
