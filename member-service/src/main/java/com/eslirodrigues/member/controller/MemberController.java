package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.dto.MemberRequestEvent;
import com.eslirodrigues.member.entity.Member;
import com.eslirodrigues.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/{managerId}")
    public ResponseEntity<List<Member>> getAllMembersByManagerId(
            @PathVariable Long managerId
    ) {
        List<Member> members = memberService.getAllMembersByManagerId(managerId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/member-requests")
    public ResponseEntity<List<MemberRequestEvent>> getNewMemberRequests() {
        List<MemberRequestEvent> requests = memberService.getNewMemberRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/members/{managerId}")
    public ResponseEntity<Member> createMember(
            @PathVariable Long managerId,
            @RequestBody CreateMemberRequest request
    ) {
        Member createdMember = memberService.createMember(managerId, request);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }
}