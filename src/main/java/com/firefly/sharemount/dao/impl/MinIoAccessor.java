package com.firefly.sharemount.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import io.minio.*;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.util.List;

public class MinIoAccessor implements StorageAccessor {
    public static MinIoAccessor createNew(JSONObject args) {
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
        client.putObject(PutObjectArgs.builder().bucket(bucket).object(String.join("", path, "/", name, "/"))
                .stream(new ByteArrayInputStream(new byte[]{}),0,-1).build());
    }

    @Override
    @SneakyThrows
    public void createEmpty(String path, String name) {
        client.putObject(PutObjectArgs.builder().bucket(bucket).object(String.join("", path, "/", name))
                .stream(new ByteArrayInputStream(new byte[]{}),0,-1).build());
    }

    @Override
    @SneakyThrows
    public void rename(String loc, String source, String dest) {
        client.copyObject(CopyObjectArgs.builder().bucket(bucket).object(String.join("", loc, "/", dest))
                .source(CopySource.builder().bucket(bucket).object(String.join("", loc, "/", source)).build()).build());

    }

    @Override
    @SneakyThrows
    public void copy(String source, String dest) {
        client.copyObject(CopyObjectArgs.builder().bucket(bucket).object(source)
                .source(CopySource.builder().bucket(bucket).object(dest).build()).build());

    }

    @Override
    public void move(String source, String dest) {

    }

    @Override
    public void delete(String path) {

    }

    @Override
    @SneakyThrows
    public FileStatDTO getFileStat(String path) {
        StatObjectResponse objectStat = client.statObject(StatObjectArgs.builder().bucket(bucket).object(path).build());
        // TODO
        return null;
    }

    @Override
    @SneakyThrows
    public List<FileStatDTO> listDir(String path) {
        return null;
    }

    @Override
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public void close() {

    }
}
