package com.eslirodrigues.member_request_service.service

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MemberRequestProducer(
    private val kafkaTemplate: KafkaTemplate<String, MemberRequestDTO>,
    @param:Value($$"${app.kafka.topic.member-requests}") private val topic: String
) {
    companion object {
        private val log = LoggerFactory.getLogger(MemberRequestProducer::class.java)
    }

    fun sendMemberRequest(memberRequest: MemberRequestDTO) {
        kafkaTemplate.send(topic, memberRequest.email, memberRequest)
        log.info("Sent member request for ${memberRequest.email} to topic $topic")
    }
}