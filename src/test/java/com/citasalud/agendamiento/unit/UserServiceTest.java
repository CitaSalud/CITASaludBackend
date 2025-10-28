package com.citasalud.agendamiento.unit;

import com.citasalud.agendamiento.application.service.UserService;
import com.citasalud.agendamiento.domain.exception.UserAlreadyExistsException;
import com.citasalud.agendamiento.domain.model.Role;
import com.citasalud.agendamiento.domain.model.User;
import com.citasalud.agendamiento.domain.ports.out.RoleRepositoryPort;
import com.citasalud.agendamiento.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private String email;
    private String nombre;
    private String plainPassword;
    private String roleName;
    private String hashedPassword;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        nombre = "Test User";
        plainPassword = "password123";
        roleName = "AFFILIATE";
        hashedPassword = "hashed-password-123";

        role = new Role();
        role.setId((short) 1);
        role.setName(roleName);

        user = new User(email, nombre, hashedPassword, role.getId());
        user.setId(UUID.randomUUID());
    }

    @Test
    void createUser_Success() {
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());
        when(roleRepositoryPort.findByName(roleName)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(plainPassword)).thenReturn(hashedPassword);
        when(userRepositoryPort.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(email, nombre, plainPassword, roleName);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(nombre, result.getNombre());
        assertEquals(hashedPassword, result.getHashedPassword());
        assertEquals(role.getId(), result.getRoleId());

        verify(userRepositoryPort).findByEmail(email);
        verify(roleRepositoryPort).findByName(roleName);
        verify(passwordEncoder).encode(plainPassword);
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void createUser_UserAlreadyExists() {
        User existingUser = new User(email, nombre, hashedPassword, role.getId());
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(email, nombre, plainPassword, roleName));

        verify(userRepositoryPort).findByEmail(email);
        verify(roleRepositoryPort, never()).findByName(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void createUser_RoleNotFound() {
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());
        when(roleRepositoryPort.findByName(roleName)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(email, nombre, plainPassword, roleName));

        verify(userRepositoryPort).findByEmail(email);
        verify(roleRepositoryPort).findByName(roleName);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepositoryPort, never()).save(any());
    }
}