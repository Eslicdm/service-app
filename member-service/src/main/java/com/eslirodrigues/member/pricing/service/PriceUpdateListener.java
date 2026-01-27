package com.eslirodrigues.member.pricing.service;

import com.eslirodrigues.member.pricing.dto.PriceUpdateEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceUpdateListener {

    private final PriceCacheService priceCacheService;

    @RabbitListener(queues = "${app.rabbitmq.queue.price-updated}")
    public void onPriceUpdate(PriceUpdateEventDTO priceUpdate) {
        log.info("Received price update event: {}", priceUpdate);
        priceCacheService.cachePriceUpdate(priceUpdate);
    }
}