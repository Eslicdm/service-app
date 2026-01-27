package com.eslirodrigues.member.request.config;

import com.eslirodrigues.member.request.dto.MemberRequestEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MemberRequestEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, MemberRequestEvent> consumerFactory
    ) {
        var factory =
                new ConcurrentKafkaListenerContainerFactory<String, MemberRequestEvent>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}