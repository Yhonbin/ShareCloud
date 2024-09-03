package com.firefly.sharemount.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.config.ApplicationConfiguration;
import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.dao.StorageAccessorRetryProxy;
import com.firefly.sharemount.dao.impl.LocalStorageAccessor;
import com.firefly.sharemount.dao.impl.MinIoAccessor;
import com.firefly.sharemount.dao.impl.SftpAccessor;
import com.firefly.sharemount.mapper.MountMapper;
import com.firefly.sharemount.mapper.StorageMapper;
import com.firefly.sharemount.pojo.data.Storage;
import com.firefly.sharemount.pojo.dto.StorageDTO;
import com.firefly.sharemount.pojo.dto.StorageStatDTO;
import com.firefly.sharemount.service.StorageService;
import com.firefly.sharemount.service.UserService;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StorageServiceImpl implements StorageService {
    @Resource
    private StorageMapper storageMapper;

    @Resource
    private UserService userService;

    @Resource MountMapper mountMapper;

    @Resource
    private ApplicationConfiguration config;

    private final ConcurrentHashMap<BigInteger, StorageAccessor> connections = new ConcurrentHashMap<>();

    @Override
    public StorageStatDTO getStorageStat(BigInteger id) {
        return getStorageStat(storageMapper.getById(id));
    }

    @Override
    public StorageStatDTO getStorageStat(Storage storage) {
        StorageStatDTO ret = new StorageStatDTO();
        ret.setId(storage.getId());
        ret.setOwner(userService.getUserDTO(storage.getOwner()));
        ret.setName(storage.getName());
        ret.setReadonly(storage.getReadonly());
        String configStr = storageMapper.getInterfaceById(storage.getId());
        JSONObject interfaceJson = JSON.parseObject(configStr);
        ret.setType(getType(interfaceJson));
        ret.setConnectionInfo(getConnectionInfo(interfaceJson));
        return ret;
    }

    @Override
    public StorageAccessor getConnection(BigInteger id) {
        StorageAccessor ret = connections.get(id);
        if (ret != null) return ret;
        Boolean isReadOnly = storageMapper.getById(id).getReadonly();
        String configStr = storageMapper.getInterfaceById(id);
        StorageAccessor connection = makeConnection(isReadOnly, JSON.parseObject(configStr));
        config.loadConfig();
        Object retryObj = config.getNestedConfig("connection.retry-times");
        int retry = retryObj instanceof Integer ? (Integer) retryObj : 0;
        if (connection != null) connections.put(id, new StorageAccessorRetryProxy(connection, retry));
        return connection;
    }

    @Override
    @Transactional
    public void uploadStorage(StorageDTO storageDto) {
        storageMapper.uploadStorage(storageDto);
        BigInteger id = storageMapper.getInsertId();
        storageMapper.uploadStorageInterface(id, storageDto);
        storageMapper.uploadStorageLog(id,false, "");
    }

    @Override
    public void transfer(BigInteger srcId, BigInteger dstId) {
       storageMapper.transferToGroup(srcId, dstId);
    }

    @Override
    public BigInteger getOwnerById(BigInteger storageId) {
        return storageMapper.getById(storageId).getOwner();
    }

    @Override
    public void deleteStorage(BigInteger storageId) {
        storageMapper.deleteStorage(storageId);
        mountMapper.deleteStorageFromMount(storageId);
    }

    @Override
    public List<Storage> listStorage(BigInteger userId) {
        return storageMapper.listStorage(userId);
    }


    private StorageAccessor makeConnection(Boolean isReadOnly, JSONObject config) {
        switch (config.getString("type")) {
            case "localStorage":
                return LocalStorageAccessor.createNew(config);
            case "MinIO":
                return MinIoAccessor.createNew(config);
            case "SFTP":
                return SftpAccessor.createNew(config);
        }
        return null;
    }

    private String getConnectionInfo(JSONObject config) {
        switch (config.getString("type")) {
            case "localStorage":
                return LocalStorageAccessor.getConnectionInfo(config);
            case "MinIO":
                return MinIoAccessor.getConnectionInfo(config);
            case "SFTP":
                return SftpAccessor.getConnectionInfo(config);
        }
        return "<Error>";
    }

    private String getType(JSONObject config) {
        switch (config.getString("type")) {
            case "localStorage":
                return LocalStorageAccessor.getType();
            case "MinIO":
                return MinIoAccessor.getType();
            case "SFTP":
                return SftpAccessor.getType();
        }
        return "<Error>";
    }
}
