package com.citasalud.agendamiento.domain.ports.in;

import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface FindAvailableSlotsUseCase {
    List<AvailableSlotInstance> findAvailableSlots(
        UUID professionalId, 
        UUID siteId, 
        OffsetDateTime startDate, 
        OffsetDateTime endDate
    );
}