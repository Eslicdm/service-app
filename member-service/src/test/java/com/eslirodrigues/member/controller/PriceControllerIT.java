package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.PriceUpdateEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PriceControllerIT {

    private static final String PRICES_PATH = "/api/v1/prices";

    @LocalServerPort
    private Integer port;

    private RequestSpecification requestSpecification;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private RedisTemplate<String, PriceUpdateEventDTO> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance().build();


    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return Mockito.mock(JwtDecoder.class);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("app.services.pricing.base-url", wireMockServer::baseUrl);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> "http://dummy-issuer"
        );
    }

    @BeforeEach
    void setUp() {
        var connectionFactory = redisTemplate.getConnectionFactory();
        Assertions.assertNotNull(connectionFactory);
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.serverCommands().flushAll();
        }
        wireMockServer.resetAll();

        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost").setPort(port)
                .addHeader("Authorization", "Bearer dummy-token").build();

        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "none").claim("sub", "test-user").build();
        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);
    }

    @Test
    void getAllPrices_whenCacheMiss_shouldFetchFromServiceAndCacheResult() throws JsonProcessingException {
        var pricesFromService = List.of(
                new PriceUpdateEventDTO("1", PriceUpdateEventDTO.PriceType.FREE, BigDecimal.ZERO, "Free", LocalDateTime.now(), LocalDateTime.now()),
                new PriceUpdateEventDTO("2", PriceUpdateEventDTO.PriceType.HALF_PRICE, new BigDecimal("49.99"), "Half", LocalDateTime.now(), LocalDateTime.now()),
                new PriceUpdateEventDTO("3", PriceUpdateEventDTO.PriceType.FULL_PRICE, new BigDecimal("99.99"), "Full", LocalDateTime.now(), LocalDateTime.now())
        );
        wireMockServer.stubFor(get(urlEqualTo("/prices")).willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(pricesFromService))));

        // First call (cache miss)
        given().spec(requestSpecification)
                .when().get(PRICES_PATH)
                .then().assertThat().statusCode(200)
                .body("$", hasSize(3))
                .body("[0].priceType", is("free"));

        // Second call (cache hit)
        given().spec(requestSpecification)
                .when().get(PRICES_PATH)
                .then().assertThat().statusCode(200)
                .body("$", hasSize(3));

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/prices")));
    }
}