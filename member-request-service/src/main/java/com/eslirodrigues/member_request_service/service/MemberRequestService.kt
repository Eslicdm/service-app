package com.eslirodrigues.member_request_service.service;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRequestService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRequestProducer memberRequestProducer;
    private static final String SUBMISSION_CACHE_PREFIX = "submission:";
    private static final Duration SUBMISSION_CACHE_TTL = Duration.ofMinutes(5);

    public void processSubmission(MemberRequestDTO memberRequestDTO) {
        String cacheKey = SUBMISSION_CACHE_PREFIX + memberRequestDTO.email();

        Boolean alreadyProcessed = redisTemplate.opsForValue().setIfAbsent(
                cacheKey,
                "processed",
                SUBMISSION_CACHE_TTL
        );

        if (Boolean.TRUE.equals(alreadyProcessed)) {
            log.info(
                    "New submission for email: {}. Sending to Kafka.",
                    memberRequestDTO.email()
            );
            memberRequestProducer.sendMemberRequest(memberRequestDTO);
        } else {
            log.warn(
                    "Duplicate submission detected within 5 minutes for email: {}. Ignoring.",
                    memberRequestDTO.email()
            );
        }
    }
}