package com.firefly.sharemount.mapper;

import com.firefly.sharemount.pojo.data.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;

@Mapper
public interface UserInfoMapper {
    @Select("SELECT * FROM user_info WHERE email = #{email} AND is_deleted = 0")
    UserInfo findByUserEmail(@Param("email") String email);

    @Select("SELECT * FROM user_info WHERE phone_number = #{phoneNumber} AND is_deleted = 0")
    UserInfo findByUserPhoneNumber(@Param("phoneNumber") String phoneNumber);
    @Select("SELECT * FROM user_info WHERE user_id = #{userId} AND is_deleted = 0")
    UserInfo findByUserId(@Param("userId") BigInteger userId);

    @Insert("INSERT INTO user_info(user_id, password, email, phone_number)" +
            "VALUES (#{userId},#{Sha256Password}, #{email}, #{phoneNumber})")
    void addUserInfo(@Param("userId") BigInteger userId, @Param("Sha256Password") String Sha256Password,
                     @Param("email") String email, @Param("phoneNumber") String phoneNumber);
}
