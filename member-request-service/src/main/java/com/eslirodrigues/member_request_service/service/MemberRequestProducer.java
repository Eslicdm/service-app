package com.eslirodrigues.member_request_service.service;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRequestProducer {

    @Value("${app.kafka.topic.member-requests}")
    private String topic;

    private final KafkaTemplate<String, MemberRequestDTO> kafkaTemplate;

    public void sendMemberRequest(MemberRequestDTO memberRequest) {
        kafkaTemplate.send(topic, memberRequest.email(), memberRequest);
        log.info("Sent member request for {} to topic {}", memberRequest.email(), topic);
    }
}