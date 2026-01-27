package com.eslirodrigues.member.request.service;

import com.eslirodrigues.member.request.dto.MemberRequestEvent;
import com.eslirodrigues.member.core.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberRequestService {

    private static final String MEMBER_REQUESTS_HASH_KEY = "member-requests";

    @Qualifier("genericRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

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
