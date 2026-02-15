package com.eslirodrigues.member.pricing.service;

import com.eslirodrigues.member.pricing.client.PricingServiceClient;
import com.eslirodrigues.member.pricing.dto.PriceUpdateEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PriceCacheService {

    private static final Logger log = LoggerFactory.getLogger(PriceCacheService.class);

    private final RedisTemplate<String, PriceUpdateEventDTO> redisTemplate;
    private final PricingServiceClient pricingServiceClient;
    private static final String PRICE_UPDATE_KEY_PREFIX = "price-update:";

    public PriceCacheService(
            RedisTemplate<String, PriceUpdateEventDTO> redisTemplate,
            PricingServiceClient pricingServiceClient
    ) {
        this.redisTemplate = redisTemplate;
        this.pricingServiceClient = pricingServiceClient;
    }

    private String generateCacheKey(PriceUpdateEventDTO.PriceType priceType) {
        return PRICE_UPDATE_KEY_PREFIX + priceType.getValue();
    }

    public void cachePriceUpdate(PriceUpdateEventDTO priceUpdate) {
        String key = generateCacheKey(priceUpdate.priceType());
        redisTemplate.opsForValue().set(key, priceUpdate);
    }

    public List<PriceUpdateEventDTO> getAllPrices() {
        List<String> keys = Arrays.stream(PriceUpdateEventDTO.PriceType.values())
                .map(this::generateCacheKey)
                .toList();

        List<PriceUpdateEventDTO> cachedPrices =
                redisTemplate.opsForValue().multiGet(keys);

        if (cachedPrices != null && !cachedPrices.contains(null)) {
            log.info("Returning all prices from cache.");
            return cachedPrices.stream().filter(Objects::nonNull).toList();
        }

        log.info("Cache miss for some prices, fetching from pricing-service.");
        List<PriceUpdateEventDTO> freshPrices = pricingServiceClient.getAllPrices();
        if (freshPrices != null) {
            freshPrices.forEach(this::cachePriceUpdate);
        }
        return freshPrices;
    }


}