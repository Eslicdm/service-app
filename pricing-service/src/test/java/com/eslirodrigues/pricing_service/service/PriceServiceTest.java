package com.eslirodrigues.pricing_service.service;

import com.eslirodrigues.pricing_service.dto.PriceUpdateDTO;
import com.eslirodrigues.pricing_service.entity.Price;
import com.eslirodrigues.pricing_service.entity.PriceType;
import com.eslirodrigues.pricing_service.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PriceService priceService;

    private static final String PRICING_EXCHANGE = "pricing.exchange";
    private static final String PRICE_UPDATED_ROUTING_KEY = "price.updated.key";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                priceService, "pricingExchange", PRICING_EXCHANGE
        );
        ReflectionTestUtils.setField(
                priceService, "priceUpdatedRoutingKey", PRICE_UPDATED_ROUTING_KEY
        );
    }

    @Test
    void getAllPrices_shouldReturnListOfPrices() {
        var price1 = new Price(
                "1",
                PriceType.FULL_PRICE,
                new BigDecimal("99.90"),
                "Full Price Plan",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Price price2 = new Price(
                "2",
                PriceType.HALF_PRICE,
                new BigDecimal("49.95"),
                "Half Price Plan",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        List<Price> expectedPrices = List.of(price1, price2);

        when(priceRepository.findAll()).thenReturn(expectedPrices);

        List<Price> actualPrices = priceService.getAllPrices();

        assertEquals(expectedPrices.size(), actualPrices.size());
        assertEquals(expectedPrices, actualPrices);
        verify(priceRepository, times(1)).findAll();
    }

    @Test
    void updatePrice_shouldUpdateAndReturnPrice_whenPriceExists()
            throws ChangeSetPersister.NotFoundException {
        var priceType = PriceType.FULL_PRICE;
        var priceUpdateDTO = new PriceUpdateDTO(
                new BigDecimal("109.90"), "New full price description"
        );
        var existingPrice = new Price(
                "1",
                priceType,
                new BigDecimal("99.90"),
                "Old description",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(priceRepository.findByPriceType(priceType))
                .thenReturn(Optional.of(existingPrice));

        ArgumentCaptor<Price> priceCaptor = ArgumentCaptor.forClass(Price.class);
        when(priceRepository.save(priceCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Price updatedPrice = priceService.updatePrice(priceType, priceUpdateDTO);

        assertNotNull(updatedPrice);
        assertEquals(priceUpdateDTO.value(), updatedPrice.getValue());
        assertEquals(priceUpdateDTO.description(), updatedPrice.getDescription());
        assertNotNull(updatedPrice.getUpdatedAt());

        Price capturedPrice = priceCaptor.getValue();
        assertEquals(priceUpdateDTO.value(), capturedPrice.getValue());
        assertEquals(priceUpdateDTO.description(), capturedPrice.getDescription());

        verify(priceRepository, times(1)).findByPriceType(priceType);
        verify(priceRepository, times(1)).save(any(Price.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                PRICING_EXCHANGE, PRICE_UPDATED_ROUTING_KEY, updatedPrice
        );
    }

    @Test
    void updatePrice_shouldThrowNotFoundException_whenPriceDoesNotExist() {
        var priceType = PriceType.FREE;
        var priceUpdateDTO =
                new PriceUpdateDTO(BigDecimal.ZERO, "Free plan description");

        when(priceRepository.findByPriceType(priceType)).thenReturn(Optional.empty());

        assertThrows(
                ChangeSetPersister.NotFoundException.class,
                () -> priceService.updatePrice(priceType, priceUpdateDTO)
        );

        verify(priceRepository, times(1)).findByPriceType(priceType);
        verify(priceRepository, never()).save(any(Price.class));
        verify(rabbitTemplate, never()).convertAndSend(
                eq(PRICING_EXCHANGE), eq(PRICE_UPDATED_ROUTING_KEY), any(Price.class)
        );
    }
}