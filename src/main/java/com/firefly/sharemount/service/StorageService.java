package com.firefly.sharemount.service;

import com.firefly.sharemount.dao.StorageAccessor;
import com.firefly.sharemount.pojo.data.Storage;
import com.firefly.sharemount.pojo.dto.StorageStatDTO;

import java.math.BigInteger;

public interface StorageService {
    StorageStatDTO getStorageStat(BigInteger id);

    StorageStatDTO getStorageStat(Storage storage);

    StorageAccessor getConnection(BigInteger id);
}
