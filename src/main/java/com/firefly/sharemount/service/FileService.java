package com.firefly.sharemount.service;

import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.User;
import com.firefly.sharemount.pojo.dto.FileStatDTO;

public interface FileService {
    FileBO findFileBO(User user, String[] path);

    FileStatDTO getStat(FileBO file);

    void mkdir(FileBO file);

    void createEmpty(FileBO file);

    void delete(FileBO file);

    void mountOn(FileBO file);

    void unmountOn(FileBO file);

    void createSymbolicLink(FileBO file, User targetUser, String path);
}
