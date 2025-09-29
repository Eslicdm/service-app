package com.eslirodrigues.member.controller;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.entity.Member;
import com.eslirodrigues.member.entity.ServiceType;
import com.eslirodrigues.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        public MemberService memberService() { return Mockito.mock(MemberService.class); }
    }

    @Test
    void createMember_shouldReturn201Created() throws Exception {
        Long managerId = 1L;
        var request = new CreateMemberRequest(
                "John Doe",
                "john.doe@test.com",
                LocalDate.of(1990, 1, 15),
                null,
                ServiceType.FULL_PRICE
        );

        var createdMember = new Member(
                100L,
                "John Doe",
                "john.doe@test.com",
                LocalDate.of(1990, 1, 15),
                null, ServiceType.FULL_PRICE,
                managerId
        );

        when(memberService.createMember(eq(managerId), any(CreateMemberRequest.class)))
                .thenReturn(createdMember);

        mockMvc.perform(post("/api/v1/members/{managerId}", managerId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")))
                .andExpect(jsonPath("$.managerId", is(managerId.intValue())));
    }

    @Test
    void getAllMembersByManagerId_shouldReturn200OkWithMemberList() throws Exception {
        Long managerId = 2L;
        var member1 = new Member(
                101L,
                "Jane Smith",
                "jane.smith@test.com",
                null,
                null,
                ServiceType.FREE,
                managerId
        );
        var member2 = new Member(
                102L,
                "Peter Jones",
                "peter.jones@test.com",
                null,
                null,
                ServiceType.FULL_PRICE,
                managerId
        );
        List<Member> memberList = List.of(member1, member2);

        when(memberService.getAllMembersByManagerId(managerId)).thenReturn(memberList);

        mockMvc.perform(get("/api/v1/members/{managerId}", managerId)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(101)))
                .andExpect(jsonPath("$[0].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].id", is(102)))
                .andExpect(jsonPath("$[1].name", is("Peter Jones")));
    }
}