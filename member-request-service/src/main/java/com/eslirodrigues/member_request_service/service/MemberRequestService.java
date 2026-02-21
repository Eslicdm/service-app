package com.eslirodrigues.member_request_service.service;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MemberRequestService {

    private static final Logger log = LoggerFactory.getLogger(MemberRequestService.class);
    private static final String SUBMISSION_CACHE_PREFIX = "submission:";
    private static final Duration SUBMISSION_CACHE_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRequestProducer memberRequestProducer;

    public MemberRequestService(
            RedisTemplate<String, String> redisTemplate,
            MemberRequestProducer memberRequestProducer
    ) {
        this.redisTemplate = redisTemplate;
        this.memberRequestProducer = memberRequestProducer;
    }

    public void processSubmission(MemberRequestDTO memberRequestDTO) {
        String cacheKey = SUBMISSION_CACHE_PREFIX + memberRequestDTO.email();

        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(cacheKey, "processed", SUBMISSION_CACHE_TTL);

        if (Boolean.TRUE.equals(wasSet)) {
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