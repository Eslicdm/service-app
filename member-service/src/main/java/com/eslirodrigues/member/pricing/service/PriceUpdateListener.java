package com.eslirodrigues.member.pricing.service;

import com.eslirodrigues.member.pricing.dto.PriceUpdateEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PriceUpdateListener {

    private static final Logger log = LoggerFactory.getLogger(PriceUpdateListener.class);

    private final PriceCacheService priceCacheService;

    public PriceUpdateListener(PriceCacheService priceCacheService) {
        this.priceCacheService = priceCacheService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.price-updated}")
    public void onPriceUpdate(PriceUpdateEventDTO priceUpdate) {
        log.info("Received price update event: {}", priceUpdate);
        priceCacheService.cachePriceUpdate(priceUpdate);
    }
}