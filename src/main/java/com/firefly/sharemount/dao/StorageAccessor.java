package com.firefly.sharemount.dao;

import com.firefly.sharemount.pojo.dto.FileStatDTO;

import java.util.List;

public interface StorageAccessor {
    void connect();

    void mkdir(String path, String name);

    void createEmpty(String path, String name);

    void copy(String source, String dest);

    void move(String source, String dest);

    void delete(String path);

    boolean exists(String path);

    FileStatDTO getFileStat(String path);

    List<FileStatDTO> listDir(String path);

    long getLastAccessTime();

    void close();
}
