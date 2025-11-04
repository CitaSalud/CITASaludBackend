package com.citasalud.agendamiento.unit.service;

import com.citasalud.agendamiento.application.service.SlotService;
import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.domain.ports.out.AvailableSlotInstanceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private AvailableSlotInstanceRepositoryPort availableSlotRepositoryPort;

    @InjectMocks
    private SlotService slotService;

    private UUID professionalId;
    private UUID siteId;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private List<AvailableSlotInstance> slots;

    @BeforeEach
    void setUp() {
        professionalId = UUID.randomUUID();
        siteId = UUID.randomUUID();
        startDate = OffsetDateTime.now();
        endDate = startDate.plusDays(7);

        AvailableSlotInstance slot1 = new AvailableSlotInstance();
        slot1.setId(UUID.randomUUID());
        slot1.setProfessionalId(professionalId);
        slot1.setSiteId(siteId);
        slot1.setStartAt(startDate.plusHours(1));
        slot1.setEndAt(startDate.plusHours(2));
        slot1.setStatus("available");

        AvailableSlotInstance slot2 = new AvailableSlotInstance();
        slot2.setId(UUID.randomUUID());
        slot2.setProfessionalId(professionalId);
        slot2.setSiteId(siteId);
        slot2.setStartAt(startDate.plusDays(1).plusHours(1));
        slot2.setEndAt(startDate.plusDays(1).plusHours(2));
        slot2.setStatus("available");

        slots = Arrays.asList(slot1, slot2);
    }

    @Test
    void findAvailableSlots_Success() {
        when(availableSlotRepositoryPort.findAvailableByCriteria(professionalId, siteId, startDate, endDate))
                .thenReturn(slots);

        List<AvailableSlotInstance> result = slotService.findAvailableSlots(professionalId, siteId, startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(slots, result);

        verify(availableSlotRepositoryPort).findAvailableByCriteria(professionalId, siteId, startDate, endDate);
    }

    @Test
    void findAvailableSlots_EmptyList() {
        when(availableSlotRepositoryPort.findAvailableByCriteria(professionalId, siteId, startDate, endDate))
                .thenReturn(Arrays.asList());

        List<AvailableSlotInstance> result = slotService.findAvailableSlots(professionalId, siteId, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(availableSlotRepositoryPort).findAvailableByCriteria(professionalId, siteId, startDate, endDate);
    }
}