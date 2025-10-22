package com.citasalud.agendamiento.domain.exception;

public class UnauthorizedAppointmentAccessException extends RuntimeException {
    public UnauthorizedAppointmentAccessException(String message) {
        super(message);
    }
}