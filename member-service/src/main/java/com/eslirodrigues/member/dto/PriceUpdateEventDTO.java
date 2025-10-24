package com.eslirodrigues.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.stream.Collectors.*;

public record PriceUpdateEventDTO(
    String id,
    PriceType priceType,
    BigDecimal value,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public enum PriceType {
        FREE("free"),
        HALF_PRICE("half-price"),
        FULL_PRICE("full-price");

        private final String value;

        private static final Map<String, PriceType> valueMap = Stream.of(values())
                .collect(toMap(
                        priceType -> priceType.value,
                        java.util.function.Function.identity())
                );
        private static final Map<String, PriceType> nameMap = Stream.of(values())
                .collect(toMap(Enum::name, java.util.function.Function.identity()));

        PriceType(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static PriceType fromValue(String value) {
            return Optional.ofNullable(valueMap.get(value.toLowerCase()))
                    .or(() -> Optional.ofNullable(nameMap.get(value.toUpperCase())))
                    .orElseThrow(() -> new IllegalArgumentException("Unknown enum value: '" + value + "'"));
        }
    }
}