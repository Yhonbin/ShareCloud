package com.firefly.sharemount.mapper;

import com.firefly.sharemount.pojo.data.SymbolicLink;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
@CacheNamespace
public interface SymbolicLinkMapper {
    @Select("SELECT * FROM symbolic_link WHERE id = #{id} AND is_deleted = 0")
    SymbolicLink getById(@Param("id") BigInteger id);

    @Select("SELECT * FROM symbolic_link WHERE parent = #{parent} AND name COLLATE utf8_general_ci = #{name} AND is_deleted = 0 LIMIT 1")
    SymbolicLink findByParentPathAndName(@Param("parent") BigInteger parent, @Param("name") String name);

    @Select("SELECT * FROM symbolic_link WHERE parent = #{parent} AND is_deleted = 0")
    List<SymbolicLink> findAllByParentPath(@Param("parent") BigInteger parent);

    @Select("SELECT sltp.path FROM symbolic_link_target_path sltp " +
            "LEFT JOIN symbolic_link sl ON sl.id = #{id} AND sltp.id = #{id} " +
            "WHERE sl.is_deleted = 0")
    String getTargetPathById(@Param("id") BigInteger id);

    @Update("UPDATE symbolic_link SET is_deleted = 1 WHERE parent = #{id}")
    void deleteAllChildrenSymbolicLinkByPathId(@Param("id") BigInteger id);

    @Update("UPDATE symbolic_link SET is_deleted = 1 WHERE id = #{id}")
    void deleteById(@Param("id") BigInteger id);
}
