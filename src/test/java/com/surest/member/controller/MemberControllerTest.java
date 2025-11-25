package com.surest.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member.config.SpringSecurityConfig;
import com.surest.member.config.TestSecurityConfig;
import com.surest.member.dto.MemberDto;
import com.surest.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import(TestSecurityConfig.class)
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/member - Add Member")
    void testAddMember() throws Exception {
        MemberDto dto = new MemberDto();
        dto.setFirstName("Sandeep");

        Mockito.when(memberService.createMember(any()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Sandeep"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/v1/member/{uuid} - Get Member by ID")
    void testGetMemberById() throws Exception {
        UUID id = UUID.randomUUID();
        MemberDto dto = new MemberDto();
        dto.setId(id);

        Mockito.when(memberService.getMemberById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/member/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/v1/member/members - Get Members with Filters")
    void testGetMembersWithFilters() throws Exception {
        MemberDto dto = new MemberDto();
        dto.setFirstName("Anand");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName"));
        Page<MemberDto> page = new PageImpl<>(List.of(dto), pageable, 1);

        Mockito.when(memberService.getMembersWithFilters(any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/member/members")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Anand"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/v1/member - Get All Members")
    void testGetAllMembers() throws Exception {
        MemberDto dto = new MemberDto();
        dto.setFirstName("Bharath");

        Mockito.when(memberService.getAllMemberss())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Bharath"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v1/member/{uuid} - Delete Member")
    void testDeleteMember() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/member/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("Member Deleted Successfully"));

        Mockito.verify(memberService).deleteMemberById(id);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/v1/member/{uuid} - Update Member")
    void testUpdateMember() throws Exception {
        UUID id = UUID.randomUUID();
        MemberDto dto = new MemberDto();
        dto.setFirstName("Govind");

        Mockito.when(memberService.updateMember(eq(id), any()))
                .thenReturn(dto);

        mockMvc.perform(put("/api/v1/member/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Govind"));
    }
}
