package com.firefly.sharemount.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.mapper.StorageMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StorageServiceImpl {
    private final ConcurrentHashMap<BigInteger, StorageAccessor> connectionCache;

    @Resource
    private StorageMapper storageMapper;

    private StorageServiceImpl() {
        connectionCache = new ConcurrentHashMap<>();
    }

    private StorageAccessor makeConnection(Boolean isReadOnly, JSONObject config) {
        //try {
            if (!config.containsKey("type")) return null;
            switch (config.getString("type")) {

            }
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        return null;
    }

    public StorageAccessor getConnection(BigInteger id) {
        if (connectionCache.containsKey(id)) return connectionCache.get(id);
        Boolean isReadOnly = storageMapper.getById(id).getReadonly();
        String configStr = storageMapper.getInterfaceById(id);
        StorageAccessor connection = makeConnection(isReadOnly, JSON.parseObject(configStr));
        if (connection != null) connectionCache.put(id, connection);
        return connection;
    }
}
