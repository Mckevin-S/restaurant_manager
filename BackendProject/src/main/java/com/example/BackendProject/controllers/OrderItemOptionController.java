package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.OrderItemOptionDto;
import com.example.BackendProject.services.interfaces.OrderItemOptionServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-item-options")
@Tag(name = "Détails Options Commande", description = "Lien entre une ligne de commande et les options choisies")
public class OrderItemOptionController {

    private final OrderItemOptionServiceInterface service;

    public OrderItemOptionController(OrderItemOptionServiceInterface service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'SERVEUR')")
    @Operation(summary = "Ajouter une option à une ligne de commande")
    public ResponseEntity<OrderItemOptionDto> create(@RequestBody OrderItemOptionDto dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @GetMapping("/ligne-commande/{id}")
    @Operation(summary = "Lister les options pour une ligne de commande précise")
    public ResponseEntity<List<OrderItemOptionDto>> getByLigne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByLigneCommande(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'SERVEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
