package com.firefly.sharemount.mapper;

import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
@CacheNamespace
public interface MountMapper {
    @Select("SELECT storage_id FROM mount WHERE path = #{id} AND is_deleted = 0 LIMIT 1")
    BigInteger findByPathId(@Param("id") BigInteger id);

    @Update("UPDATE mount SET is_deleted = 1 WHERE path = #{id}")
    void deleteByPathId(@Param("id") BigInteger id);

    @Select("SELECT path FROM mount WHERE storage_id = #{id} AND is_deleted = 0")
    List<BigInteger> findMountPointsByStorageId(@Param("id") BigInteger id);

    @Insert("INSERT INTO mount(path,storage_id) VALUES (#{path}, #{storageId})")
    void insertMount(@Param("path") BigInteger path, @Param("storageId") BigInteger storageId);

    @Update("UPDATE mount SET is_deleted = 1 WHERE storage_id = #{storageId}")
    void deleteStorageFromMount(@Param("storageId") BigInteger storageId);
}
