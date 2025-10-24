package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.entity.Member;
import com.eslirodrigues.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members/{managerId}")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembersByManagerId(
            @PathVariable Long managerId
    ) {
        List<Member> members = memberService.getAllMembersByManagerId(managerId);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity<Member> createMember(
            @PathVariable Long managerId,
            @RequestBody CreateMemberRequest request
    ) {
        Member createdMember = memberService.createMember(managerId, request);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }
}