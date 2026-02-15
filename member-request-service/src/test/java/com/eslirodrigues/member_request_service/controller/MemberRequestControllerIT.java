package com.eslirodrigues.member_request_service.controller;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class MemberRequestControllerIT {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @LocalServerPort
    private int port;

    private RequestSpecification requestSpecification;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return Mockito.mock(JwtDecoder.class);
        }
    }

    private static final String MEMBER_REQUESTS_PATH = "/api/v1/member-requests";
    private static final String SUBMISSION_CACHE_PREFIX = "submission:";

    @Container
    static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    @Container
    static final ConfluentKafkaContainer kafka =
            new ConfluentKafkaContainer("confluentinc/cp-kafka:7.5.3");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> "http://dummy-issuer");
    }

    @BeforeEach
    void setUp() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer dummy-token")
                .build();

        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "none")
                .claim("sub", "test-user")
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
    }

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys(SUBMISSION_CACHE_PREFIX + "*");
        if (!keys.isEmpty()) redisTemplate.delete(keys);
    }

    @Test
    void submitRequest_withValidData_shouldReturnAcceptedAndStoreInRedis() {
        var request = new MemberRequestDTO("test.user@serviceapp.com", MemberRequestDTO.ServiceType.FREE);

        given().spec(requestSpecification).body(request)
        .when().post(MEMBER_REQUESTS_PATH)
        .then().statusCode(202);

        String cacheKey = SUBMISSION_CACHE_PREFIX + request.email();
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);
        assertThat(cachedValue).isEqualTo("processed");
    }
}