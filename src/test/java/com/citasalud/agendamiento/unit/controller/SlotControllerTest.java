package com.citasalud.agendamiento.unit.controller;

import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.domain.ports.in.FindAvailableSlotsUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.controller.SlotController;
import com.citasalud.agendamiento.domain.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SlotControllerTest {

    @Mock
    private FindAvailableSlotsUseCase findAvailableSlotsUseCase;

    @InjectMocks
    private SlotController slotController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 👉 Registramos el GlobalExceptionHandler para capturar errores en los tests
        mockMvc = MockMvcBuilders
                .standaloneSetup(slotController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnAvailableSlotsSuccessfully() throws Exception {
        // Arrange
        UUID slotId = UUID.randomUUID();
        UUID professionalId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();
        OffsetDateTime startAt = OffsetDateTime.now().plusDays(1);
        OffsetDateTime endAt = startAt.plusHours(1);

        AvailableSlotInstance mockSlot = new AvailableSlotInstance();
        mockSlot.setId(slotId);
        mockSlot.setProfessionalId(professionalId);
        mockSlot.setSiteId(siteId);
        mockSlot.setStartAt(startAt);
        mockSlot.setEndAt(endAt);
        mockSlot.setStatus("available");

        when(findAvailableSlotsUseCase.findAvailableSlots(any(), any(), any(), any()))
                .thenReturn(List.of(mockSlot));

        // Act & Assert
        mockMvc.perform(get("/api/v1/slots")
                        .param("professionalId", professionalId.toString())
                        .param("siteId", siteId.toString())
                        .param("startDate", startAt.toString())
                        .param("endDate", endAt.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slotId").value(slotId.toString()))
                .andExpect(jsonPath("$[0].professionalId").value(professionalId.toString()))
                .andExpect(jsonPath("$[0].siteId").value(siteId.toString()))
                .andExpect(jsonPath("$[0].startAt").exists())
                .andExpect(jsonPath("$[0].endAt").exists());
    }

    @Test
    void shouldReturnEmptyListWhenNoSlotsAvailable() throws Exception {
        // Arrange
        when(findAvailableSlotsUseCase.findAvailableSlots(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/slots")
                        .param("professionalId", UUID.randomUUID().toString())
                        .param("siteId", UUID.randomUUID().toString())
                        .param("startDate", OffsetDateTime.now().toString())
                        .param("endDate", OffsetDateTime.now().plusHours(1).toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnInternalServerErrorWhenUseCaseFails() throws Exception {
        // Arrange
        when(findAvailableSlotsUseCase.findAvailableSlots(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Error fetching slots"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/slots")
                        .param("professionalId", UUID.randomUUID().toString())
                        .param("siteId", UUID.randomUUID().toString())
                        .param("startDate", OffsetDateTime.now().toString())
                        .param("endDate", OffsetDateTime.now().plusHours(1).toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Error fetching slots"));
    }
}
