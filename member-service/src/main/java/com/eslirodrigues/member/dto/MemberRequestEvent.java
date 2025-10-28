package com.eslirodrigues.member.dto;

import com.eslirodrigues.member.entity.ServiceType;

public record MemberRequestEvent(
    String email,
    ServiceType serviceType
) {}