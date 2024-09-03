package com.firefly.sharemount.dao.storage;

import com.firefly.sharemount.exception.BadConnectionToStorageException;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

public interface StorageAccessor {
    void connect() throws BadConnectionToStorageException;

    void mkdir(String path, String name) throws FileAlreadyExistsException, BadConnectionToStorageException;

    void createEmpty(String path, String name) throws FileAlreadyExistsException, BadConnectionToStorageException;

    void copy(String source, String dest) throws IOException, BadConnectionToStorageException;

    void move(String source, String dest) throws IOException, BadConnectionToStorageException;

    void upload(String path, String name, MultipartFile srcFile) throws IOException;

    void download(String path, String name, OutputStream os) throws IOException;

    void delete(String path) throws FileNotFoundException, BadConnectionToStorageException;

    boolean exists(String path) throws BadConnectionToStorageException;

    FileStatDTO getFileStat(String path) throws BadConnectionToStorageException;

    List<FileStatDTO> listDir(String path) throws FileNotFoundException, BadConnectionToStorageException;

    long getLastAccessTime();

    void close();
}
