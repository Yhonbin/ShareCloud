package com.firefly.sharemount.service;

import java.math.BigInteger;
import java.util.List;

public interface ParticipationService {
    List<BigInteger> getParticipatedGroups(BigInteger userId);

    Integer getPrivilegeById(BigInteger userId, BigInteger groupId);
}
