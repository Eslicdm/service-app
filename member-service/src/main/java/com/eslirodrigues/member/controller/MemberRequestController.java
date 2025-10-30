package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.MemberRequestEvent;
import com.eslirodrigues.member.service.MemberRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members/requests")
@RequiredArgsConstructor
public class MemberRequestController {

    private final MemberRequestService memberRequestService;

    @GetMapping
    public ResponseEntity<List<MemberRequestEvent>> getNewMemberRequests() {
        List<MemberRequestEvent> requests = memberRequestService.getNewMemberRequests();
        return ResponseEntity.ok(requests);
    }
}
