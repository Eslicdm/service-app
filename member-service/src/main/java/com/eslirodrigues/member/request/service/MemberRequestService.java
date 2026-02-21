package com.eslirodrigues.member.request.service;

import com.eslirodrigues.member.request.dto.MemberRequestEvent;
import com.eslirodrigues.member.core.entity.ServiceType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberRequestService {

    private static final String MEMBER_REQUESTS_HASH_KEY = "member-requests";

    private final RedisTemplate<String, Object> redisTemplate;

    public MemberRequestService(
            @Qualifier("genericRedisTemplate") RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public List<MemberRequestEvent> getNewMemberRequests() {
        Map<Object, Object> entries =
                redisTemplate.opsForHash().entries(MEMBER_REQUESTS_HASH_KEY);
        return entries.entrySet().stream()
                .map(entry -> new MemberRequestEvent(
                        (String) entry.getKey(),
                        ServiceType.fromValue((String) entry.getValue())
                ))
                .toList();
    }
}
