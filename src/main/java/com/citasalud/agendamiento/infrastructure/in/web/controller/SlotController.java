package com.citasalud.agendamiento.infrastructure.in.web.controller;

import com.citasalud.agendamiento.domain.ports.in.FindAvailableSlotsUseCase;
import com.citasalud.agendamiento.infrastructure.in.web.dto.AvailableSlotResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/slots")
public class SlotController {

    private final FindAvailableSlotsUseCase findAvailableSlotsUseCase;

    public SlotController(FindAvailableSlotsUseCase findAvailableSlotsUseCase) {
        this.findAvailableSlotsUseCase = findAvailableSlotsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AvailableSlotResponse>> findAvailableSlots(
            @RequestParam UUID professionalId,
            @RequestParam UUID siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        List<AvailableSlotResponse> slots = findAvailableSlotsUseCase.findAvailableSlots(
            professionalId, siteId, startDate, endDate)
            .stream()
            .map(model -> new AvailableSlotResponse(
                    model.getId(),
                    model.getStartAt(),
                    model.getEndAt(),
                    model.getProfessionalId(),
                    model.getSiteId()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(slots);
    }
}