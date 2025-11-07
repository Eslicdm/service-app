package com.eslirodrigues.pricing_service.controller;

import com.eslirodrigues.pricing_service.dto.PriceUpdateDTO;
import com.eslirodrigues.pricing_service.entity.Price;
import com.eslirodrigues.pricing_service.entity.PriceType;
import jakarta.validation.Valid;
import com.eslirodrigues.pricing_service.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping
    public ResponseEntity<List<Price>> getAllPrices() {
        return ResponseEntity.ok(priceService.getAllPrices());
    }

    @PutMapping("/{priceType}")
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<Price> updatePrice(
            @PathVariable PriceType priceType,
            @RequestBody @Valid PriceUpdateDTO priceUpdateDTO
    ) {
        return ResponseEntity.ok(priceService.updatePrice(priceType, priceUpdateDTO));
    }
}