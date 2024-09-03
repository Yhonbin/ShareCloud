package com.firefly.sharemount.service;

import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.data.Storage;
import com.firefly.sharemount.pojo.dto.StorageDTO;
import com.firefly.sharemount.pojo.dto.StorageStatDTO;

import java.math.BigInteger;
import java.util.List;

public interface StorageService {
    StorageStatDTO getStorageStat(BigInteger id);

    StorageStatDTO getStorageStat(Storage storage);

    StorageAccessor getConnection(BigInteger id);

    void uploadStorage(StorageDTO storageDto);

    void transfer(BigInteger srcId, BigInteger dstId);

    BigInteger getOwnerById(BigInteger storageId);

    void deleteStorage(BigInteger storageId);

    List<Storage> listStorage(BigInteger id);
}
