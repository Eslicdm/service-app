package com.eslirodrigues.member.dto;

import com.eslirodrigues.member.entity.ServiceType;

import java.time.LocalDate;

public record CreateMemberRequest(
    String name,
    String email,
    LocalDate birthDate,
    String photo,
    ServiceType serviceType
) {}