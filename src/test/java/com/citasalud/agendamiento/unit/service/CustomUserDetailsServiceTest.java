package com.citasalud.agendamiento.unit.service;

import com.citasalud.agendamiento.application.service.CustomUserDetailsService;
import com.citasalud.agendamiento.domain.model.Role;
import com.citasalud.agendamiento.domain.model.User;
import com.citasalud.agendamiento.domain.ports.out.RoleRepositoryPort;
import com.citasalud.agendamiento.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {


    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("test@example.com", "Test User", "hashed123", (short) 1);
        user.setId(UUID.randomUUID());

        role = new Role();
        role.setId((short) 1);
        role.setName("affiliate");
    }

    @Test
    void testLoadUserByUsername_UserFoundWithRole() {
        when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(roleRepositoryPort.findById((short) 1)).thenReturn(Optional.of(role));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("hashed123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_AFFILIATE")));

        verify(userRepositoryPort).findByEmail("test@example.com");
        verify(roleRepositoryPort).findById((short) 1);
    }

    @Test
    void testLoadUserByUsername_UserFoundWithoutRole() {
        when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(roleRepositoryPort.findById((short) 1)).thenReturn(Optional.empty());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_DEFAULT")));

        verify(userRepositoryPort).findByEmail("test@example.com");
        verify(roleRepositoryPort).findById((short) 1);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepositoryPort.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("notfound@example.com"));

        verify(userRepositoryPort).findByEmail("notfound@example.com");
        verify(roleRepositoryPort, never()).findById(any());
    }

}
