package com.firefly.sharemount.service;

import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.User;

public interface FileService {
    FileBO findFileBO(User user, String[] path);

    void deleteFolder(String[] path);
}
