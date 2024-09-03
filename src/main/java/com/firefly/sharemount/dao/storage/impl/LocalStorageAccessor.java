package com.firefly.sharemount.dao.storage.impl;

import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.dao.storage.StorageAccessor;
import com.firefly.sharemount.dao.storage.StorageAccessorMeta;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@StorageAccessorMeta(
        displayTypeName = "LocalStorage",
        acceptInterfaceType = {"localStorage"},
        allowMultipartUpload = true
)
public class LocalStorageAccessor implements StorageAccessor {
    public static String getConnectionInfo(JSONObject args) {
        String path = args.getString("root");
        return path == null ? "<Error>" : path;
    }

    public static LocalStorageAccessor createNew(JSONObject args) {
        String path = args.getString("root");
        return path == null ? null : new LocalStorageAccessor(path);
    }

    private final File rootDir;
    private long lastAccessTime;

    private LocalStorageAccessor(String path) {
        rootDir = new File(path);
        lastAccessTime = System.currentTimeMillis();
    }

    @Override
    public void connect() {
        lastAccessTime = System.currentTimeMillis();
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) throw new RuntimeException("Failed to mkdirs.");
        }
        if (!rootDir.isDirectory()) throw new RuntimeException("Use an absolute file as the storage root directory.");
    }

    @Override
    public void mkdir(String path, String name) throws FileAlreadyExistsException {
        lastAccessTime = System.currentTimeMillis();
        File dir = new File(rootDir, String.join("", path, "/", name));
        if (!dir.mkdirs()) throw new FileAlreadyExistsException("Failed to mkdirs.");
    }

    @Override
    public void createEmpty(String path, String name) throws FileAlreadyExistsException {
        lastAccessTime = System.currentTimeMillis();
        File newFile = new File(rootDir, String.join("", path, "/", name));
        boolean success;
        try {
            success = newFile.createNewFile();
        } catch (IOException e) {
            success = false;
        }
        if (!success) throw new FileAlreadyExistsException("Failed to create new file.");
    }

    @Override
    public void upload(String path, String name, MultipartFile srcFile) throws IOException {
        lastAccessTime = System.currentTimeMillis();
        File parent = new File(rootDir, path);
        if (!parent.exists()) {
            if (!parent.mkdirs()) throw new FileAlreadyExistsException("File already exists.");
        }
        else if (!parent.isDirectory()) throw new FileAlreadyExistsException("File already exists.");
        File newFile = new File(parent, name);
        if (newFile.exists()) throw new FileAlreadyExistsException("File already exists.");
        srcFile.transferTo(newFile);
    }

    @Override
    public void download(String path, String name, OutputStream os) throws IOException {
        lastAccessTime = System.currentTimeMillis();
        File file = new File(rootDir, String.join("", path, "/", name));
        if (!file.isFile()) throw new FileNotFoundException("File not exists.");
        FileInputStream fis = new FileInputStream(file);
        IOUtils.copy(fis, os);
    }

    @Override
    public void copy(String source, String dest) throws IOException {
        lastAccessTime = System.currentTimeMillis();
        Files.copy(Paths.get(source), Paths.get(dest));
    }

    @Override
    public void move(String source, String dest) throws IOException {
        lastAccessTime = System.currentTimeMillis();
        Files.move(Paths.get(source), Paths.get(dest));
    }

    private boolean deleteTree(File file) {
        if (!file.exists()) return false;
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) deleteTree(child);
        }
        return file.delete();
    }

    @Override
    public void delete(String path) throws FileNotFoundException {
        lastAccessTime = System.currentTimeMillis();
        if (!deleteTree(new File(path))) throw new FileNotFoundException("Failed to delete file.");
    }

    @Override
    public boolean exists(String path) {
        lastAccessTime = System.currentTimeMillis();
        File file = new File(rootDir, path);
        return file.exists();
    }

    private FileStatDTO getFileStat(File file) {
        FileStatDTO ret = new FileStatDTO();
        if (!file.exists()) ret.setType("nonexistent");
        else {
            ret.setName(file.getName());
            ret.setLastModified(new Date(file.lastModified()));
            if (file.isDirectory()) ret.setType("dir");
            else {
                ret.setType("file");
                ret.setSize(file.length());
            }
        }
        return ret;
    }

    @Override
    public FileStatDTO getFileStat(String path) {
        lastAccessTime = System.currentTimeMillis();
        return getFileStat(new File(rootDir, path));
    }

    @Override
    public List<FileStatDTO> listDir(String path) throws FileNotFoundException {
        lastAccessTime = System.currentTimeMillis();
        File parent = new File(rootDir, path);
        if (!parent.isDirectory()) throw new FileNotFoundException();
        File[] children = parent.listFiles();
        if (children == null) throw new FileNotFoundException();
        ArrayList<FileStatDTO> ret = new ArrayList<>(children.length);
        for (File file : children) ret.add(getFileStat(file));
        return ret;
    }

    @Override
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public void close() {
    }
}
