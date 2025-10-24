package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.entity.ServiceType;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MemberControllerIT {

    private static final String BASE_PATH = "/api/v1/members/{managerId}";

    @LocalServerPort
    private Integer port;

    private RequestSpecification requestSpecification;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private JwtDecoder jwtDecoder;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return Mockito.mock(JwtDecoder.class);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
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
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer dummy-token")
                .build();

        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "none")
                .claim("sub", "test-user-id")
                .claim("scope", "read write")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);
    }

    @Test
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createMember_withValidRequest_shouldReturnCreated() {
        Long managerId = 1L;
        var request = new CreateMemberRequest(
                "John Doe",
                "john.doe@test.com",
                LocalDate.of(1990, 1, 15),
                "http://example.com/photo.jpg",
                ServiceType.FULL_PRICE
        );

        given().spec(requestSpecification)
                .body(request)
                .when().post(BASE_PATH, managerId)
                .then().statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john.doe@test.com"))
                .body("managerId", equalTo(managerId.intValue()));
    }

    @Test
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllMembersByManagerId_whenMembersExist_shouldReturnMemberList() {
        Long managerId = 2L;
        var memberToCreate = new CreateMemberRequest(
                "Test Member",
                "test.member@serviceapp.com",
                LocalDate.of(1995, 5, 25),
                null,
                ServiceType.FREE
        );

        given().spec(requestSpecification)
                .body(memberToCreate)
                .post(BASE_PATH, managerId)
                .then().statusCode(201);

        given().spec(requestSpecification)
                .when().get(BASE_PATH, managerId)
                .then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("Test Member"))
                .body("[0].managerId", equalTo(managerId.intValue()));
    }
}