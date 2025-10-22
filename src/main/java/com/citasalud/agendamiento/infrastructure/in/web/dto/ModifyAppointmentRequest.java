package com.citasalud.agendamiento.infrastructure.in.web.dto;

import java.util.UUID;

public class ModifyAppointmentRequest {

    private UUID newAvailableSlotInstanceId;

    // Getters y Setters
    public UUID getNewAvailableSlotInstanceId() {
        return newAvailableSlotInstanceId;
    }

    public void setNewAvailableSlotInstanceId(UUID newAvailableSlotInstanceId) {
        this.newAvailableSlotInstanceId = newAvailableSlotInstanceId;
    }
}