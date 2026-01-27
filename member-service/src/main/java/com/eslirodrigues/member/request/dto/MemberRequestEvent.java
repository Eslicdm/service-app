package com.eslirodrigues.member.request.dto;

import com.eslirodrigues.member.core.entity.ServiceType;

public record MemberRequestEvent(
    String email,
    ServiceType serviceType
) {}