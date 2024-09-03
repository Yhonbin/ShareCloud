package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.dao.storage.StorageAccessor;
import com.firefly.sharemount.exception.BadConnectionToStorageException;
import com.firefly.sharemount.exception.WriteToVirtualFolderNotAllowedException;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.mapper.*;
import com.firefly.sharemount.pojo.data.*;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import com.firefly.sharemount.pojo.dto.StorageStatDTO;
import com.firefly.sharemount.service.FileService;
import com.firefly.sharemount.service.StorageService;
import com.firefly.sharemount.service.UserService;
import com.firefly.sharemount.utils.PathUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;

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
    public FileStatDTO getStat(FileBO file) throws BadConnectionToStorageException {
        return getStat(file, false);
    }

    public FileStatDTO getStat(FileBO file, boolean ignoreStorage) throws BadConnectionToStorageException {
        if (file.getSymbolicLink() != null) {
            FileStatDTO ret = new FileStatDTO();
            ret.setName(file.getSymbolicLink().getName());
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
            if (!ignoreStorage) ret.setMount(storageService.getStorageStat(file.getStorage()));
        }
        if (ret == null) {
            ret = new FileStatDTO();
            ret.setType("nonexistent");
        }
        if (file.getVfRestPath().isEmpty() && Objects.equals(ret.getType(), "nonexistent")) {
            ret.setName(file.getVirtualFolder().getName());
            ret.setType("vdir");
        }
        return ret;
    }

    @Override
    @Transactional
    public List<FileStatDTO> listDir(FileBO file, BigInteger ignoreStorageId) throws BadConnectionToStorageException {
        HashMap<String, FileStatDTO> ret = new HashMap<>();
        try {
            StorageAccessor storage = file.getStorage() == null ? null : storageService.getConnection(file.getStorage().getId());
            if (storage != null) {
                storage.listDir(String.join("/", file.getStorageRestPath())).forEach(stat -> ret.put(stat.getName(), stat));
            }
        } catch (FileNotFoundException ignored) {
        }
        if (file.getVfRestPath().isEmpty()) {
            List<VirtualFolder> children = fsMapper.findChildren(file.getVirtualFolder().getId());
            for (VirtualFolder child : children) {
                BigInteger mountId = mountMapper.findByPathId(child.getId());
                StorageStatDTO storageStat = mountId != null ? storageService.getStorageStat(mountId) : null;
                if (ret.containsKey(child.getName())) {
                    ret.get(child.getName()).setMount(storageStat);
                    continue;
                }
                FileStatDTO childStat = new FileStatDTO();
                childStat.setName(child.getName());
                childStat.setType("vdir");
                childStat.setMount(storageStat);
                ret.put(child.getName(), childStat);
            }
            List<SymbolicLink> links = symbolicLinkMapper.findAllByParentPath(file.getVirtualFolder().getId());
            for (SymbolicLink link : links) {
                FileStatDTO linkStat = new FileStatDTO();
                linkStat.setName(link.getName());
                linkStat.setType("link");
                linkStat.setLinkTargetUser(userService.getUserDTO(link.getTargetUser()));
                String targetPath = symbolicLinkMapper.getTargetPathById(link.getId());
                linkStat.setLinkTarget(targetPath);
                linkStat.setLinkTargetStat(getStat(findFileBO(link.getTargetUser(), PathUtil.pathToQueue(targetPath))));
                ret.put(link.getName(), linkStat);
            }
        }
        return new LinkedList<>(ret.values());
    }

    @Override
    @Transactional
    public void mkdir(FileBO file, Boolean virtual) throws FileAlreadyExistsException, BadConnectionToStorageException {
        if (file.getSymbolicLink() != null) throw new FileAlreadyExistsException("");
        if (file.getStorage() == null || virtual) {
            BigInteger parent = file.getVirtualFolder().getId();
            for (String p : file.getVfRestPath()) {
                fsMapper.mkdir(parent, p);
                parent = fsMapper.getInsertId();
            }
            // 获取virtualFolder
            VirtualFolder virtualFolder = fsMapper.getById(parent);
            file.setVirtualFolder(virtualFolder);
            file.setVfRestPath(new LinkedList<>());
        } else {
            if (file.getStorageRestPath().isEmpty()) return;
            StorageAccessor storage = storageService.getConnection(file.getStorage().getId());
            String name = file.getStorageRestPath().pollLast();
            try {
                storage.mkdir(String.join("/", file.getStorageRestPath()), name);
            } finally {
                file.getStorageRestPath().offerLast(name);
            }
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
    public void upload(FileBO dest, MultipartFile srcFile) throws WriteToVirtualFolderNotAllowedException, BadConnectionToStorageException, IOException {
        if (dest.getStorage() == null) throw new WriteToVirtualFolderNotAllowedException();
        if (dest.getStorageRestPath().isEmpty()) throw new FileAlreadyExistsException("");
        String name = dest.getStorageRestPath().pollLast();
        StorageAccessor storage = storageService.getConnection(dest.getStorage().getId());
        try {
            storage.upload(String.join("/", dest.getStorageRestPath()), name, srcFile);
        } finally {
            dest.getStorageRestPath().offerLast(name);
        }
    }

    @Override
    public void download(FileBO source, OutputStream os) throws BadConnectionToStorageException, IOException {
        if (source.getStorage() == null) throw new FileNotFoundException();
        String name = source.getStorageRestPath().pollLast();
        StorageAccessor storage = storageService.getConnection(source.getStorage().getId());
        try {
            storage.download(String.join("/", source.getStorageRestPath()), name, os);
        } finally {
            source.getStorageRestPath().offerLast(name);
        }
    }

    private void deleteVirtualTree(VirtualFolder folder) {
        fsMapper.deleteById(folder.getId());
        mountMapper.deleteByPathId(folder.getId());
        symbolicLinkMapper.deleteAllChildrenSymbolicLinkByPathId(folder.getId());
        List<VirtualFolder> children = fsMapper.findChildren(folder.getId());
        children.forEach(this::deleteVirtualTree);
    }

    @Override
    @Transactional
    public void delete(FileBO file) throws BadConnectionToStorageException, FileNotFoundException {
        if (file.getSymbolicLink() != null) {
            symbolicLinkMapper.deleteById(file.getSymbolicLink().getId());
            file.setSymbolicLink(null);
            file.setLinkOwner(null);
        } else {
            boolean virtualSuccess = file.getVfRestPath().isEmpty();
            if (virtualSuccess) {
                deleteVirtualTree(file.getVirtualFolder());
            }
            boolean storageSuccess = file.getStorage() != null;
            if (storageSuccess) {
                try {
                    StorageAccessor storage = storageService.getConnection(file.getStorage().getId());
                    storage.delete(String.join("/", file.getStorageRestPath()));
                } catch (FileNotFoundException e) {
                    storageSuccess = false;
                }
            }
            if (!storageSuccess && !virtualSuccess) throw new FileNotFoundException("");
        }
    }

    @Override
    @Transactional
    public void mountOn(FileBO file, BigInteger storageId) {
        // 挂载
        Deque<String> vfRestPath = file.getVfRestPath();
        BigInteger parent = file.getVirtualFolder().getId();
        if (!vfRestPath.isEmpty()) {
            for (String p : vfRestPath) {
                fsMapper.mkdir(parent, p);
                parent = fsMapper.getInsertId();
            }
        }
        mountMapper.insertMount(parent, storageId);
    }

    @Override
    public void unmountOn(FileBO file) {
        BigInteger parent = file.getVirtualFolder().getId();
        if (file.getVfRestPath().isEmpty()) {
            mountMapper.deleteByPathId(parent);
        }
    }

    @Override
    public void createSymbolicLink(FileBO file, User targetUser, String path) {

    }

    private VirtualFolder findLastVirtualFolder(VirtualFolder folder, Deque<String> path) {
        while (!path.isEmpty()) {
            VirtualFolder nextFolder = fsMapper.findChildByName(folder.getId(), path.peekFirst());
            if (nextFolder == null) return folder;
            folder = nextFolder;
            path.pollFirst();
        }
        return folder;
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

    public FileBO findFileBO(BigInteger id, Deque<String> path) {
        return findFileBO(userMapper.getById(id), path);
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
