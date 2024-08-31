package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.mapper.*;
import com.firefly.sharemount.pojo.data.*;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import com.firefly.sharemount.service.FileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Deque;
import java.util.LinkedList;

@Service
public class FileServiceImpl implements FileService {
    @Resource
    private FilesystemMapper fsMapper;

    @Resource
    private StorageMapper storageMapper;

    @Resource
    private MountMapper mountMapper;

    @Resource
    private SymbolicLinkMapper symbolicLinkMapper;

    @Resource
    private UserMapper userMapper;

    private Deque<String> pathArrayToQueue(String[] path) {
        LinkedList<String> ret = new LinkedList<>();
        if (path.length == 0 || (path.length == 1 && path[0].isEmpty())) return ret;
        for (String p : path) ret.offerLast(p);
        return ret;
    }

    private VirtualFolder findLastVirtualFolder(VirtualFolder folder, Deque<String> path) {
        if (path.isEmpty()) return folder;
        String nextName = path.pollFirst();
        if (path.isEmpty() && nextName.isEmpty()) return folder;
        do {
            VirtualFolder nextFolder = fsMapper.findChildByName(folder.getId(), nextName);
            if (nextFolder == null) return folder;
            folder = nextFolder;
            nextName = path.pollFirst();
        } while (!path.isEmpty());
        VirtualFolder fin = fsMapper.findChildByName(folder.getId(), nextName);
        return fin == null ? folder : fin;
    }

    private BigInteger findLastStorageId(VirtualFolder folder, Deque<String> path) {
        while (folder.getParent() != null) {
            BigInteger storageId = mountMapper.findByPathId(folder.getId());
            if (storageId != null) return storageId;
            path.offerFirst(folder.getName());
            folder = fsMapper.getById(folder.getParent());
        }
        return mountMapper.findByPathId(folder.getId());
    }

    public FileBO findFileBO(User user, Deque<String> path) {
        VirtualFolder folder = fsMapper.getById(user.getRoot());
        folder = findLastVirtualFolder(folder, path);
        if (!path.isEmpty()) {
            SymbolicLink tryLink = symbolicLinkMapper.findByParentPathAndName(folder.getId(), path.peekFirst());
            if (tryLink != null) {
                path.pollFirst();
                User targetUser = userMapper.getById(tryLink.getTargetUser());
                if (targetUser == null)
                    return path.isEmpty() ? FileBO.makeNewInvalidSymbolicLink(tryLink, user) : null;
                String target = symbolicLinkMapper.getTargetPathById(tryLink.getId());
                boolean isSelfSymbolicLink = path.isEmpty();
                Deque<String> nextPath = pathArrayToQueue(target.split("/"));
                while (!path.isEmpty()) nextPath.offerLast(path.pollFirst());
                FileBO linked = findFileBO(targetUser, nextPath);
                if (linked == null)
                    return isSelfSymbolicLink ? FileBO.makeNewInvalidSymbolicLink(tryLink, user) : null;
                if (isSelfSymbolicLink) {
                    linked.setSymbolicLink(tryLink);
                    linked.setLinkOwner(user);
                }
                return linked;
            }
        }
        Deque<String> sPath = new LinkedList<>(path);
        BigInteger storageId = findLastStorageId(folder, sPath);
        Storage storage = storageId == null ? null : storageMapper.getById(storageId);
        sPath = storage == null ? null : sPath;
        return new FileBO(storage, sPath, folder, path, user, null, null);
    }

    @Override
    public FileBO findFileBO(User user, String[] strPath) {
        Deque<String> path = pathArrayToQueue(strPath);
        return findFileBO(user, path);
    }

    @Override
    public FileStatDTO getStat(FileBO file) {
        return null;
    }

    @Override
    public void mkdir(FileBO file) {

    }

    @Override
    public void createEmpty(FileBO file) {

    }

    @Override
    public void delete(FileBO file) {

    }

    @Override
    public void mountOn(FileBO file) {

    }

    @Override
    public void unmountOn(FileBO file) {

    }

    @Override
    public void createSymbolicLink(FileBO file, User targetUser, String path) {

    }
}
