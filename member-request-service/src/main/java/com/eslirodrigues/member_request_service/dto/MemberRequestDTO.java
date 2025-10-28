package com.eslirodrigues.member_request_service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public record MemberRequestDTO(
        @NotBlank @Email String email,
        @NotNull ServiceType serviceType
) {
    public enum ServiceType {
        FREE("free"),
        HALF_PRICE("half-price"),
        FULL_PRICE("full-price");

        private final String value;

        private static final Map<String, ServiceType> valueMap = Stream.of(values())
                .collect(toMap(
                        serviceType -> serviceType.value,
                        java.util.function.Function.identity())
                );
        private static final Map<String, ServiceType> nameMap = Stream.of(values())
                .collect(toMap(Enum::name, java.util.function.Function.identity()));

        ServiceType(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static ServiceType fromValue(String value) {
            return Optional.ofNullable(valueMap.get(value.toLowerCase()))
                    .or(() -> Optional.ofNullable(nameMap.get(value.toUpperCase())))
                    .orElseThrow(() ->
                            new IllegalArgumentException("Unknown enum value: '" + value + "'")
                    );
        }
    }
}