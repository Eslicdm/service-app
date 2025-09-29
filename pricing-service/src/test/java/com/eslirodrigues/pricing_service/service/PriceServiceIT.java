package com.eslirodrigues.pricing_service.service;

import com.eslirodrigues.pricing_service.dto.PriceUpdateDTO;
import com.eslirodrigues.pricing_service.entity.Price;
import com.eslirodrigues.pricing_service.entity.PriceType;
import com.eslirodrigues.pricing_service.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class PriceServiceIT {

    @Container
    private static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:7.0");
    @Container
    private static final RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @Test
    void updatePrice_shouldUpdatePriceInDatabaseAndSendRabbitMQMessage()
            throws ChangeSetPersister.NotFoundException {
        var priceType = PriceType.FULL_PRICE;
        var initialPrice = new Price(
                null,
                priceType,
                new BigDecimal("99.90"),
                "Initial description",
                null,
                null
        );
        initialPrice = priceRepository.save(initialPrice);

        var priceUpdateDTO =
                new PriceUpdateDTO(new BigDecimal("109.90"), "Updated description");

        Price updatedPrice = priceService.updatePrice(priceType, priceUpdateDTO);

        Optional<Price> retrievedPrice = priceRepository.findById(updatedPrice.getId());
        assertTrue(retrievedPrice.isPresent());
        assertEquals(priceUpdateDTO.value(), retrievedPrice.get().getValue());
        assertEquals(priceUpdateDTO.description(), retrievedPrice.get().getDescription());

        priceRepository.deleteById(initialPrice.getId());
    }

}
