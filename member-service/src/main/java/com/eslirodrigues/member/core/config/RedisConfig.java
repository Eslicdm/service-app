package com.eslirodrigues.member.core.config;

import com.eslirodrigues.member.pricing.dto.PriceUpdateEventDTO;
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

    @Bean("priceUpdateRedisTemplate")
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

    @Bean("genericRedisTemplate")
    public RedisTemplate<String, Object> genericRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        var serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        return template;
    }
}