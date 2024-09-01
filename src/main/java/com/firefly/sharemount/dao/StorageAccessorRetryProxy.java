package com.firefly.sharemount.dao;

import com.firefly.sharemount.pojo.dto.FileStatDTO;

import java.util.List;

public class StorageAccessorRetryProxy implements StorageAccessor {
    private final StorageAccessor accessor;
    private final int retryTimes;

    public StorageAccessorRetryProxy(StorageAccessor accessor, int retryTimes) {
        this.accessor = accessor;
        this.retryTimes = retryTimes;
    }

    @Override
    public void connect() {
        accessor.connect();
    }

    @Override
    public void mkdir(String path, String name) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.mkdir(path, name);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.mkdir(path, name);
    }

    @Override
    public void createEmpty(String path, String name) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.createEmpty(path, name);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.createEmpty(path, name);

    }

    @Override
    public void copy(String source, String dest) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.copy(source, dest);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.copy(source, dest);
    }

    @Override
    public void move(String source, String dest) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.move(source, dest);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.move(source, dest);
    }

    @Override
    public void delete(String path) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.delete(path);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.delete(path);
    }

    @Override
    public boolean exists(String path) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                return accessor.exists(path);
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        return accessor.exists(path);
    }

    @Override
    public FileStatDTO getFileStat(String path) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                return accessor.getFileStat(path);
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        return accessor.getFileStat(path);
    }

    @Override
    public List<FileStatDTO> listDir(String path) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                return accessor.listDir(path);
            } catch (Exception e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        return accessor.listDir(path);
    }

    @Override
    public long getLastAccessTime() {
        return accessor.getLastAccessTime();
    }

    @Override
    public void close() {
        accessor.close();
    }
}
