package com.eslirodrigues.member.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonCreator
    public static ServiceType fromValue(String value) {
        for (ServiceType type : ServiceType.values()) {
            if (type.value.equalsIgnoreCase(value)) return type;
        }
        throw new IllegalArgumentException("Unknown enum value: '" + value + "'");
    }
}