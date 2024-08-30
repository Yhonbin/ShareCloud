package com.firefly.sharemount.mapper;


import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.data.UserInfo;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByName(@Param("username") String username);

    @Select("SELECT * FROM user_info WHERE email = #{email}")
    UserInfo findByUserEmail(@Param("email") String email);

    @Select("SELECT * FROM user_info WHERE phone_number = #{phoneNumber}")
    UserInfo findByUserPhoneNumber(@Param("phoneNumber") String phoneNumber);
    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    UserInfo findByUserId(@Param("userId") BigInteger userId);

    @Insert("INSERT INTO user(name,create_time, update_time, is_deleted)" +
            "VALUES(#{username},now(),now(),0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    BigInteger addUser(@Param("username") String username);

    @Insert("INSERT INTO user(name,create_time, update_time, is_deleted)" +
            "VALUES(#{groupName},now(),now(),0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    BigInteger addGroup(@Param("groupName") String groupName);

    @Insert("INSERT INTO user_info(user_id, password, email,phone_number,allocated,create_time, update_time, is_deleted) " +
            "VALUES(#{userId},#{password},#{email},#{phoneNumber},#{allocated}, now(), now(), 0)")
    void register(@Param("UserInfo") UserInfo userInfo);


}
