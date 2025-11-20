package com.surest.member.jwt;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    // Test secret (Base64 encoded)
    private final String testSecret = Base64.getEncoder().encodeToString("test-jwt-secret-key-which-is-very-long".getBytes());

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Inject mock property values via reflection since @Value won't work in unit test directly
        java.lang.reflect.Field secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtTokenProvider, testSecret);

        java.lang.reflect.Field expirationField = JwtTokenProvider.class.getDeclaredField("jwtExpirationDate");
        expirationField.setAccessible(true);
        expirationField.set(jwtTokenProvider, 3600000L); // 1 hour expiration
    }

    @Test
    void generateToken_ShouldReturnValidJWT() {
        when(authentication.getName()).thenReturn("testUser");

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals("testUser", jwtTokenProvider.getUsername(token));
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void getUsername_ShouldExtractCorrectUsername() {
        when(authentication.getName()).thenReturn("user123");
        String token = jwtTokenProvider.generateToken(authentication);

        String username = jwtTokenProvider.getUsername(token);

        assertEquals("user123", username);
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        String invalidToken = "this.is.an.invalid.token";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() throws Exception {
        // Manually generate a token with custom expiration in the past to simulate expiry
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject("expiredUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000 * 2)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(Base64.getDecoder().decode(testSecret)))
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertFalse(isValid);
    }
}
