package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.mapper.*;
import com.firefly.sharemount.pojo.data.*;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import com.firefly.sharemount.service.FileService;
import com.firefly.sharemount.service.StorageService;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.PathUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private UserService userService;

    @Resource
    private StorageService storageService;

    @Override
    public FileBO findFileBO(BigInteger id, String path) {
        return findFileBO(userMapper.getById(id), path);
    }

    @Override
    public FileBO findFileBO(User user, String strPath) {
        Deque<String> path = PathUtil.pathToQueue(strPath);
        return findFileBO(user, path);
    }

    @Override
    public FileStatDTO getStat(FileBO file) {
        if (file.getSymbolicLink() != null) {
            FileStatDTO ret = new FileStatDTO();
            ret.setType("link");
            ret.setLinkTargetUser(userService.getUserDTO(file.getLinkOwner()));
            String targetPath = symbolicLinkMapper.getTargetPathById(file.getSymbolicLink().getId());
            ret.setLinkTarget(targetPath);
            ret.setLinkTargetStat(getStat(findFileBO(file.getLinkOwner(), PathUtil.pathToQueue(targetPath))));
            return ret;
        }
        FileStatDTO ret = null;
        StorageAccessor storage = file.getStorage() == null ? null : storageService.getConnection(file.getStorage().getId());
        if (storage != null) {
            ret = storage.getFileStat(String.join("/", file.getStorageRestPath()));
            ret.setMount(storageService.getStorageStat(file.getStorage()));
        }
        if (ret == null) {
            ret = new FileStatDTO();
            ret.setType("nonexistent");
        }
        if (file.getVfRestPath().isEmpty() && Objects.equals(ret.getType(), "nonexistent")) ret.setType("vdir");
        return ret;
    }

    @Override
    public List<FileStatDTO> listDir(FileBO file) {
        LinkedList<FileStatDTO> ret = new LinkedList<>();
        try {
            StorageAccessor storage = file.getStorage() == null ? null : storageService.getConnection(file.getStorage().getId());
            if (storage != null) {
                ret.addAll(storage.listDir(String.join("/", file.getStorageRestPath())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file.getVfRestPath().isEmpty()) {
            List<VirtualFolder> children = fsMapper.findChildren(file.getVirtualFolder().getId());
            for (VirtualFolder child : children) {
                file.getVfRestPath().offerLast(child.getName());
                ret.add(getStat(findFileBO(file.getVfOwner(), file.getVfRestPath())));
                file.getVfRestPath().pollLast();
            }
        }
        return ret;
    }

    @Override
    public void mkdir(FileBO file, Boolean virtual) {
        // TODO 事务
        if (file.getStorage() == null || virtual) {
            BigInteger parent = file.getVirtualFolder().getId();
            for (String p : file.getVfRestPath()) {
                fsMapper.mkdir(parent, p);
                parent = fsMapper.getInsertId();
            }
        } else {
            if (file.getStorageRestPath().isEmpty()) return;
            StorageAccessor storage = storageService.getConnection(file.getStorage().getId());
            String name = file.getStorageRestPath().pollLast();
            storage.mkdir(String.join("/", file.getStorageRestPath()), name);
            file.getStorageRestPath().offerLast(name);
        }
    }

    @Override
    public void createEmpty(FileBO file) {

    }

    @Override
    public void copy(FileBO source, FileBO dest) {

    }

    @Override
    public void move(FileBO source, FileBO dest) {

    }

    @Override
    public void delete(FileBO file) {

    }

    @Override
    public void mountOn(FileBO file, BigInteger storageId) {
        // 挂载
    }

    @Override
    public void unmountOn(FileBO file) {

    }

    @Override
    public void createSymbolicLink(FileBO file, User targetUser, String path) {

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
                boolean isSelfSymbolicLink = path.isEmpty();
                String target = symbolicLinkMapper.getTargetPathById(tryLink.getId());
                Deque<String> nextPath = PathUtil.pathToQueue(target);
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
}
