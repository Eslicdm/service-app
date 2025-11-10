package com.eslirodrigues.member.dto;

import com.eslirodrigues.member.entity.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request payload for creating a new member.")
public record CreateMemberRequest(
    @Schema(description = "Full name of the member.", example = "John Doe")
    String name,

    @Schema(description = "Unique email address of the member.", example = "john.doe@example.com")
    String email,

    @Schema(description = "Member's date of birth.", example = "30-12-2000")
    LocalDate birthDate,

    @Schema(description = "URL of the member's profile photo.", example = "https://example.com/photo.jpg")
    String photo,

    @Schema(description = "Type of service subscription for the member.", example = "FULL_PRICE")
    ServiceType serviceType
) {}