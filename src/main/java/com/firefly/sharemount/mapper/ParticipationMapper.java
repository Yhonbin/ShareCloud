package com.firefly.sharemount.mapper;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
@CacheNamespace
public interface ParticipationMapper {

    @Insert("INSERT INTO participation(user_id,group_id,privilege) VALUES(#{userId},#{groupId},#{privilege})")
    void addGroup(@Param("userId") BigInteger userId, @Param("groupId") BigInteger groupId, @Param("privilege") Integer privilege);

    @Select("SELECT COUNT(*) FROM participation WHERE user_id = #{userId} AND group_id = #{groupId} AND is_deleted = 0")
    Integer findByGroupId(@Param("userId") BigInteger userId,@Param("groupId") BigInteger groupId);

    @Select("SELECT group_id FROM participation WHERE user_id = #{user_id} AND is_deleted = 0")
    List<BigInteger> findParticipatedGroups(@Param("user_id") BigInteger userId);

    @Update("UPDATE participation SET is_deleted = 1 WHERE user_id = #{userId} AND group_id = #{groupId}")
    void exitGroup(@Param("userId") BigInteger userId, @Param("groupId") BigInteger groupId);


    @Select("SELECT privilege FROM participation WHERE user_id = #{userId} AND group_id = #{groupId} AND is_deleted = 0")
    Integer getPrivilegeById(@Param("userId") BigInteger userId, @Param("groupId") BigInteger groupId);
}
