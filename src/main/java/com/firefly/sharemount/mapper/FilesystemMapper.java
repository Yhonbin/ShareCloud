package com.firefly.sharemount.mapper;

import com.firefly.sharemount.pojo.data.VirtualFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface FilesystemMapper {
    @Select("SELECT * FROM filesystem WHERE id = #{id} AND is_deleted = 0")
    VirtualFolder getById(@Param("id") BigInteger id);

    @Select("SELECT * FROM filesystem WHERE parent = #{parent} AND is_deleted = 0")
    List<VirtualFolder> findChildren(@Param("parent") BigInteger parent);

    @Select("SELECT * FROM filesystem LIMIT 1 WHERE parent = #{parent} AND name COLLATE utf8_general_ci = #{name} AND is_deleted = 0")
    VirtualFolder findChildByName(@Param("parent") BigInteger parent, @Param("name") String name);

    @Update("UPDATE filesystem SET is_deleted = 1 WHERE id = #{id}")
    void deleteById(@Param("id") BigInteger id);
}
