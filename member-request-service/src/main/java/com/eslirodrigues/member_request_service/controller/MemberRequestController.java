package com.eslirodrigues.member_request_service.controller;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import com.eslirodrigues.member_request_service.service.MemberRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member-requests")
public class MemberRequestController {

    private final MemberRequestService memberRequestService;

    public MemberRequestController(MemberRequestService memberRequestService) {
        this.memberRequestService = memberRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void submitRequest(@RequestBody @Valid MemberRequestDTO memberRequestDTO) {
        memberRequestService.processSubmission(memberRequestDTO);
    }
}