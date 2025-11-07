package com.citasalud.agendamiento.unit.adapter;

import com.citasalud.agendamiento.domain.model.Appointment;
import com.citasalud.agendamiento.infrastructure.out.persistence.adapter.JpaAppointmentRepositoryAdapter;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AppointmentEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.AppointmentMapper;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class JpaAppointmentRepositoryAdapterTest {


    @Mock
    private SpringDataJpaAppointmentRepository repository;

    @Mock
    private AppointmentMapper mapper;

    @InjectMocks
    private JpaAppointmentRepositoryAdapter adapter;

    private Appointment appointment;
    private AppointmentEntity entity;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        appointment = new Appointment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), "scheduled", UUID.randomUUID());
        appointment.setId(id);

        entity = new AppointmentEntity();
        entity.setId(id);
        entity.setAffiliateId(appointment.getAffiliateId());
        entity.setProfessionalId(appointment.getProfessionalId());
        entity.setSiteId(appointment.getSiteId());
        entity.setStartAt(appointment.getStartAt());
        entity.setEndAt(appointment.getEndAt());
        entity.setStatus(appointment.getStatus());
        //entity.setSlotId(appointment.getSlotId());
    }

    @Test
    void save_ShouldReturnMappedDomainObject() {
        when(mapper.toEntity(any(Appointment.class))).thenReturn(entity);
        when(repository.save(any(AppointmentEntity.class))).thenReturn(entity);
        when(mapper.toDomainModel(any(AppointmentEntity.class))).thenReturn(appointment);

        Appointment result = adapter.save(appointment);

        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
        verify(mapper).toEntity(appointment);
        verify(repository).save(entity);
        verify(mapper).toDomainModel(entity);
    }

    @Test
    void findById_ShouldReturnAppointmentWhenExists() {
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomainModel(entity)).thenReturn(appointment);

        Optional<Appointment> result = adapter.findById(id);

        assertTrue(result.isPresent());
        assertEquals(appointment.getId(), result.get().getId());
        verify(repository).findById(id);
        verify(mapper).toDomainModel(entity);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Appointment> result = adapter.findById(id);

        assertTrue(result.isEmpty());
        verify(repository).findById(id);
        verify(mapper, never()).toDomainModel(any());
    }


}
