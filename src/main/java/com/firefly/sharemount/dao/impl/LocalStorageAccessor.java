package com.firefly.sharemount.dao.impl;

import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LocalStorageAccessor implements StorageAccessor {
    private final File rootDir;

    private LocalStorageAccessor(String path) {
        rootDir = new File(path);
    }

    @Override
    @SneakyThrows
    public void connect() {
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) throw new IOException("Failed to mkdirs.");
        }
        if (rootDir.isFile()) throw new IOException("Use an absolute file as the storage root directory.");
    }

    @Override
    @SneakyThrows
    public void mkdir(String path, String name) {
        File dir = new File(rootDir, String.join("", path, "/", name));
        if (!dir.mkdirs()) throw new IOException("Failed to mkdirs.");
    }

    @Override
    @SneakyThrows
    public void createEmpty(String path, String name) {
        File newFile = new File(rootDir, String.join("", path, "/", name));
        if (!newFile.createNewFile()) throw new IOException("Failed to mkdirs.");
    }

    @Override
    public void rename(String loc, String source, String dest) {

    }

    @Override
    public void copy(String source, String dest) {

    }

    @Override
    public void move(String source, String dest) {

    }

    @Override
    public void delete(String path) {

    }

    @Override
    public FileStatDTO getFileStat(String path) {
        return null;
    }

    @Override
    public List<FileStatDTO> listDir(String path) {
        return null;
    }

    @Override
    public long getLastAccessTime() {
        return 0;
    }

    @Override
    public void close() {

    }
}
