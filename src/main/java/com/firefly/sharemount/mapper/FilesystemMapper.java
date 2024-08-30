package com.firefly.sharemount.mapper;

import org.apache.ibatis.annotations.*;

import java.math.BigInteger;

@Mapper
public interface FilesystemMapper {

    @Insert("INSERT INTO filesystem(name, parent) "+
             "VALUES(#{fileName},null)")
    void addFilesystem(@Param("fileName") String fileName);

    @Select("SELECT LAST_INSERT_ID()")
    BigInteger getInsertId();
}
