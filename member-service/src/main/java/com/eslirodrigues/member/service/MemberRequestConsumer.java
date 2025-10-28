package com.eslirodrigues.member.service;

import com.eslirodrigues.member.dto.MemberRequestEvent;
import com.eslirodrigues.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberRequestConsumer {

    private static final String MEMBER_REQUESTS_HASH_KEY = "member-requests";

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public MemberRequestConsumer(
            MemberRepository memberRepository,
            @Qualifier("genericRedisTemplate") RedisTemplate<String, Object> redisTemplate
    ) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.member-requests}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(MemberRequestEvent event) {
        log.info("Received member request event: {}", event);

        boolean memberExists = memberRepository.findByEmail(event.email()).isPresent();

        if (memberExists) {
            log.warn(
                    "Email {} already exists as a member. Ignoring request.",
                    event.email()
            );
            return;
        }

        redisTemplate.opsForHash()
                .put(MEMBER_REQUESTS_HASH_KEY, event.email(), event.serviceType());
        log.info("Stored new member request for {} in Redis.", event.email());
    }
}