package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.RapportsDto;
import com.example.BackendProject.services.interfaces.RapportServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")

public class RapportsController {
    
    private static final Logger logger = LoggerFactory.getLogger(RapportsController.class);
    private final RapportServiceInterface rapportService;

    public RapportsController(RapportServiceInterface rapportService) {
        this.rapportService = rapportService;
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<RapportsDto> createRapport(@RequestBody RapportsDto rapportsDto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'un rapport", context);
        try {
            RapportsDto createdRapport = rapportService.createRapport(rapportsDto);
            logger.info("{} Rapport créé avec succès. ID: {}", context, createdRapport.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRapport);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création du rapport: {}", context, e.getMessage(), e);
            throw e;
        }
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<RapportsDto> getRapportById(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération du rapport avec l'ID: {}", context, id);
        try {
            RapportsDto rapport = rapportService.getRapportById(id);
            logger.info("{} Rapport ID: {} récupéré avec succès", context, id);
            return ResponseEntity.ok(rapport);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération du rapport ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<List<RapportsDto>> getAllRapports(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de tous les rapports", context);
        List<RapportsDto> rapports = rapportService.getAllRapports();
        logger.info("{} {} rapports récupérés avec succès", context, rapports.size());
        return ResponseEntity.ok(rapports);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<RapportsDto> updateRapport(
            @PathVariable Long id,
            @RequestBody RapportsDto rapportsDto,
            HttpServletRequest request
    ) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour du rapport ID: {}", context, id);
        try {
            RapportsDto updatedRapport = rapportService.updateRapport(id, rapportsDto);
            logger.info("{} Rapport ID: {} mis à jour avec succès", context, id);
            return ResponseEntity.ok(updatedRapport);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour du rapport ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression du rapport ID: {}", context, id);
        try {
            rapportService.deleteRapport(id);
            logger.info("{} Rapport ID: {} supprimé avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression du rapport ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }
}
