package com.eslirodrigues.member_request_service.controller

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO
import com.eslirodrigues.member_request_service.service.MemberRequestService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/member-requests")
class MemberRequestController(private val memberRequestService: MemberRequestService) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun submitRequest(
        @RequestBody @Valid memberRequestDTO: MemberRequestDTO
    ) = memberRequestService.processSubmission(memberRequestDTO)
}