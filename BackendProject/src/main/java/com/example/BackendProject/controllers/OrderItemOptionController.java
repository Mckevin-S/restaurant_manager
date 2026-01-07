package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.OrderItemOptionDto;
import com.example.BackendProject.services.interfaces.OrderItemOptionServiceInterface;
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
@RequestMapping("/api/order-item-options")
@Tag(name = "Détails Options Commande", description = "Lien entre une ligne de commande et les options choisies")
public class OrderItemOptionController {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemOptionController.class);
    private final OrderItemOptionServiceInterface service;

    public OrderItemOptionController(OrderItemOptionServiceInterface service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'SERVEUR')")
    @Operation(summary = "Ajouter une option à une ligne de commande")
    public ResponseEntity<OrderItemOptionDto> create(@RequestBody OrderItemOptionDto dto, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative d'ajout d'une option à une ligne de commande", context);
        try {
            OrderItemOptionDto saved = service.save(dto);
            logger.info("{} Option ajoutée avec succès. ID: {}", context, saved.getId());
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("{} Erreur lors de l'ajout de l'option: {}", context, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/ligne-commande/{id}")
    @Operation(summary = "Lister les options pour une ligne de commande précise")
    public ResponseEntity<List<OrderItemOptionDto>> getByLigne(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Récupération des options pour la ligne de commande ID: {}", context, id);
        List<OrderItemOptionDto> options = service.getByLigneCommande(id);
        logger.info("{} {} options récupérées pour la ligne de commande ID: {}", context, options.size(), id);
        return ResponseEntity.ok(options);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'SERVEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String context = LoggingUtils.getLogContext(request);
        logger.info("{} Tentative de suppression de l'option ID: {}", context, id);
        try {
            service.delete(id);
            logger.info("{} Option ID: {} supprimée avec succès", context, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("{} Erreur lors de la suppression de l'option ID: {} - {}", context, id, e.getMessage(), e);
            throw e;
        }
    }
}
