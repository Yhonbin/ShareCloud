package com.firefly.sharemount.dao.storage.impl;

import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.dao.storage.StorageAccessor;
import com.firefly.sharemount.dao.storage.StorageAccessorMeta;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import io.minio.*;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.List;

@StorageAccessorMeta(
        displayTypeName = "MinIO",
        acceptInterfaceType = {"minio"},
        allowMultipartUpload = true
)
public class MinIoAccessor implements StorageAccessor {
    public static String getConnectionInfo(JSONObject args) {
        String endpoint = args.getString("endpoint");
        String bucket = args.getString("bucket");
        endpoint = endpoint == null ? "<Error>" : endpoint;
        bucket = bucket == null ? "<Error>" : bucket;
        endpoint = endpoint.contains("://") ? "https://" + endpoint : endpoint;
        endpoint = endpoint.endsWith("/") ? endpoint : endpoint + "/";
        return endpoint + bucket;
    }

    public static MinIoAccessor createNew(Boolean readonly, JSONObject args) {
        String endpoint = args.getString("endpoint");
        String accessKey = args.getString("accessKey");
        String secretKey = args.getString("secretKey");
        String bucket = args.getString("bucket");
        if (endpoint == null || accessKey == null) return null;
        if (secretKey == null || bucket == null) return null;
        return new MinIoAccessor(endpoint, accessKey, secretKey, bucket);
    }

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucket;
    private MinioClient client;
    private long lastAccessTime;

    private MinIoAccessor(String endpoint, String accessKey, String secretKey, String bucket) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucket = bucket;
        this.lastAccessTime = System.currentTimeMillis();
    }

    @Override
    @SneakyThrows
    public void connect() {
        lastAccessTime = System.currentTimeMillis();
        client = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    }

    @Override
    @SneakyThrows
    public void mkdir(String path, String name) {
        lastAccessTime = System.currentTimeMillis();
        client.putObject(PutObjectArgs.builder().bucket(bucket).object(String.join("", path, "/", name, "/"))
                .stream(new ByteArrayInputStream(new byte[]{}),0,-1).build());
    }

    @Override
    @SneakyThrows
    public void createEmpty(String path, String name) {
        lastAccessTime = System.currentTimeMillis();
        client.putObject(PutObjectArgs.builder().bucket(bucket).object(String.join("", path, "/", name))
                .stream(new ByteArrayInputStream(new byte[]{}),0,-1).build());
    }

    @Override
    @SneakyThrows
    public void copy(String source, String dest) {
        lastAccessTime = System.currentTimeMillis();
        client.copyObject(CopyObjectArgs.builder().bucket(bucket).object(source)
                .source(CopySource.builder().bucket(bucket).object(dest).build()).build());

    }

    @Override
    @SneakyThrows
    public void move(String source, String dest) {
        lastAccessTime = System.currentTimeMillis();
        copy(source, dest);
        client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(source).build());
    }

    @Override
    @SneakyThrows
    public void upload(String path, String name, MultipartFile srcFile) {

    }

    @Override
    @SneakyThrows
    public void download(String path, String name, OutputStream os) {

    }

    @Override
    @SneakyThrows
    public void delete(String path) {
        lastAccessTime = System.currentTimeMillis();
        client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path).build());
    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    @SneakyThrows
    public FileStatDTO getFileStat(String path) {
        lastAccessTime = System.currentTimeMillis();
        StatObjectResponse objectStat = client.statObject(StatObjectArgs.builder().bucket(bucket).object(path).build());
        // TODO
        return null;
    }

    @Override
    @SneakyThrows
    public List<FileStatDTO> listDir(String path) {
        lastAccessTime = System.currentTimeMillis();
        return null;
    }

    @Override
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public void close() {
        client = null;
    }
}
