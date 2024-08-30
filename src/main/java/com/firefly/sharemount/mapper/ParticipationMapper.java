package com.firefly.sharemount.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Mapper
public interface ParticipationMapper {

    @Insert("INSERT INTO participation(user_id,group_id) VALUES(#{userId},#{groupId})")
    void addGroup(@Param("userId") BigInteger userId, @Param("groupId") BigInteger groupId);
}
