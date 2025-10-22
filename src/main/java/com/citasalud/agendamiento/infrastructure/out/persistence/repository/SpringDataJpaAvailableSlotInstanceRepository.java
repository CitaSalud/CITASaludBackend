package com.citasalud.agendamiento.infrastructure.out.persistence.repository;

import com.citasalud.agendamiento.infrastructure.out.persistence.entity.AvailableSlotInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataJpaAvailableSlotInstanceRepository extends JpaRepository<AvailableSlotInstanceEntity, UUID> {


    @Query("SELECT asi FROM AvailableSlotInstanceEntity asi " +
        "JOIN AvailabilitySlotEntity asl ON asi.availabilitySlotId = asl.id " +
        "WHERE asi.status = 'available' " +
        "AND asl.professionalId = :professionalId " +
        "AND asl.siteId = :siteId " +
        "AND asi.startAt >= :startDate " +
        "AND asi.endAt <= :endDate")
    List<AvailableSlotInstanceEntity> findAvailableByCriteria(
        @Param("professionalId") UUID professionalId,
        @Param("siteId") UUID siteId,
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );
}