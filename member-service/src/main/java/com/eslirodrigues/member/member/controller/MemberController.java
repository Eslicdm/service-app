package com.eslirodrigues.member.member.controller;

import com.eslirodrigues.member.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.member.dto.UpdateMemberRequest;
import com.eslirodrigues.member.core.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.eslirodrigues.member.member.service.MemberService;
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
@Tag(name = "Member Management", description = "APIs for creating, retrieving, updating, and deleting members.")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    @Operation(
            summary = "Get all members for a manager",
            description = "Retrieves a list of all members associated with the authenticated manager. The manager is identified by the subject of the JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of members"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have 'manager' or 'admin' role")
    })
    public ResponseEntity<List<Member>> getAllMembersByManagerId(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String managerId = jwt.getSubject();
        List<Member> members = memberService.getAllMembersByManagerId(managerId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<Member> getMemberById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long memberId
    ) {
        String managerId = jwt.getSubject();
        Member member = memberService.getMemberById(managerId, memberId);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    public ResponseEntity<Member> createMember(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateMemberRequest request
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