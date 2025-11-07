package com.citasalud.agendamiento.unit.adapter;

import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.infrastructure.out.persistence.adapter.JpaAvailableSlotInstanceRepositoryAdapter;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AvailabilitySlotEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AvailableSlotInstanceEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.AvailableSlotInstanceMapper;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaAvailabilitySlotRepository;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaAvailableSlotInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaAvailableSlotInstanceRepositoryAdapterTest {


    @Mock
    private SpringDataJpaAvailableSlotInstanceRepository instanceRepository;

    @Mock
    private SpringDataJpaAvailabilitySlotRepository slotRepository;

    @Mock
    private AvailableSlotInstanceMapper mapper;

    @InjectMocks
    private JpaAvailableSlotInstanceRepositoryAdapter adapter;

    private UUID slotId;
    private UUID professionalId;
    private UUID siteId;
    private AvailableSlotInstanceEntity entity;
    private AvailableSlotInstance domainModel;

    @BeforeEach
    void setUp() {
        slotId = UUID.randomUUID();
        professionalId = UUID.randomUUID();
        siteId = UUID.randomUUID();

        entity = new AvailableSlotInstanceEntity();
        entity.setId(UUID.randomUUID());
        entity.setAvailabilitySlotId(slotId);
        entity.setStatus("AVAILABLE");

        domainModel = new AvailableSlotInstance();
        domainModel.setId(entity.getId());
        domainModel.setStatus("AVAILABLE");
    }

    @Test
    void findById_shouldReturnModelWithParentData() {
        AvailabilitySlotEntity parentSlot = new AvailabilitySlotEntity();
        parentSlot.setProfessionalId(professionalId);
        parentSlot.setSiteId(siteId);

        when(instanceRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(mapper.toDomainModel(entity)).thenReturn(domainModel);
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(parentSlot));

        Optional<AvailableSlotInstance> result = adapter.findById(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(professionalId, result.get().getProfessionalId());
        assertEquals(siteId, result.get().getSiteId());
        verify(instanceRepository).findById(entity.getId());
        verify(slotRepository).findById(slotId);
        verify(mapper).toDomainModel(entity);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(instanceRepository.findById(entity.getId())).thenReturn(Optional.empty());

        Optional<AvailableSlotInstance> result = adapter.findById(entity.getId());

        assertTrue(result.isEmpty());
        verify(instanceRepository).findById(entity.getId());
        verifyNoInteractions(mapper);
    }

    @Test
    void save_shouldUpdateStatusAndReturnMappedModel() {
        when(instanceRepository.findById(domainModel.getId())).thenReturn(Optional.of(entity));
        when(instanceRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomainModel(entity)).thenReturn(domainModel);

        AvailableSlotInstance result = adapter.save(domainModel);

        assertNotNull(result);
        assertEquals(domainModel.getId(), result.getId());
        verify(instanceRepository).findById(domainModel.getId());
        verify(instanceRepository).save(entity);
        verify(mapper).toDomainModel(entity);
    }

    @Test
    void findAvailableByCriteria_shouldReturnMappedList() {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime end = start.plusDays(1);
        List<AvailableSlotInstanceEntity> entities = List.of(entity);

        when(instanceRepository.findAvailableByCriteria(professionalId, siteId, start, end)).thenReturn(entities);
        when(mapper.toDomainModel(entity)).thenReturn(domainModel);

        List<AvailableSlotInstance> result = adapter.findAvailableByCriteria(professionalId, siteId, start, end);

        assertEquals(1, result.size());
        assertEquals(professionalId, result.get(0).getProfessionalId());
        assertEquals(siteId, result.get(0).getSiteId());
        verify(instanceRepository).findAvailableByCriteria(professionalId, siteId, start, end);
        verify(mapper).toDomainModel(entity);
    }


}
