package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.mapper.ParticipationMapper;
import com.firefly.sharemount.service.ParticipationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Service
public class ParticipationServiceImpl implements ParticipationService {
    @Resource
    private ParticipationMapper participationMapper;

    @Override
    public List<BigInteger> getParticipatedGroups(BigInteger userId) {
        return participationMapper.findParticipatedGroups(userId);
    }

    @Override
    public Integer getPrivilegeById(BigInteger userId, BigInteger groupId) {
        return participationMapper.getPrivilegeById(userId, groupId);
    }
}
