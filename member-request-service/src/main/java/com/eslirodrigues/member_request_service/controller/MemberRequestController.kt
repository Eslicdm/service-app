package com.eslirodrigues.member_request_service.controller;

import com.eslirodrigues.member_request_service.dto.MemberRequestDTO;
import com.eslirodrigues.member_request_service.service.MemberRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member-requests")
@RequiredArgsConstructor
public class MemberRequestController {

    private final MemberRequestService memberRequestService;

    @PostMapping
    public ResponseEntity<Void> submitRequest(
            @RequestBody @Valid MemberRequestDTO memberRequestDTO
    ) {
        memberRequestService.processSubmission(memberRequestDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}