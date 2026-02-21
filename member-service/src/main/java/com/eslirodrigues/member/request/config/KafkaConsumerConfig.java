package com.eslirodrigues.member.request.config;

import com.eslirodrigues.member.request.dto.MemberRequestEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, MemberRequestEvent> consumerFactory(
            KafkaProperties kafkaProperties
    ) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        // Explicitly configure ErrorHandlingDeserializer and delegates via properties
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                JacksonJsonDeserializer.class.getName());
        
        // Tell JsonDeserializer to target your DTO and ignore missing headers
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                MemberRequestEvent.class.getName());
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

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