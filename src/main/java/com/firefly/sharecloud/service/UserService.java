package com.firefly.sharecloud.service;

import com.firefly.sharecloud.pojo.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;


public interface UserService {


    User findByUserName(String userName);

    void register(String userName, String password, String verifyWay);


}
