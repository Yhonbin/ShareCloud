package com.firefly.sharemount.service;

import com.firefly.sharemount.exception.BadConnectionToStorageException;
import com.firefly.sharemount.exception.WriteToVirtualFolderNotAllowedException;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

public interface FileService {
    FileBO findFileBO(BigInteger id, String path);

    FileBO findFileBO(User user, String path);

    void mkdir(FileBO file, Boolean virtual) throws FileAlreadyExistsException, BadConnectionToStorageException;

    void createEmpty(FileBO file);

    void copy(FileBO source, FileBO dest);

    void move(FileBO source, FileBO dest);

    void upload(FileBO dest, MultipartFile srcFile) throws WriteToVirtualFolderNotAllowedException, IOException, BadConnectionToStorageException;

    void download(FileBO source, OutputStream os) throws BadConnectionToStorageException, IOException;

    void delete(FileBO file) throws FileNotFoundException, BadConnectionToStorageException;

    FileStatDTO getStat(FileBO file) throws BadConnectionToStorageException;

    List<FileStatDTO> listDir(FileBO file, BigInteger ignoreStorageId) throws BadConnectionToStorageException, FileNotFoundException;

    void mountOn(FileBO file, BigInteger storageId);

    void unmountOn(FileBO file) throws FileNotExistsException;

    void createSymbolicLink(FileBO file, User targetUser, String path);
}
