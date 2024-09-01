package com.firefly.sharemount.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import lombok.SneakyThrows;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.LocalFileFilter;
import net.schmizz.sshj.xfer.LocalSourceFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SftpAccessor implements StorageAccessor {
    public static String getType() {
        return "SFTP";
    }

    public static String getConnectionInfo(JSONObject args) {
        String host = args.getString("host");
        Integer port = args.getInteger("port");
        String user = args.getString("user");
        host = host == null ? "<Error>" : host;
        port = port == null ? 22 : port;
        user = user == null ? "<Error>" : user;
        return String.format("%s@%s:%d", user, host, port);
    }

    public static SftpAccessor createNew(JSONObject args) {
        String host = args.getString("host");
        Integer port = args.getInteger("port");
        String user = args.getString("user");
        String password = args.getString("password");
        port = port == null ? 22 : port;
        if (host == null) return null;
        if (user == null || password == null) return null;
        return new SftpAccessor(host, port, user, password);
    }

    private static class EmptyFile implements LocalSourceFile {
        private final String filename;

        public EmptyFile(String filename) {
            this.filename = filename;
        }

        @Override
        public String getName() {
            return filename;
        }

        @Override
        public long getLength() {
            return 0;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(new byte[] {});
        }

        @Override
        public int getPermissions() throws IOException {
            return 420;  // 0644
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public Iterable<? extends LocalSourceFile> getChildren(LocalFileFilter localFileFilter) throws IOException {
            throw new IOException("Error listing files in directory: " + this);
        }

        @Override
        public boolean providesAtimeMtime() {
            return true;
        }

        @Override
        public long getLastAccessTime() throws IOException {
            return System.currentTimeMillis() / 1000;
        }

        @Override
        public long getLastModifiedTime() throws IOException {
            return System.currentTimeMillis() / 1000;
        }
    }

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private SFTPClient client;

    private SftpAccessor(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    @SneakyThrows
    public void connect() {
        SSHClient sshClient = new SSHClient();
        sshClient.connect(host, port);
        sshClient.authPassword(user, password);
        client = sshClient.newSFTPClient();
    }

    @Override
    @SneakyThrows
    public void mkdir(String path, String name) {
        client.mkdirs(String.join("", path, "/", name));
    }

    @Override
    @SneakyThrows
    public void createEmpty(String path, String name) {
        client.put(new EmptyFile(name), String.join("", path, "/", name));
    }

    @Override
    public void copy(String source, String dest) {

    }

    @Override
    public void move(String source, String dest) {

    }

    @Override
    public void delete(String path) {

    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public FileStatDTO getFileStat(String path) {
        return null;
    }

    @Override
    public List<FileStatDTO> listDir(String path) {
        return null;
    }

    @Override
    public long getLastAccessTime() {
        return 0;
    }

    @Override
    public void close() {

    }
}
