package com.citasalud.agendamiento.application.service;

import com.citasalud.agendamiento.domain.ports.in.LoginUseCase;
import com.citasalud.agendamiento.domain.ports.out.JwtProviderPort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtProviderPort jwtProviderPort;

    public AuthService(AuthenticationManager authenticationManager, JwtProviderPort jwtProviderPort) {
        this.authenticationManager = authenticationManager;
        this.jwtProviderPort = jwtProviderPort;
    }

    @Override
    public String login(String email, String password) {
        // Spring Security se encarga de buscar al usuario y validar la contraseña
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        // Si la autenticación es exitosa, la guardamos en el contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generamos y devolvemos el token JWT
        return jwtProviderPort.generateToken(authentication);
    }
}