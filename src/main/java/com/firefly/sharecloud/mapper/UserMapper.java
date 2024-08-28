package com.firefly.sharecloud.mapper;


import com.firefly.sharecloud.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUserName(@Param("username") String username);

    @Insert("INSERT INTO user(username, password, phone_number,create_time, update_time, is_delete) " +
            "VALUES(#{username},#{SHA256Password},#{verifyWay}, now(), now(), 0)")
    void registerViaPhone(@Param("username")String username, @Param("SHA256Password")String SHA256Password, @Param("verifyWay") String verifyWay);

    @Insert("INSERT INTO user(username, password, email,create_time, update_time, is_delete) " +
            "VALUES(#{username},#{SHA256Password},#{verifyWay}, now(), now(), 0)")
    void registerViaEmail(@Param("username")String username, @Param("SHA256Password")String SHA256Password, @Param("verifyWay") String verifyWay);
}
