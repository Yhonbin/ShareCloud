package com.firefly.sharemount.service;

import java.math.BigInteger;

public interface ParticipationService {
    Integer getPrivilegeById(BigInteger userId, BigInteger groupId);
}
