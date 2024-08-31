package com.firefly.sharemount.mapper;


import com.firefly.sharemount.pojo.data.User;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id} AND is_deleted = 0")
    User getById(@Param("id") BigInteger id);

    @Select("SELECT * FROM user WHERE name = #{username} AND is_deleted = 0")
    User findByName(@Param("username") String username);

    @Insert("INSERT INTO user(name, root)" +
            "VALUES(#{username},#{root})")
    void addUser(@Param("username") String username,@Param("root") BigInteger root);

    @Select("SELECT LAST_INSERT_ID()")
    BigInteger getInsertId();
}
