package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.ReservationDto;
import com.example.BackendProject.services.interfaces.ReservationService;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Gestion des Réservations", description = "API pour la gestion des réservations du restaurant")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(summary = "Créer une réservation")
    public ResponseEntity<ReservationDto> create(@RequestBody ReservationDto dto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Requête de création de réservation", context);
        return new ResponseEntity<>(reservationService.createReservation(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les réservations")
    public ResponseEntity<List<ReservationDto>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        if (date != null) {
            logger.info("{} Récupération des réservations pour la date: {}", context, date);
            return ResponseEntity.ok(reservationService.getReservationsByDate(date));
        }
        logger.info("{} Récupération de toutes les réservations", context);
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une réservation par ID")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une réservation")
    public ResponseEntity<ReservationDto> update(@PathVariable Long id, @RequestBody ReservationDto dto) {
        return ResponseEntity.ok(reservationService.updateReservation(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une réservation")
    public ResponseEntity<ReservationDto> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(reservationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des réservations")
    public ResponseEntity<List<ReservationDto>> search(@RequestParam String term) {
        return ResponseEntity.ok(reservationService.searchReservations(term));
    }
}
