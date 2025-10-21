package com.citasalud.agendamiento.domain.ports.out;

import org.springframework.security.core.Authentication;

public interface JwtProviderPort {
    String generateToken(Authentication authentication);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}