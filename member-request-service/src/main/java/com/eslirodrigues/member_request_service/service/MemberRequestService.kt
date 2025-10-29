package com.eslirodrigues.member_request_service.service

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class MemberRequestService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val memberRequestProducer: MemberRequestProducer
) {

    companion object {
        private val log = LoggerFactory.getLogger(MemberRequestService::class.java)
        private const val SUBMISSION_CACHE_PREFIX = "submission:"
        private val SUBMISSION_CACHE_TTL = Duration.ofMinutes(5)
    }

    fun processSubmission(memberRequestDTO: MemberRequestDTO) {
        val cacheKey = SUBMISSION_CACHE_PREFIX + memberRequestDTO.email

        val wasSet = redisTemplate.opsForValue()
            .setIfAbsent(cacheKey, "processed", SUBMISSION_CACHE_TTL)

        if (wasSet == true) {
            log.info("New submission for email: ${memberRequestDTO.email}. Sending to Kafka.")
            memberRequestProducer.sendMemberRequest(memberRequestDTO)
        } else {
            log.warn("Duplicate submission detected within 5 minutes for email:" +
                    " ${memberRequestDTO.email}. Ignoring.")
        }
    }
}