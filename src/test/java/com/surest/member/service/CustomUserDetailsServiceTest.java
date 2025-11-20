package com.surest.member.service;

import com.surest.member.entity.User;
import com.surest.member.entity.Role;
import com.surest.member.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound_ReturnsUserDetails() {
        // Arrange
        Role role = new Role();
        role.setName("ROLE_USER");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setRoles(Set.of(role));

        when(userRepository.findByUsernameOrEmail("testUser", "testUser"))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("unknownUser", "unknownUser"))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknownUser"));
        assertEquals("User not exists by Username or Email", exception.getMessage());
    }
}
