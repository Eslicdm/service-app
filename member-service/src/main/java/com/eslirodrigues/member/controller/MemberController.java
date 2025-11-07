package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.dto.UpdateMemberRequest;
import com.eslirodrigues.member.entity.Member;
import com.eslirodrigues.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<List<Member>> getAllMembersByManagerId(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String managerId = jwt.getSubject();

        List<Member> members = memberService.getAllMembersByManagerId(managerId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<Member> getMemberById(@PathVariable Long memberId) {
        Member member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<Member> createMember(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateMemberRequest request
    ) {
        String managerId = jwt.getSubject();

        Member createdMember = memberService.createMember(managerId, request);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long memberId,
            @RequestBody UpdateMemberRequest request
    ) {
        Member updatedMember = memberService.updateMember(memberId, request);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long memberId
    ) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}