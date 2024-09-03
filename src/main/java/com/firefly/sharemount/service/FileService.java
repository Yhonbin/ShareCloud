package com.firefly.sharemount.service;

import com.firefly.sharemount.exception.FileAlreadyExistsException;
import com.firefly.sharemount.exception.FileNotExistsException;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.dto.FileStatDTO;

import java.math.BigInteger;
import java.util.List;

public interface FileService {
    FileBO findFileBO(BigInteger id, String path);

    FileBO findFileBO(User user, String path);

    void mkdir(FileBO file, Boolean virtual) throws FileAlreadyExistsException;

    void createEmpty(FileBO file);

    void copy(FileBO source, FileBO dest);

    void move(FileBO source, FileBO dest);

    void delete(FileBO file) throws FileNotExistsException;

    FileStatDTO getStat(FileBO file);

    List<FileStatDTO> listDir(FileBO file, BigInteger ignoreStorageId);

    void mountOn(FileBO file, BigInteger storageId);

    void unmountOn(FileBO file) throws FileNotExistsException;

    void createSymbolicLink(FileBO file, User targetUser, String path);
}
