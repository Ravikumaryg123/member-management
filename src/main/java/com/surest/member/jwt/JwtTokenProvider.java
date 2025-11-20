package com.surest.member.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    private Key getSigningKey() {
        // Decode Base64-encoded secret and create HMAC key
        byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate JWT token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)                     // use setSubject(), NOT subject()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())                 // set signing key here
                .compact();
    }

    // Get username from JWT token
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())           // use setSigningKey()
                .build()
                .parseClaimsJws(token)                     // parses and validates JWT signature
                .getBody();

        return claims.getSubject();
    }

    // Validate JWT token validity and signature
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);                  // will throw if invalid
            return true;
        } catch (Exception e) {
            // log the exception or handle invalid token
            return false;
        }
    }
}

