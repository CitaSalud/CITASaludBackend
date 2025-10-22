package com.citasalud.agendamiento.domain.ports.in;

import com.citasalud.agendamiento.domain.model.Appointment;
import java.util.UUID;

public interface ModifyAppointmentUseCase {
    /**
     * Modifica una cita existente, moviéndola a un nuevo slot.
     * @param appointmentId El ID de la cita a modificar.
     * @param newAvailableSlotInstanceId El ID del nuevo slot al que se moverá la cita.
     * @param userEmail El email del usuario autenticado (para verificar permisos).
     * @return La cita actualizada.
     */
    Appointment modifyAppointment(UUID appointmentId, UUID newAvailableSlotInstanceId, String userEmail);
}