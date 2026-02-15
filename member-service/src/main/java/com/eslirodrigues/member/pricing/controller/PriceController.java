package com.eslirodrigues.member.pricing.controller;

import com.eslirodrigues.member.pricing.dto.PriceUpdateEventDTO;
import com.eslirodrigues.member.pricing.service.PriceCacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/members/prices")
public class PriceController {

    private final PriceCacheService priceCacheService;

    public PriceController(PriceCacheService priceCacheService) {
        this.priceCacheService = priceCacheService;
    }

    @GetMapping
    public ResponseEntity<List<PriceUpdateEventDTO>> getAllPrices() {
        return ResponseEntity.ok(priceCacheService.getAllPrices());
    }
}