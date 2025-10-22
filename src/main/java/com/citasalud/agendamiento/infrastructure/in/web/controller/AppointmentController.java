package com.citasalud.agendamiento.infrastructure.in.web.controller;

import com.citasalud.agendamiento.domain.ports.in.CancelAppointmentUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.dto.CancelAppointmentRequest;
import com.citasalud.agendamiento.domain.ports.in.ModifyAppointmentUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.dto.ModifyAppointmentRequest;
import java.security.Principal;
import java.util.UUID;
import com.citasalud.agendamiento.domain.model.Appointment;
import com.citasalud.agendamiento.domain.ports.in.ScheduleAppointmentUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.dto.ScheduleAppointmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final ScheduleAppointmentUseCase scheduleAppointmentUseCase;
    private final ModifyAppointmentUseCase modifyAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;

    public AppointmentController(ScheduleAppointmentUseCase scheduleAppointmentUseCase,
            ModifyAppointmentUseCase modifyAppointmentUseCase,
            CancelAppointmentUseCase cancelAppointmentUseCase) {
        this.scheduleAppointmentUseCase = scheduleAppointmentUseCase;
        this.modifyAppointmentUseCase = modifyAppointmentUseCase;
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
    }

    @PostMapping
    public ResponseEntity<Appointment> scheduleAppointment(@RequestBody ScheduleAppointmentRequest request) {
        Appointment createdAppointment = scheduleAppointmentUseCase.scheduleAppointment(
                request.getAvailableSlotInstanceId(),
                request.getAffiliateId());

        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<Appointment> modifyAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody ModifyAppointmentRequest request,
            Principal principal // Obtenemos al usuario autenticado desde el token JWT
    ) {
        // El 'name' del Principal es el email que guardamos en el token
        String userEmail = principal.getName();

        Appointment modifiedAppointment = modifyAppointmentUseCase.modifyAppointment(
                appointmentId,
                request.getNewAvailableSlotInstanceId(),
                userEmail // Pasamos el email para la validación de seguridad
        );

        return ResponseEntity.ok(modifiedAppointment);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody(required = false) CancelAppointmentRequest request,
            Principal principal
    ) {
        String userEmail = principal.getName();
        String reason = (request != null) ? request.getReason() : "Cancelada por el usuario";

        Appointment cancelledAppointment = cancelAppointmentUseCase.cancelAppointment(
                appointmentId,
                userEmail,
                reason);

        return ResponseEntity.ok(cancelledAppointment);
    }
}