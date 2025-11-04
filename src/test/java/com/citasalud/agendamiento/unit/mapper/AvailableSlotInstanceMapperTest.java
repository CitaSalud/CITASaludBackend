package com.citasalud.agendamiento.unit.mapper;

import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AvailableSlotInstanceEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.AvailableSlotInstanceMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AvailableSlotInstanceMapperTest {

    private final AvailableSlotInstanceMapper mapper = new AvailableSlotInstanceMapper();

    @Test
    void shouldMapDomainModelToEntityCorrectly() {
        // Arrange
        UUID id = UUID.randomUUID();
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusHours(1);
        String status = "AVAILABLE";

        AvailableSlotInstance model = new AvailableSlotInstance();
        model.setId(id);
        model.setStartAt(startAt);
        model.setEndAt(endAt);
        model.setStatus(status);

        // Act
        AvailableSlotInstanceEntity entity = mapper.toEntity(model);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(startAt, entity.getStartAt());
        assertEquals(endAt, entity.getEndAt());
        assertEquals(status, entity.getStatus());
    }

    @Test
    void shouldMapEntityToDomainModelCorrectly() {
        // Arrange
        UUID id = UUID.randomUUID();
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusHours(2);
        String status = "BOOKED";

        AvailableSlotInstanceEntity entity = new AvailableSlotInstanceEntity();
        entity.setId(id);
        entity.setStartAt(startAt);
        entity.setEndAt(endAt);
        entity.setStatus(status);

        // Act
        AvailableSlotInstance model = mapper.toDomainModel(entity);

        // Assert
        assertNotNull(model);
        assertEquals(id, model.getId());
        assertEquals(startAt, model.getStartAt());
        assertEquals(endAt, model.getEndAt());
        assertEquals(status, model.getStatus());
    }
}
