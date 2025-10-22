package com.citasalud.agendamiento.infrastructure.in.web.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AvailableSlotResponse {

    private UUID slotId;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private UUID professionalId;
    private UUID siteId;

    // Constructor
    public AvailableSlotResponse(UUID slotId, OffsetDateTime startAt, OffsetDateTime endAt, UUID professionalId, UUID siteId) {
        this.slotId = slotId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.professionalId = professionalId;
        this.siteId = siteId;
    }

    // Getters
    public UUID getSlotId() { return slotId; }
    public OffsetDateTime getStartAt() { return startAt; }
    public OffsetDateTime getEndAt() { return endAt; }
    public UUID getProfessionalId() { return professionalId; }
    public UUID getSiteId() { return siteId; }
}