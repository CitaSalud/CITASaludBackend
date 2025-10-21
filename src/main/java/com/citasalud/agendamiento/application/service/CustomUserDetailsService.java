package com.citasalud.agendamiento.application.service;

import com.citasalud.agendamiento.domain.ports.out.RoleRepositoryPort;
import com.citasalud.agendamiento.domain.ports.out.UserRepositoryPort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;

    public CustomUserDetailsService(UserRepositoryPort userRepositoryPort, RoleRepositoryPort roleRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.citasalud.agendamiento.domain.model.User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Buscamos el nombre del rol (asumiendo que tienes findById en RoleRepositoryPort)
        String roleName = roleRepositoryPort.findById(user.getRoleId())
                            .map(role -> "ROLE_" + role.getName().toUpperCase()) // Ej: "ROLE_AFFILIATE"
                            .orElse("ROLE_DEFAULT");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        return new User(user.getEmail(), user.getHashedPassword(), authorities);
    }
}