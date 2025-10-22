package com.citasalud.agendamiento.domain.ports.in;

import com.citasalud.agendamiento.domain.model.Appointment;
import java.util.UUID;

public interface ModifyAppointmentUseCase {
    Appointment modifyAppointment(UUID appointmentId, UUID newAvailableSlotInstanceId, String userEmail);
}