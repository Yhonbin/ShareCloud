package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.mapper.*;
import com.firefly.sharemount.pojo.data.*;
import com.firefly.sharemount.service.FileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;

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

    private Queue<String> pathArrayToQueue(String[] path) {
        LinkedList<String> ret = new LinkedList<>();
        if (path.length == 0 || (path.length == 1 && path[0].isEmpty())) return ret;
        for (String p : path) ret.offer(p);
        return ret;
    }

    private VirtualFolder findLastVirtualFolder(VirtualFolder folder, Queue<String> path) {
        if (path.isEmpty()) return folder;
        String nextName = path.poll();
        if (path.isEmpty() && nextName.isEmpty()) return folder;
        do {
            VirtualFolder nextFolder = fsMapper.findChildByName(folder.getId(), nextName);
            if (nextFolder == null) return folder;
            folder = nextFolder;
            nextName = path.poll();
        } while (!path.isEmpty());
        VirtualFolder fin = fsMapper.findChildByName(folder.getId(), nextName);
        return fin == null ? folder : fin;
    }

    private BigInteger findLastStorageId(VirtualFolder folder) {
        while (folder.getParent() != null) {
            BigInteger storageId = mountMapper.findByPathId(folder.getId());
            if (storageId != null) return storageId;
            folder = fsMapper.getById(folder.getParent());
        }
        return mountMapper.findByPathId(folder.getId());
    }

    @Override
    public FileBO findFileBO(User user, String[] strPath) {
        Queue<String> path = pathArrayToQueue(strPath);
        return findFileBO(user, path);
    }

    public FileBO findFileBO(User user, Queue<String> path) {
        if (user == null) return null;
        VirtualFolder folder = fsMapper.getById(user.getRoot());
        if (folder == null) return null;
        folder = findLastVirtualFolder(folder, path);
        if (path.isEmpty()) {
            BigInteger storageId = findLastStorageId(folder);
            Storage storage = storageId == null ? null : storageMapper.getById(storageId);
            return new FileBO(storage, folder, user, null, null, path);
        }
        SymbolicLink tryLink = symbolicLinkMapper.findByParentPathAndName(folder.getId(), path.peek());
        if (tryLink != null) {
            path.poll();
            User targetUser = userMapper.getById(tryLink.getTargetUser());
            if (targetUser == null)
                return path.isEmpty() ? new FileBO(null, null, null, tryLink, user, path) : null;
            String target = symbolicLinkMapper.getTargetPathById(tryLink.getId());
            boolean isSelfSymbolicLink = path.isEmpty();
            Queue<String> nextPath = pathArrayToQueue(target.split("/"));
            while (!path.isEmpty()) nextPath.offer(path.poll());
            FileBO linked = findFileBO(targetUser, nextPath);
            if (linked == null)
                return isSelfSymbolicLink ? new FileBO(null, null, null, tryLink, user, path) : null;
            if (isSelfSymbolicLink) {
                linked.setSymbolicLink(tryLink);
                linked.setLinkOwner(user);
            }
            return linked;
        }
        BigInteger storageId = findLastStorageId(folder);
        Storage storage = storageId == null ? null : storageMapper.getById(storageId);
        if (storage == null) return null;
        return new FileBO(storage, null, null, null, null, path);
    }

    @Override
    public void deleteFolder(String[] path) {

    }
}
