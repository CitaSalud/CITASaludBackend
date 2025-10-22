package com.citasalud.agendamiento.application.service;

import com.citasalud.agendamiento.domain.exception.*;
import com.citasalud.agendamiento.domain.model.User;
import com.citasalud.agendamiento.domain.ports.in.ModifyAppointmentUseCase;
import com.citasalud.agendamiento.domain.ports.out.UserRepositoryPort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.citasalud.agendamiento.domain.model.Appointment;
import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.domain.ports.in.ScheduleAppointmentUseCase;
import com.citasalud.agendamiento.domain.ports.out.AppointmentRepositoryPort;
import com.citasalud.agendamiento.domain.ports.out.AvailableSlotInstanceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class AppointmentService implements ScheduleAppointmentUseCase, ModifyAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final AvailableSlotInstanceRepositoryPort availableSlotInstanceRepositoryPort;
    private final UserRepositoryPort userRepositoryPort; 

    public AppointmentService(AppointmentRepositoryPort appointmentRepositoryPort,
            AvailableSlotInstanceRepositoryPort availableSlotInstanceRepositoryPort,
            UserRepositoryPort userRepositoryPort) { 
        this.appointmentRepositoryPort = appointmentRepositoryPort;
        this.availableSlotInstanceRepositoryPort = availableSlotInstanceRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @Transactional
    public Appointment scheduleAppointment(UUID availableSlotInstanceId, UUID affiliateId) {
        AvailableSlotInstance slot = availableSlotInstanceRepositoryPort.findById(availableSlotInstanceId)
                .orElseThrow(() -> new SlotNotFoundException("El slot seleccionado no existe."));

        if (!"available".equalsIgnoreCase(slot.getStatus())) {
            throw new SlotNotAvailableException("Este slot ya no está disponible.");
        }

        slot.setStatus("booked");
        availableSlotInstanceRepositoryPort.save(slot);

        Appointment newAppointment = new Appointment(
                slot.getProfessionalId(),
                slot.getSiteId(),
                affiliateId,
                slot.getStartAt(),
                slot.getEndAt(),
                "scheduled",
                slot.getId());

        return appointmentRepositoryPort.save(newAppointment);
    }

    @Override
    @Transactional
    public Appointment modifyAppointment(UUID appointmentId, UUID newAvailableSlotInstanceId, String userEmail) {
        User affiliate = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userEmail));

        Appointment appointment = appointmentRepositoryPort.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("La cita con ID " + appointmentId + " no existe."));

        if (!appointment.getAffiliateId().equals(affiliate.getId())) {
            throw new UnauthorizedAppointmentAccessException("No tiene permisos para modificar esta cita.");
        }

        AvailableSlotInstance oldSlot = availableSlotInstanceRepositoryPort
                .findById(appointment.getAvailableSlotInstanceId())
                .orElseThrow(() -> new SlotNotFoundException("El slot antiguo de la cita no se encontró (ID: "
                        + appointment.getAvailableSlotInstanceId() + "). Error de integridad de datos."));

        AvailableSlotInstance newSlot = availableSlotInstanceRepositoryPort.findById(newAvailableSlotInstanceId)
                .orElseThrow(() -> new SlotNotFoundException(
                        "El nuevo slot seleccionado (ID: " + newAvailableSlotInstanceId + ") no existe."));

        if (!"available".equalsIgnoreCase(newSlot.getStatus())) {
            throw new SlotNotAvailableException("El nuevo slot seleccionado ya no está disponible.");
        }

        oldSlot.setStatus("available");
        newSlot.setStatus("booked");

        appointment.setAvailableSlotInstanceId(newSlot.getId());
        appointment.setStartAt(newSlot.getStartAt());
        appointment.setEndAt(newSlot.getEndAt());
        appointment.setProfessionalId(newSlot.getProfessionalId());
        appointment.setSiteId(newSlot.getSiteId());

        availableSlotInstanceRepositoryPort.save(oldSlot);
        availableSlotInstanceRepositoryPort.save(newSlot);
        return appointmentRepositoryPort.save(appointment);
    }
}