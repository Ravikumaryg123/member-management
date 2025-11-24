package com.surest.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member.dto.MemberDto;
import com.surest.member.jwt.JwtAuthenticationFilter;
import com.surest.member.jwt.JwtTokenProvider;
import com.surest.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MemberDto sampleMember;

    @BeforeEach
    void setup() {
        sampleMember = new MemberDto();
        sampleMember.setId(UUID.randomUUID());
        sampleMember.setFirstName("Abid");
        sampleMember.setLastName("Hussian");
        sampleMember.setDateOfBirth(LocalDate.of(1980, 1, 1));
        sampleMember.setEmail("abidhussian@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMemberAsAdminShouldReturnCreatedMember() throws Exception {
        Mockito.when(memberService.createMember(any(MemberDto.class))).thenReturn(sampleMember);

        mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleMember.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("Abid"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void listMembersAsUserShouldReturnPage() throws Exception {
        Page<MemberDto> page = new PageImpl<>(List.of(sampleMember), PageRequest.of(0, 10), 1);
        Mockito.when(memberService.getMembersWithFilters(anyString(), anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/member/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleMember.getId().toString()))
                .andExpect(jsonPath("$.content[0].firstName").value("Abid"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getByIdAsUserShouldReturnMember() throws Exception {
        Mockito.when(memberService.getMemberById(any(UUID.class))).thenReturn(sampleMember);

        mockMvc.perform(get("/api/v1/member/{uuid}", sampleMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Abid"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMemberAsAdminShouldReturnUpdatedMember() throws Exception {
        MemberDto updated = new MemberDto();
        updated.setId(sampleMember.getId());
        updated.setFirstName("Sunil");
        updated.setLastName("M");
        updated.setDateOfBirth(LocalDate.of(1992, 2, 2));
        updated.setEmail("Sunilm@example.com");

        Mockito.when(memberService.updateMember(eq(sampleMember.getId()), any(MemberDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/member/{uuid}", sampleMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Sunil"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMemberAsAdminShouldReturnOk() throws Exception {
        Mockito.doNothing().when(memberService).deleteMemberById(any(UUID.class));

        mockMvc.perform(delete("/api/v1/member/{uuid}", sampleMember.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Member Deleted Successfully"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void listMembersSortSingleFieldShouldUseAscByDefault() throws Exception {
        Page<MemberDto> page = new PageImpl<>(List.of(sampleMember));
        Mockito.when(memberService.getMembersWithFilters(anyString(), anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/member/members")
                        .param("sort", "firstName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleMember.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMemberInvalidRequestShouldReturnBadRequest() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/member"));
    }
}
