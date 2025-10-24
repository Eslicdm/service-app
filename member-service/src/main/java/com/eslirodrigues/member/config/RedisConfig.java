package com.eslirodrigues.member.config;

import com.eslirodrigues.member.dto.PriceUpdateEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, PriceUpdateEventDTO> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        var serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, PriceUpdateEventDTO.class);

        RedisTemplate<String, PriceUpdateEventDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        return template;
    }
}