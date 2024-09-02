package com.firefly.sharemount.service;

import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.data.Storage;
import com.firefly.sharemount.pojo.dto.StorageDTO;
import com.firefly.sharemount.pojo.dto.StorageStatDTO;

import java.math.BigInteger;

public interface StorageService {
    StorageStatDTO getStorageStat(BigInteger id);

    StorageStatDTO getStorageStat(Storage storage);

    StorageAccessor getConnection(BigInteger id);

    void uploadStorage(StorageDTO storageDto);

    void transferToGroup(BigInteger owner, BigInteger groupId);

    BigInteger getOwnerById(BigInteger storageId);
}
