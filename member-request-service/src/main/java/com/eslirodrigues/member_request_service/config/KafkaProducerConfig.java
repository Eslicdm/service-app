package com.eslirodrigues.member_request_service.config;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, MemberRequestDTO> kafkaTemplate(
            ProducerFactory<String, MemberRequestDTO> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}