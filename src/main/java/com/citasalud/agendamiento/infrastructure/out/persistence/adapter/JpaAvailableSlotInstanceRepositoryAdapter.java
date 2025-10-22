package com.citasalud.agendamiento.infrastructure.out.persistence.adapter;

import com.citasalud.agendamiento.domain.model.AvailableSlotInstance;
import com.citasalud.agendamiento.domain.ports.out.AvailableSlotInstanceRepositoryPort;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AvailabilitySlotEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AvailableSlotInstanceEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.AvailableSlotInstanceMapper;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaAvailabilitySlotRepository;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaAvailableSlotInstanceRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaAvailableSlotInstanceRepositoryAdapter implements AvailableSlotInstanceRepositoryPort {

    private final SpringDataJpaAvailableSlotInstanceRepository instanceRepository;
    private final SpringDataJpaAvailabilitySlotRepository slotRepository; // <-- AÑADIDO
    private final AvailableSlotInstanceMapper mapper;

    public JpaAvailableSlotInstanceRepositoryAdapter(
            SpringDataJpaAvailableSlotInstanceRepository instanceRepository,
            SpringDataJpaAvailabilitySlotRepository slotRepository, // <-- AÑADIDO
            AvailableSlotInstanceMapper mapper) {
        this.instanceRepository = instanceRepository;
        this.slotRepository = slotRepository; // <-- AÑADIDO
        this.mapper = mapper;
    }

    @Override
    public Optional<AvailableSlotInstance> findById(UUID id) {

        return instanceRepository.findById(id).map(instanceEntity -> {

            AvailableSlotInstance model = mapper.toDomainModel(instanceEntity);

            Optional<AvailabilitySlotEntity> parentSlotOpt = slotRepository
                    .findById(instanceEntity.getAvailabilitySlotId());
            parentSlotOpt.ifPresent(parentSlot -> {
                model.setProfessionalId(parentSlot.getProfessionalId());
                model.setSiteId(parentSlot.getSiteId());
            });

            return model;
        });
    }

    @Override
    public AvailableSlotInstance save(AvailableSlotInstance slotInstance) {
        var entity = instanceRepository.findById(slotInstance.getId()).orElseThrow();
        entity.setStatus(slotInstance.getStatus());
        var savedEntity = instanceRepository.save(entity);
        return mapper.toDomainModel(savedEntity);
    }

    @Override
    public List<AvailableSlotInstance> findAvailableByCriteria(UUID professionalId, UUID siteId,
            OffsetDateTime startDate, OffsetDateTime endDate) {
        List<AvailableSlotInstanceEntity> entities = instanceRepository.findAvailableByCriteria(
                professionalId, siteId, startDate, endDate);

        return entities.stream()
                .map(entity -> {
                    AvailableSlotInstance model = mapper.toDomainModel(entity);
                    model.setProfessionalId(professionalId);
                    model.setSiteId(siteId);
                    return model;
                })
                .toList();
    }
}