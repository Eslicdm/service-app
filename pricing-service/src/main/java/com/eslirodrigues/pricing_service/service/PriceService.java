package com.eslirodrigues.pricing_service.service;

import com.eslirodrigues.pricing_service.dto.PriceUpdateDTO;
import com.eslirodrigues.pricing_service.entity.Price;
import com.eslirodrigues.pricing_service.entity.PriceType;
import com.eslirodrigues.pricing_service.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.pricing}")
    private String pricingExchange;

    @Value("${app.rabbitmq.routingkey.price-updated}")
    private String priceUpdatedRoutingKey;

    public List<Price> getAllPrices() {
        return priceRepository.findAll();
    }

    public Price updatePrice(
            PriceType priceType,
            PriceUpdateDTO priceUpdateDTO
    ) throws ChangeSetPersister.NotFoundException {
        Price price = priceRepository.findByPriceType(priceType)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        price.setValue(priceUpdateDTO.value());
        price.setDescription(priceUpdateDTO.description());
        price.setUpdatedAt(LocalDateTime.now());

        Price updatedPrice = priceRepository.save(price);

        sendPriceUpdateNotification(updatedPrice);

        return updatedPrice;
    }

    private void sendPriceUpdateNotification(Price price) {
        rabbitTemplate.convertAndSend(pricingExchange, priceUpdatedRoutingKey, price);
    }
}