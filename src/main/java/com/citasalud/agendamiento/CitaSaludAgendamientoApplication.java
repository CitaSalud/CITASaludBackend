package com.citasalud.agendamiento;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "API de Agendamiento - CITASalud", 
    version = "1.0.0", 
    description = "Documentación de los endpoints para la gestión de citas, usuarios y disponibilidad."
))
public class CitaSaludAgendamientoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitaSaludAgendamientoApplication.class, args);
	}

}
