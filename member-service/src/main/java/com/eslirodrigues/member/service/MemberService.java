package com.eslirodrigues.member.service;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.dto.MemberRequestEvent;
import com.eslirodrigues.member.entity.Member;
import com.eslirodrigues.member.entity.ServiceType;
import com.eslirodrigues.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final String MEMBER_REQUESTS_HASH_KEY = "member-requests";

    private final MemberRepository memberRepository;

    @Qualifier("genericRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    public List<Member> getAllMembersByManagerId(Long managerId) {
        return memberRepository.findAllByManagerId(managerId);
    }

    public List<MemberRequestEvent> getNewMemberRequests() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(MEMBER_REQUESTS_HASH_KEY);
        return entries.entrySet().stream()
                .map(entry -> new MemberRequestEvent(
                        (String) entry.getKey(),
                        ServiceType.fromValue((String) entry.getValue())
                ))
                .toList();
    }

    public Member createMember(
            Long managerId,
            CreateMemberRequest request
    ) {
        Member member = new Member();
        member.setName(request.name());
        member.setEmail(request.email());
        member.setBirthDate(request.birthDate());
        member.setPhoto(request.photo());
        member.setServiceType(request.serviceType());
        member.setManagerId(managerId);

        return memberRepository.save(member);
    }
}