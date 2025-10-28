package com.citasalud.agendamiento.unit;

import com.citasalud.agendamiento.application.service.AuthService;
import com.citasalud.agendamiento.domain.ports.out.JwtProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProviderPort jwtProviderPort;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private String email;
    private String password;
    private String token;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        password = "password123";
        token = "jwt-token-123";
    }

    @Test
    void login_Success() {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(jwtProviderPort.generateToken(authentication)).thenReturn(token);

        String result = authService.login(email, password);

        assertEquals(token, result);
        verify(authenticationManager).authenticate(authToken);
        verify(jwtProviderPort).generateToken(authentication);
        // Verify that authentication is set in SecurityContext
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void login_AuthenticationFails() {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(authToken))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class,
                () -> authService.login(email, password));

        verify(authenticationManager).authenticate(authToken);
        verify(jwtProviderPort, never()).generateToken(any());
    }
}