package com.eslirodrigues.member.dto;

import com.eslirodrigues.member.entity.ServiceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateMemberRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,

        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        String photo,

        ServiceType serviceType
) {
}