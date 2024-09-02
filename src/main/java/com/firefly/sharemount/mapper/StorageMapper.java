package com.firefly.sharemount.mapper;

import com.firefly.sharemount.pojo.data.Storage;
import com.firefly.sharemount.pojo.data.StorageConnectionLog;
import com.firefly.sharemount.pojo.dto.StorageDTO;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
@CacheNamespace
public interface StorageMapper {
    @Select("SELECT * FROM storage WHERE id = #{id} AND is_deleted = 0")
    Storage getById(@Param("id") BigInteger id);

    @Select("SELECT si.interface FROM storage_interface si " +
            "LEFT JOIN storage s ON si.id = #{id} AND s.id = #{id} " +
            "WHERE s.is_deleted = 0")
    String getInterfaceById(@Param("id") BigInteger id);

    @Select("SELECT sl.* FROM storage_log sl " +
            "LEFT JOIN storage s ON sl.id = #{id} AND s.id = #{id} " +
            "WHERE s.is_deleted = 0")
    StorageConnectionLog getLogById(@Param("id") BigInteger id);


    @Insert("INSERT INTO storage(owner,name, occupation, readonly)" +
            "VALUES (#{owner},#{name},#{occupation}, #{readonly})")
    void uploadStorage(StorageDTO storageDto);

    @Select("SELECT LAST_INSERT_ID()")
    BigInteger getInsertId();

    @Insert("INSERT INTO storage_interface(id, interface) VALUES (#{id},#{storageInterface})")
    void uploadStorageInterface(@Param("id") BigInteger id,@Param("storageDto") StorageDTO storageDto);

    @Update("UPDATE storage SET owner = #{groupId} WHERE is_deleted = 0 AND owner = #{owner}")
    void transferToGroup(@Param("owner") BigInteger owner, @Param("groupId") BigInteger groupId);

    @Insert("INSERT INTO storage_log(id,success, log) VALUES (#{id},#{success}, #{log})")
    void uploadStorageLog(@Param("id") BigInteger id, @Param("success") boolean success, @Param("log") String log);
}
