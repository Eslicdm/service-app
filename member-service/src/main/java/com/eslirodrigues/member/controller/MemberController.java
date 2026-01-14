package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.dto.UpdateMemberRequest;
import com.eslirodrigues.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @Operation(summary = "Get a member by ID", description = "Retrieves the details of a specific member by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved member details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have 'manager' or 'admin' role"),
            @ApiResponse(responseCode = "404", description = "Not Found - Member with the given ID does not exist")
    })
    public ResponseEntity<Member> getMemberById(
            @Parameter(description = "ID of the member to retrieve", required = true) @PathVariable Long memberId
    ) {
        Member member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    @Operation(summary = "Create a new member", description = "Creates a new member and associates them with the authenticated manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have 'manager' or 'admin' role")
    })
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
    @Operation(summary = "Update an existing member", description = "Updates the details of an existing member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have 'manager' or 'admin' role"),
            @ApiResponse(responseCode = "404", description = "Not Found - Member with the given ID does not exist")
    })
    public ResponseEntity<Member> updateMember(
            @Parameter(description = "ID of the member to update", required = true)
            @PathVariable Long memberId,
            @RequestBody UpdateMemberRequest request
    ) {
        Member updatedMember = memberService.updateMember(memberId, request);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('manager') or hasRole('admin')")
    @Operation(summary = "Delete a member", description = "Deletes a member by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have 'manager' or 'admin' role")
    })
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "ID of the member to delete", required = true)
            @PathVariable Long memberId
    ) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}