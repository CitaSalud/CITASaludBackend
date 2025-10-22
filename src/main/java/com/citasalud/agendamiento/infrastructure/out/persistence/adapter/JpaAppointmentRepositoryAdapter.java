package com.citasalud.agendamiento.infrastructure.out.persistence.adapter;

import com.citasalud.agendamiento.domain.model.Appointment;
import com.citasalud.agendamiento.domain.ports.out.AppointmentRepositoryPort;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AppointmentEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.AppointmentMapper;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaAppointmentRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final SpringDataJpaAppointmentRepository repository;
    private final AppointmentMapper mapper;

    public JpaAppointmentRepositoryAdapter(SpringDataJpaAppointmentRepository repository, AppointmentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentEntity entity = mapper.toEntity(appointment);
        AppointmentEntity savedEntity = repository.save(entity);
        return mapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Appointment> findById(UUID appointmentId) {
        // Mapeamos la Entidad encontrada al Modelo de Dominio
        return repository.findById(appointmentId)
                .map(mapper::toDomainModel);
    }
}