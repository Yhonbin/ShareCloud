package com.firefly.sharemount.mapper;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;

@Mapper
@CacheNamespace
public interface ParticipationMapper {

    @Insert("INSERT INTO participation(user_id,group_id,privilege) VALUES(#{userId},#{groupId},#{privilege})")
    void addGroup(@Param("userId") BigInteger userId, @Param("groupId") BigInteger groupId, @Param("privilege") Integer privilege);

    @Select("SELECT COUNT(*) FROM participation WHERE user_id = #{userId} AND group_id = #{groupId} AND is_deleted = 0")
    Integer findByGroupId(@Param("userId") BigInteger userId,@Param("groupId") BigInteger groupId);


    @Update("UPDATE participation SET is_deleted = 1 WHERE user_id = #{userId} AND group_id = #{groupId}")
    void exitGroup(@Param("userId") BigInteger userId, @Param("groupId") BigInteger groupId);
}
