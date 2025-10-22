package com.citasalud.agendamiento.application.service;

import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.domain.ports.in.FindAvailableSlotsUseCase;
import com.citasalud.agendamiento.domain.ports.out.AvailableSlotInstanceRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SlotService implements FindAvailableSlotsUseCase {

    private final AvailableSlotInstanceRepositoryPort availableSlotRepositoryPort;

    public SlotService(AvailableSlotInstanceRepositoryPort availableSlotRepositoryPort) {
        this.availableSlotRepositoryPort = availableSlotRepositoryPort;
    }

    @Override
    public List<AvailableSlotInstance> findAvailableSlots(UUID professionalId, UUID siteId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return availableSlotRepositoryPort.findAvailableByCriteria(professionalId, siteId, startDate, endDate);
    }
}