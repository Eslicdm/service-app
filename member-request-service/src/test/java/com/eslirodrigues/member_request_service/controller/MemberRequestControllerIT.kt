package com.eslirodrigues.member_request_service.controller

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.utility.DockerImageName

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MemberRequestControllerIT @Autowired constructor(
    private val jwtDecoder: JwtDecoder,
    private val redisTemplate: RedisTemplate<String, String>
) {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var requestSpecification: RequestSpecification

    @TestConfiguration
    class TestSecurityConfig {
        @Bean
        fun jwtDecoder(): JwtDecoder = Mockito.mock(JwtDecoder::class.java)
    }

    companion object {
        private const val MEMBER_REQUESTS_PATH = "/api/v1/member-requests"
        private const val SUBMISSION_CACHE_PREFIX = "submission:"

        @Container
        @JvmField
        val redis: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379)

        @Container
        @JvmField
        val kafka = ConfluentKafkaContainer("confluentinc/cp-kafka:7.5.3")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.apply {
                add("spring.data.redis.host", redis::getHost)
                add("spring.data.redis.port", redis::getFirstMappedPort)
                add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers)
                add("spring.security.oauth2.resourceserver.jwt.issuer-uri") {
                    "http://dummy-issuer"
                }
            }
        }
    }

    @BeforeEach
    fun setUp() {
        requestSpecification = RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(port)
            .setContentType(ContentType.JSON)
            .addHeader("Authorization", "Bearer dummy-token")
            .build()

        val jwt = Jwt.withTokenValue("dummy-token")
            .header("alg", "none")
            .claim("sub", "test-user")
            .build()
        Mockito.`when`(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt)
    }

    @AfterEach
    fun tearDown() {
        redisTemplate.keys("$SUBMISSION_CACHE_PREFIX*")
            .takeIf { keys -> keys.isNotEmpty() }
            ?.let { keys -> redisTemplate.delete(keys) }
    }

    @Test
    fun `submitRequest with valid data should return accepted and store in redis`() {
        val request =
            MemberRequestDTO("test.user@serviceapp.com", MemberRequestDTO.ServiceType.FREE)

        Given {
            spec(requestSpecification).body(request)
        } When { post(MEMBER_REQUESTS_PATH) } Then { statusCode(202) }

        val cacheKey = SUBMISSION_CACHE_PREFIX + request.email
        val cachedValue = redisTemplate.opsForValue().get(cacheKey)
        assertThat(cachedValue).isEqualTo("processed")
    }
}