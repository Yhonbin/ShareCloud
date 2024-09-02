package com.firefly.sharemount.mapper;

import com.firefly.sharemount.pojo.data.VirtualFolder;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
@CacheNamespace
public interface FilesystemMapper {
    @Select("SELECT * FROM filesystem WHERE id = #{id} AND is_deleted = 0")
    VirtualFolder getById(@Param("id") BigInteger id);

    @Select("SELECT * FROM filesystem WHERE parent = #{parent} AND is_deleted = 0")
    List<VirtualFolder> findChildren(@Param("parent") BigInteger parent);

    @Select("SELECT * FROM filesystem  WHERE parent = #{parent} AND name COLLATE utf8_general_ci = #{name} AND is_deleted = 0 LIMIT 1")
    VirtualFolder findChildByName(@Param("parent") BigInteger parent, @Param("name") String name);

    @Update("UPDATE filesystem SET is_deleted = 1 WHERE id = #{id}")
    void deleteById(@Param("id") BigInteger id);

    @Insert("INSERT INTO filesystem(name, parent) VALUES(#{fileName}, null)")
    void addUserRoot(@Param("fileName") String fileName);

    @Insert("INSERT INTO filesystem(name, parent) VALUES(#{dirName}, #{parent})")
    void mkdir(@Param("parent") BigInteger parent, @Param("dirName") String dirName);

    @Select("SELECT LAST_INSERT_ID()")
    BigInteger getInsertId();
}
