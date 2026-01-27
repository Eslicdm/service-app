package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.member.dto.UpdateMemberRequest;
import com.eslirodrigues.member.core.entity.ServiceType;
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
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MemberControllerIT {

    private static final String BASE_PATH = "/api/v1/members";
    private static final String MEMBER_ID_PATH = "/api/v1/members/{memberId}";

    @LocalServerPort
    private Integer port;

    private RequestSpecification requestSpecification;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Container
    static RabbitMQContainer rabbitmq =
            new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"));

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
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
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
                .when().queryParam("managerId", managerId).post(BASE_PATH)
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
                .queryParam("managerId", managerId).post(BASE_PATH)
                .then().statusCode(201);

        given().spec(requestSpecification)
                .when().queryParam("managerId", managerId).get(BASE_PATH)
                .then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("Test Member"))
                .body("[0].managerId", equalTo(managerId.intValue()));
    }

    @Test
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateMember_withValidRequest_shouldReturnOk() {
        Long managerId = 3L;
        var createRequest = new CreateMemberRequest(
                "Member to Update",
                "update.me@serviceapp.com",
                LocalDate.of(2000, 2, 20),
                null,
                ServiceType.FREE
        );

        Integer memberId = given().spec(requestSpecification)
                .body(createRequest)
                .queryParam("managerId", managerId).post(BASE_PATH)
                .then().statusCode(201)
                .extract().path("id");

        var updateRequest = new UpdateMemberRequest(
                "Updated Name",
                "updated.email@serviceapp.com",
                null, null, null
        );

        given().spec(requestSpecification)
                .body(updateRequest)
                .when().put(MEMBER_ID_PATH, memberId)
                .then().statusCode(200)
                .body("id", equalTo(memberId))
                .body("name", equalTo("Updated Name"))
                .body("email", equalTo("updated.email@serviceapp.com"));
    }

    @Test
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteMember_whenMemberExists_shouldReturnNoContent() {
        Long managerId = 4L;
        var createRequest = new CreateMemberRequest(
                "Member to Delete",
                "delete.me@serviceapp.com",
                LocalDate.of(2001, 3, 21),
                null,
                ServiceType.HALF_PRICE
        );

        Integer memberId = given().spec(requestSpecification)
                .body(createRequest)
                .queryParam("managerId", managerId).post(BASE_PATH)
                .then().statusCode(201)
                .extract().path("id");

        given().spec(requestSpecification)
                .when().delete(MEMBER_ID_PATH, memberId)
                .then().statusCode(204);

        given().spec(requestSpecification)
                .when().get(MEMBER_ID_PATH, memberId)
                .then().statusCode(404);
    }
}