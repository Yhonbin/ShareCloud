package com.firefly.sharemount.mapper;

import com.firefly.sharemount.pojo.data.Storage;
import com.firefly.sharemount.pojo.data.StorageConnectionLog;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
