package com.citasalud.agendamiento.domain.ports.out;

import java.util.Optional;
import java.util.UUID;
import com.citasalud.agendamiento.domain.model.Appointment;

public interface AppointmentRepositoryPort {
    Appointment save(Appointment appointment);

    Optional<Appointment> findById(UUID appointmentId);
}