package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.PriceUpdateEventDTO;
import com.eslirodrigues.member.service.PriceCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceCacheService priceCacheService;

    @GetMapping
    public ResponseEntity<List<PriceUpdateEventDTO>> getAllPrices() {
        return ResponseEntity.ok(priceCacheService.getAllPrices());
    }
}