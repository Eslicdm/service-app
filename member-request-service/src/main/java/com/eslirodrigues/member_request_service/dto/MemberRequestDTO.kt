package com.eslirodrigues.member_request_service.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MemberRequestDTO(
    @field:NotBlank @field:Email val email: String,
    @field:NotNull val serviceType: ServiceType
) {
    enum class ServiceType(@JsonValue val value: String) {
        FREE("free"),
        HALF_PRICE("half-price"),
        FULL_PRICE("full-price");

        companion object {
            private val valueMap = entries.associateBy { type -> type.value }
            private val nameMap = entries.associateBy { type -> type.name }

            @JvmStatic
            @JsonCreator
            fun fromValue(value: String): ServiceType =
                valueMap[value.lowercase()]
                    ?: nameMap[value.uppercase()]
                    ?: throw IllegalArgumentException("Unknown enum value: '$value'")
        }
    }
}