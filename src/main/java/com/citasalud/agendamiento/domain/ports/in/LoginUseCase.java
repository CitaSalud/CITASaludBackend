package com.citasalud.agendamiento.domain.ports.in;

public interface LoginUseCase {
    String login(String email, String password);
}