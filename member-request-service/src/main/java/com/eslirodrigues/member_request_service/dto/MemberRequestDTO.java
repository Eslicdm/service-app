package com.eslirodrigues.member_request_service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record MemberRequestDTO(
    @NotBlank @Email String email,
    @NotNull ServiceType serviceType
) {
    public enum ServiceType {
        FREE("free"),
        HALF_PRICE("half-price"),
        FULL_PRICE("full-price");

        private final String value;

        ServiceType(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        private static final Map<String, ServiceType> valueMap = Arrays.stream(values())
                .collect(Collectors.toMap(ServiceType::getValue, Function.identity()));
        
        private static final Map<String, ServiceType> nameMap = Arrays.stream(values())
                .collect(Collectors.toMap(ServiceType::name, Function.identity()));

        @JsonCreator
        public static ServiceType fromValue(String value) {
            if (value == null) throw new IllegalArgumentException("Value cannot be null");
            ServiceType type = valueMap.get(value.toLowerCase());
            if (type == null) type = nameMap.get(value.toUpperCase());
            if (type == null) throw new IllegalArgumentException("Unknown enum value: '" + value + "'");
            return type;
        }
    }
}