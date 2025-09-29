package com.eslirodrigues.service_app_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .flatMap(jwt -> Mono.justOrEmpty(jwt.getClaimAsString("sub")))
                .defaultIfEmpty("anonymous");
    }

    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }
}
