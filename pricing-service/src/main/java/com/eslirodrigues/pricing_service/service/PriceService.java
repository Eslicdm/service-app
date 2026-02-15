package com.eslirodrigues.pricing_service.service;

import com.eslirodrigues.pricing_service.dto.PriceUpdateDTO;
import com.eslirodrigues.pricing_service.entity.Price;
import com.eslirodrigues.pricing_service.entity.PriceType;
import com.eslirodrigues.pricing_service.repository.PriceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceService {

    private final PriceRepository priceRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.pricing}")
    private String pricingExchange;

    @Value("${app.rabbitmq.routingkey.price-updated}")
    private String priceUpdatedRoutingKey;

    public PriceService(PriceRepository priceRepository, RabbitTemplate rabbitTemplate) {
        this.priceRepository = priceRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Price> getAllPrices() {
        return priceRepository.findAll();
    }

    public Price updatePrice(
            PriceType priceType,
            PriceUpdateDTO priceUpdateDTO
    ) {
        Price price = priceRepository.findByPriceType(priceType).orElseGet(() -> {
            Price newPrice = new Price();
            newPrice.setPriceType(priceType);
            newPrice.setCreatedAt(LocalDateTime.now());
            return newPrice;
        });

        price.setValue(priceUpdateDTO.value());
        price.setDescription(priceUpdateDTO.description());
        price.setUpdatedAt(LocalDateTime.now());

        Price savedPrice = priceRepository.save(price);

        sendPriceUpdateNotification(savedPrice);

        return savedPrice;
    }

    private void sendPriceUpdateNotification(Price price) {
        rabbitTemplate.convertAndSend(pricingExchange, priceUpdatedRoutingKey, price);
    }
}