package com.citasalud.agendamiento.unit.mapper;

import com.citasalud.agendamiento.domain.model.Appointment;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AppointmentEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.AppointmentMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentMapperTest {

    private final AppointmentMapper appointmentMapper = new AppointmentMapper();

    @Test
    void shouldMapAppointmentToEntityCorrectly() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID professionalId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();
        UUID affiliateId = UUID.randomUUID();
        UUID slotInstanceId = UUID.randomUUID();

        // Usamos OffsetDateTime con zona fija (UTC) para que el test sea reproducible
        OffsetDateTime startAt = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime endAt = startAt.plusHours(1);

        String status = "CONFIRMED";

        Appointment appointment = new Appointment(
                professionalId, siteId, affiliateId, startAt, endAt, status, slotInstanceId
        );
        appointment.setId(id);

        // Act
        AppointmentEntity entity = appointmentMapper.toEntity(appointment);

        // Assert
        assertNotNull(entity);
        assertEquals(appointment.getId(), entity.getId());
        assertEquals(appointment.getProfessionalId(), entity.getProfessionalId());
        assertEquals(appointment.getSiteId(), entity.getSiteId());
        assertEquals(appointment.getAffiliateId(), entity.getAffiliateId());
        assertEquals(appointment.getStartAt(), entity.getStartAt());
        assertEquals(appointment.getEndAt(), entity.getEndAt());
        assertEquals(appointment.getStatus(), entity.getStatus());
        assertEquals(appointment.getAvailableSlotInstanceId(), entity.getAvailableSlotInstanceId());
    }

    @Test
    void shouldMapEntityToDomainModelCorrectly() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID professionalId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();
        UUID affiliateId = UUID.randomUUID();
        UUID slotInstanceId = UUID.randomUUID();

        OffsetDateTime startAt = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime endAt = startAt.plusHours(1);
        String status = "PENDING";

        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(id);
        entity.setProfessionalId(professionalId);
        entity.setSiteId(siteId);
        entity.setAffiliateId(affiliateId);
        entity.setStartAt(startAt);
        entity.setEndAt(endAt);
        entity.setStatus(status);
        entity.setAvailableSlotInstanceId(slotInstanceId);

        // Act
        Appointment model = appointmentMapper.toDomainModel(entity);

        // Assert
        assertNotNull(model);
        assertEquals(entity.getId(), model.getId());
        assertEquals(entity.getProfessionalId(), model.getProfessionalId());
        assertEquals(entity.getSiteId(), model.getSiteId());
        assertEquals(entity.getAffiliateId(), model.getAffiliateId());
        assertEquals(entity.getStartAt(), model.getStartAt());
        assertEquals(entity.getEndAt(), model.getEndAt());
        assertEquals(entity.getStatus(), model.getStatus());
        assertEquals(entity.getAvailableSlotInstanceId(), model.getAvailableSlotInstanceId());
    }



}
