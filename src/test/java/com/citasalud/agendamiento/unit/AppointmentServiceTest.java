package com.citasalud.agendamiento.unit;

import com.citasalud.agendamiento.application.service.AppointmentService;
import com.citasalud.agendamiento.domain.exception.*;
import com.citasalud.agendamiento.domain.model.Appointment;
import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.domain.model.User;
import com.citasalud.agendamiento.domain.ports.out.AppointmentRepositoryPort;
import com.citasalud.agendamiento.domain.ports.out.AvailableSlotInstanceRepositoryPort;
import com.citasalud.agendamiento.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepositoryPort;

    @Mock
    private AvailableSlotInstanceRepositoryPort availableSlotInstanceRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private AppointmentService appointmentService;

    private UUID slotId;
    private UUID affiliateId;
    private UUID professionalId;
    private UUID siteId;
    private UUID appointmentId;
    private String userEmail;
    private AvailableSlotInstance availableSlot;
    private User affiliate;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        slotId = UUID.randomUUID();
        affiliateId = UUID.randomUUID();
        professionalId = UUID.randomUUID();
        siteId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        userEmail = "test@example.com";

        availableSlot = new AvailableSlotInstance();
        availableSlot.setId(slotId);
        availableSlot.setProfessionalId(professionalId);
        availableSlot.setSiteId(siteId);
        availableSlot.setStartAt(OffsetDateTime.now().plusDays(1));
        availableSlot.setEndAt(OffsetDateTime.now().plusDays(1).plusHours(1));
        availableSlot.setStatus("available");

        affiliate = new User(userEmail, "Test User", "hashed-password", (short) 1);
        affiliate.setId(affiliateId);

        appointment = new Appointment(professionalId, siteId, affiliateId,
                availableSlot.getStartAt(), availableSlot.getEndAt(), "scheduled", slotId);
        appointment.setId(appointmentId);
    }

    @Test
    void scheduleAppointment_Success() {
        when(availableSlotInstanceRepositoryPort.findById(slotId)).thenReturn(Optional.of(availableSlot));
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.scheduleAppointment(slotId, affiliateId);

        assertNotNull(result);
        assertEquals("scheduled", result.getStatus());
        assertEquals(affiliateId, result.getAffiliateId());
        verify(availableSlotInstanceRepositoryPort).save(availableSlot);
        assertEquals("booked", availableSlot.getStatus());
    }

    @Test
    void scheduleAppointment_SlotNotFound() {
        when(availableSlotInstanceRepositoryPort.findById(slotId)).thenReturn(Optional.empty());

        assertThrows(SlotNotFoundException.class,
                () -> appointmentService.scheduleAppointment(slotId, affiliateId));
    }

    @Test
    void scheduleAppointment_SlotNotAvailable() {
        availableSlot.setStatus("booked");
        when(availableSlotInstanceRepositoryPort.findById(slotId)).thenReturn(Optional.of(availableSlot));

        assertThrows(SlotNotAvailableException.class,
                () -> appointmentService.scheduleAppointment(slotId, affiliateId));
    }

    @Test
    void modifyAppointment_Success() {
        UUID newSlotId = UUID.randomUUID();
        AvailableSlotInstance newSlot = new AvailableSlotInstance();
        newSlot.setId(newSlotId);
        newSlot.setProfessionalId(UUID.randomUUID());
        newSlot.setSiteId(UUID.randomUUID());
        newSlot.setStartAt(OffsetDateTime.now().plusDays(2));
        newSlot.setEndAt(OffsetDateTime.now().plusDays(2).plusHours(1));
        newSlot.setStatus("available");

        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.of(affiliate));
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(availableSlotInstanceRepositoryPort.findById(slotId)).thenReturn(Optional.of(availableSlot));
        when(availableSlotInstanceRepositoryPort.findById(newSlotId)).thenReturn(Optional.of(newSlot));
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.modifyAppointment(appointmentId, newSlotId, userEmail);

        assertNotNull(result);
        verify(availableSlotInstanceRepositoryPort).save(availableSlot);
        verify(availableSlotInstanceRepositoryPort).save(newSlot);
        assertEquals("available", availableSlot.getStatus());
        assertEquals("booked", newSlot.getStatus());
    }

    @Test
    void modifyAppointment_UserNotFound() {
        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> appointmentService.modifyAppointment(appointmentId, UUID.randomUUID(), userEmail));
    }

    @Test
    void modifyAppointment_AppointmentNotFound() {
        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.of(affiliate));
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.modifyAppointment(appointmentId, UUID.randomUUID(), userEmail));
    }

    @Test
    void modifyAppointment_UnauthorizedAccess() {
        User otherUser = new User("other@example.com", "Other User", "hashed-password", (short) 1);
        otherUser.setId(UUID.randomUUID());

        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.of(otherUser));
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(UnauthorizedAppointmentAccessException.class,
                () -> appointmentService.modifyAppointment(appointmentId, UUID.randomUUID(), userEmail));
    }

    @Test
    void cancelAppointment_Success() {
        appointment.setStatus("scheduled");
        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.of(affiliate));
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(availableSlotInstanceRepositoryPort.findById(slotId)).thenReturn(Optional.of(availableSlot));
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.cancelAppointment(appointmentId, userEmail, "Cancelada por el usuario");

        assertNotNull(result);
        assertEquals("cancelled", result.getStatus());
        assertEquals("available", availableSlot.getStatus());
    }

    @Test
    void cancelAppointment_NotScheduled() {
        appointment.setStatus("cancelled");
        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.of(affiliate));
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(CancellationNotAllowedException.class,
                () -> appointmentService.cancelAppointment(appointmentId, userEmail, "Reason"));
    }
}