package com.citasalud.agendamiento.infrastructure.in.web.controller;

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

    public AppointmentController(ScheduleAppointmentUseCase scheduleAppointmentUseCase,
            ModifyAppointmentUseCase modifyAppointmentUseCase) {
        this.scheduleAppointmentUseCase = scheduleAppointmentUseCase;
        this.modifyAppointmentUseCase = modifyAppointmentUseCase;
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
}