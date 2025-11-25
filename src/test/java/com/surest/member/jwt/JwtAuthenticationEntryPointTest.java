package com.surest.member.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtAuthenticationEntryPointTest {

    @Test
    void testCommence_ShouldSendUnauthorizedError() throws IOException, ServletException {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(authException.getMessage()).thenReturn("Unauthorized error");

        entryPoint.commence(request, response, authException);

        // Verify that sendError was called with 401 status and the exception message
        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), eq("Unauthorized error"));
    }
}
