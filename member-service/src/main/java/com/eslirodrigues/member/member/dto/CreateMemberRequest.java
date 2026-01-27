package com.eslirodrigues.member.member.dto;

import com.eslirodrigues.member.core.entity.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@Schema(description = "Request payload for creating a new member.")
public record CreateMemberRequest(
    @Schema(description = "Full name of the member.", example = "John Doe")
    @NotBlank(message = "Name cannot be blank")
    String name,

    @Schema(description = "Unique email address of the member.", example = "john.doe@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    String email,

    @Schema(description = "Member's date of birth.", example = "2000-12-30")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate,

    @Schema(description = "URL of the member's profile photo.", example = "https://example.com/photo.jpg")
    String photo,

    @Schema(description = "Type of service subscription for the member.", example = "FULL_PRICE")
    @NotNull(message = "Service type cannot be null")
    ServiceType serviceType
) {}