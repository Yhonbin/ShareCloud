package com.firefly.sharemount.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface MountMapper {
    @Select("SELECT storage_id FROM mount LIMIT 1 WHERE path = #{id} AND is_deleted = 0")
    BigInteger findByPathId(@Param("id") BigInteger id);

    @Update("UPDATE mount SET is_deleted = 1 WHERE path = #{id}")
    void deleteByPathId(@Param("id") BigInteger id);

    @Select("SELECT path FROM mount WHERE storage_id = #{id} AND is_deleted = 0")
    List<BigInteger> findMountPointsByStorageId(@Param("id") BigInteger id);
}
