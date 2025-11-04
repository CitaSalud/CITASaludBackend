package com.citasalud.agendamiento.unit.controller;

import com.citasalud.agendamiento.domain.ports.in.LoginUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.controller.AuthController;
import com.citasalud.agendamiento.infrastructure.in.web.dto.LoginRequest;
import com.citasalud.agendamiento.domain.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest {

    @Mock
    private LoginUseCase loginUseCase;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler()) //
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnTokenWhenLoginIsSuccessful() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String fakeToken = "fake-jwt-token";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        when(loginUseCase.login(email, password)).thenReturn(fakeToken);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenLoginFails() throws Exception {
        // Arrange
        String email = "wrong@example.com";
        String password = "invalid";
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        when(loginUseCase.login(email, password))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()) // ✅ ahora manejado por GlobalExceptionHandler
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

}
