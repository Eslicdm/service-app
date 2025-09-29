package com.eslirodrigues.pricing_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PriceUpdateDTO(
        @NotNull @DecimalMin(value = "0.0")
        BigDecimal value,
        @NotBlank
        String description
) {}