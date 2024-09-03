package com.firefly.sharemount.dao.storage;

import com.firefly.sharemount.exception.BadConnectionToStorageException;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

public class StorageAccessorRetryProxy implements StorageAccessor {
    private final StorageAccessor accessor;
    private final int retryTimes;

    public StorageAccessorRetryProxy(StorageAccessor accessor, int retryTimes) {
        this.accessor = accessor;
        this.retryTimes = retryTimes;
    }

    @Override
    public void connect() throws BadConnectionToStorageException {
        accessor.connect();
    }

    @Override
    public void mkdir(String path, String name) throws FileAlreadyExistsException, BadConnectionToStorageException {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.mkdir(path, name);
                break;
            } catch (BadConnectionToStorageException e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.mkdir(path, name);
    }

    @Override
    public void createEmpty(String path, String name) throws FileAlreadyExistsException, BadConnectionToStorageException {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.createEmpty(path, name);
                break;
            } catch (BadConnectionToStorageException e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.createEmpty(path, name);

    }

    @Override
    public void copy(String source, String dest) throws IOException, BadConnectionToStorageException {
        for (int i = 0; i < retryTimes; i++) {
            try {
                accessor.copy(source, dest);
                break;
            } catch (BadConnectionToStorageException e) {
                e.printStackTrace();
                accessor.close();
                accessor.connect();
            }
        }
        accessor.copy(source, dest);
    }

    @Override
    public void move(String source, String dest) throws IOException, BadConnectionToStorageException {
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
    public void upload(String path, String name, MultipartFile srcFile) throws IOException {
        accessor.upload(path, name, srcFile);
    }

    @Override
    public void download(String path, String name, OutputStream os) throws IOException {
        accessor.download(path, name, os);
    }

    @Override
    public void delete(String path) throws FileNotFoundException, BadConnectionToStorageException {
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
    public boolean exists(String path) throws BadConnectionToStorageException {
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
    public FileStatDTO getFileStat(String path) throws BadConnectionToStorageException {
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
    public List<FileStatDTO> listDir(String path) throws FileNotFoundException, BadConnectionToStorageException {
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
