package com.eslirodrigues.member_request_service.service;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MemberRequestProducer {

    private static final Logger log = LoggerFactory.getLogger(MemberRequestProducer.class);

    private final KafkaTemplate<String, MemberRequestDTO> kafkaTemplate;
    private final String topic;

    public MemberRequestProducer(
            KafkaTemplate<String, MemberRequestDTO> kafkaTemplate,
            @Value("${app.kafka.topic.member-requests}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMemberRequest(MemberRequestDTO memberRequest) {
        kafkaTemplate.send(topic, memberRequest.email(), memberRequest);
        log.info("Sent member request for {} to topic {}", memberRequest.email(), topic);
    }
}