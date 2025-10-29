package com.citasalud.agendamiento.unit;

import com.citasalud.agendamiento.domain.model.Appointment;
import com.citasalud.agendamiento.domain.ports.in.CancelAppointmentUseCase;
import com.citasalud.agendamiento.domain.ports.in.ModifyAppointmentUseCase;
import com.citasalud.agendamiento.domain.ports.in.ScheduleAppointmentUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.controller.AppointmentController;
import com.citasalud.agendamiento.infrastructure.in.web.dto.CancelAppointmentRequest;
import com.citasalud.agendamiento.infrastructure.in.web.dto.ModifyAppointmentRequest;
import com.citasalud.agendamiento.infrastructure.in.web.dto.ScheduleAppointmentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ScheduleAppointmentUseCase scheduleAppointmentUseCase;

    @Mock
    private ModifyAppointmentUseCase modifyAppointmentUseCase;

    @Mock
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    @Mock
    private Principal principal;

    @InjectMocks
    private AppointmentController appointmentController;

    private ObjectMapper objectMapper;
    private UUID appointmentId;
    private UUID slotId;
    private UUID affiliateId;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
        objectMapper = new ObjectMapper();

        appointmentId = UUID.randomUUID();
        slotId = UUID.randomUUID();
        affiliateId = UUID.randomUUID();

        appointment = new Appointment(UUID.randomUUID(), UUID.randomUUID(), affiliateId,
                OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), "scheduled", slotId);
        appointment.setId(appointmentId);
    }

    @Test
    void scheduleAppointment_Success() throws Exception {
        ScheduleAppointmentRequest request = new ScheduleAppointmentRequest();
        request.setAvailableSlotInstanceId(slotId);
        request.setAffiliateId(affiliateId);

        when(scheduleAppointmentUseCase.scheduleAppointment(slotId, affiliateId)).thenReturn(appointment);

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(appointmentId.toString()))
                .andExpect(jsonPath("$.status").value("scheduled"));

        verify(scheduleAppointmentUseCase).scheduleAppointment(slotId, affiliateId);
    }

    @Test
    void modifyAppointment_Success() throws Exception {
        UUID newSlotId = UUID.randomUUID();
        ModifyAppointmentRequest request = new ModifyAppointmentRequest();
        request.setNewAvailableSlotInstanceId(newSlotId);

        when(principal.getName()).thenReturn("test@example.com");
        when(modifyAppointmentUseCase.modifyAppointment(eq(appointmentId), eq(newSlotId), any(String.class)))
                .thenReturn(appointment);

        mockMvc.perform(put("/api/v1/appointments/{appointmentId}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentId.toString()));

        verify(modifyAppointmentUseCase).modifyAppointment(appointmentId, newSlotId, "test@example.com");
    }

    @Test
    void cancelAppointment_Success() throws Exception {
        CancelAppointmentRequest request = new CancelAppointmentRequest();
        request.setReason("Cancelada por el usuario");

        when(principal.getName()).thenReturn("test@example.com");
        when(cancelAppointmentUseCase.cancelAppointment(eq(appointmentId), any(String.class), any(String.class)))
                .thenReturn(appointment);

        mockMvc.perform(patch("/api/v1/appointments/{appointmentId}/cancel", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentId.toString()));

        verify(cancelAppointmentUseCase).cancelAppointment(appointmentId, "test@example.com", "Cancelada por el usuario");
    }

    @Test
    void cancelAppointment_NoReason() throws Exception {
        when(principal.getName()).thenReturn("test@example.com");
        when(cancelAppointmentUseCase.cancelAppointment(appointmentId, "test@example.com", "Cancelada por el usuario"))
                .thenReturn(appointment);

        mockMvc.perform(patch("/api/v1/appointments/{appointmentId}/cancel", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .principal(principal))
                .andExpect(status().isOk());

        verify(cancelAppointmentUseCase).cancelAppointment(appointmentId, "test@example.com", "Cancelada por el usuario");
    }
}