package com.citasalud.agendamiento.unit.controller;

import com.citasalud.agendamiento.domain.model.User;
import com.citasalud.agendamiento.domain.ports.in.CreateUserUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.controller.UserController;
import com.citasalud.agendamiento.infrastructure.in.web.dto.CreateUserRequest;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // ✅ Registramos el GlobalExceptionHandler
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setNombre("John Doe");
        request.setPassword("secret");
        request.setRoleName("AFFILIATE");

        User mockUser = new User("test@example.com", "John Doe", "hashed_secret", (short) 2);
        mockUser.setId(UUID.randomUUID());

        when(createUserUseCase.createUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nombre").value("John Doe"))
                .andExpect(jsonPath("$.roleId").value(2));
    }

    @Test
    void shouldReturnInternalServerErrorWhenUseCaseThrowsException() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("fail@example.com");
        request.setNombre("Jane Doe");
        request.setPassword("secret");
        request.setRoleName("AFFILIATE");

        when(createUserUseCase.createUser(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
