package com.firefly.sharemount.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalStorageAccessor implements StorageAccessor {
    public static String getType() {
        return "LocalStorage";
    }

    public static String getConnectionInfo(JSONObject args) {
        String path = args.getString("root");
        return path == null ? "<Error>" : path;
    }

    public static LocalStorageAccessor createNew(JSONObject args) {
        String path = args.getString("root");
        return path == null ? null : new LocalStorageAccessor(path);
    }

    private final File rootDir;
    private long lastAccessTime;

    private LocalStorageAccessor(String path) {
        rootDir = new File(path);
        lastAccessTime = System.currentTimeMillis();
    }

    @Override
    @SneakyThrows
    public void connect() {
        lastAccessTime = System.currentTimeMillis();
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) throw new IOException("Failed to mkdirs.");
        }
        if (!rootDir.isDirectory()) throw new IOException("Use an absolute file as the storage root directory.");
    }

    @Override
    @SneakyThrows
    public void mkdir(String path, String name) {
        lastAccessTime = System.currentTimeMillis();
        File dir = new File(rootDir, String.join("", path, "/", name));
        if (!dir.mkdirs()) throw new IOException("Failed to mkdirs.");
    }

    @Override
    @SneakyThrows
    public void createEmpty(String path, String name) {
        lastAccessTime = System.currentTimeMillis();
        File newFile = new File(rootDir, String.join("", path, "/", name));
        if (!newFile.createNewFile()) throw new IOException("Failed to mkdirs.");
    }

    @Override
    @SneakyThrows
    public void copy(String source, String dest) {
        lastAccessTime = System.currentTimeMillis();
        Files.copy(Paths.get(source), Paths.get(dest));
    }

    @Override
    @SneakyThrows
    public void move(String source, String dest) {
        lastAccessTime = System.currentTimeMillis();
        Files.move(Paths.get(source), Paths.get(dest));
    }

    private boolean deleteTree(File file) {
        if (!file.exists()) return false;
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) deleteTree(child);
        }
        return file.delete();
    }

    @Override
    @SneakyThrows
    public void delete(String path) {
        lastAccessTime = System.currentTimeMillis();
        if (!deleteTree(new File(path))) throw new IOException("Failed to delete file.");
    }

    @Override
    public boolean exists(String path) {
        lastAccessTime = System.currentTimeMillis();
        File file = new File(rootDir, path);
        return file.exists();
    }

    private FileStatDTO getFileStat(File file) {
        FileStatDTO ret = new FileStatDTO();
        if (!file.exists()) ret.setType("nonexistent");
        else {
            ret.setName(file.getName());
            ret.setLastModified(new Date(file.lastModified()));
            if (file.isDirectory()) ret.setType("dir");
            else {
                ret.setType("file");
                ret.setSize(file.length());
            }
        }
        return ret;
    }

    @Override
    public FileStatDTO getFileStat(String path) {
        lastAccessTime = System.currentTimeMillis();
        return getFileStat(new File(rootDir, path));
    }

    @Override
    @SneakyThrows
    public List<FileStatDTO> listDir(String path) {
        lastAccessTime = System.currentTimeMillis();
        File parent = new File(rootDir, path);
        if (!parent.isDirectory()) return new ArrayList<>();
        File[] children = parent.listFiles();
        if (children == null) return new ArrayList<>();
        ArrayList<FileStatDTO> ret = new ArrayList<>(children.length);
        for (File file : children) ret.add(getFileStat(file));
        return ret;
    }

    @Override
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public void close() {
    }
}
