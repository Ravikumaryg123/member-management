package com.surest.member.controller;

import com.surest.member.dto.JwtAuthResponse;
import com.surest.member.dto.LoginDto;
import com.surest.member.jwt.JwtTokenProvider;
import com.surest.member.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void loginShouldReturnJwtToken() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("dummyuser");
        loginDto.setPassword("dummypassword");

        String mockedToken = "mocked-jwt-token";
        Mockito.when(authService.login(Mockito.any(LoginDto.class))).thenReturn(mockedToken);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(mockedToken));
    }
}

