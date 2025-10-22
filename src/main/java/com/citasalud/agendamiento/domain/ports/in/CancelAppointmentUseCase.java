package com.citasalud.agendamiento.domain.ports.in;

import com.citasalud.agendamiento.domain.model.Appointment;
import java.util.UUID;

public interface CancelAppointmentUseCase {
    Appointment cancelAppointment(UUID appointmentId, String userEmail, String reason);
}