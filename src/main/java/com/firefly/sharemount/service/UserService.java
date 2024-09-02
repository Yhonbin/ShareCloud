package com.firefly.sharemount.service;

import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
import com.firefly.sharemount.pojo.dto.UserDTO;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;


public interface UserService {
    User findByName(String name);

    UserDTO getUserDTO(User user);

    UserDTO getUserDTO(BigInteger id);

    boolean isGroup(BigInteger userId);

    BigInteger getUserId(HttpSession httpSession);

    String getUuid(HttpSession httpSession);
}
