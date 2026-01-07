package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.OptionItemDto;
import com.example.BackendProject.services.implementations.OptionItemServiceImplementation;
import com.example.BackendProject.utils.LoggingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@Tag(name = "Options des Plats", description = "Gestion des suppléments/options (ex: frites, sauce)")
public class OptionItemController {

    private static final Logger logger = LoggerFactory.getLogger(OptionItemController.class);
    private final OptionItemServiceImplementation optionService;

    public OptionItemController(OptionItemServiceImplementation optionService) {
        this.optionService = optionService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CUISINIER')")
    @Operation(summary = "Ajouter une nouvelle option (Manager/Cuisinier)")
    public ResponseEntity<OptionItemDto> create(@RequestBody OptionItemDto dto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de création d'une option", context);
        try {
            OptionItemDto savedOption = optionService.save(dto);
            logger.info("{} Option créée avec succès. ID: {}", context, savedOption.getId());
            return new ResponseEntity<>(savedOption, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création de l'option: {}", context, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "Lister toutes les options (Tout le monde)")
    public ResponseEntity<List<OptionItemDto>> getAll(HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de toutes les options", context);
        List<OptionItemDto> options = optionService.getAll();
        logger.info("{} {} options récupérées avec succès", context, options.size());
        return ResponseEntity.ok(options);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionItemDto> getById(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération de l'option avec l'ID: {}", context, id);
        try {
            OptionItemDto option = optionService.getById(id);
            logger.info("{} Option ID: {} récupérée avec succès", context, id);
            return ResponseEntity.ok(option);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération de l'option ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUISINIER')")
    public ResponseEntity<OptionItemDto> update(@PathVariable Long id, @RequestBody OptionItemDto dto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de mise à jour de l'option ID: {}", context, id);
        try {
            OptionItemDto updatedOption = optionService.update(id, dto);
            logger.info("{} Option ID: {} mise à jour avec succès", context, id);
            return ResponseEntity.ok(updatedOption);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour de l'option ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Supprimer une option (Manager uniquement)")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de l'option ID: {}", context, id);
        try {
            optionService.delete(id);
            logger.info("{} Option ID: {} supprimée avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression de l'option ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }
}
