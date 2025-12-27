package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.OptionItemDto;
import com.example.BackendProject.services.implementations.OptionItemServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@Tag(name = "Options des Plats", description = "Gestion des suppléments/options (ex: frites, sauce)")
public class OptionItemController {

    private final OptionItemServiceImplementation optionService;

    public OptionItemController(OptionItemServiceImplementation optionService) {
        this.optionService = optionService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CUISINIER')")
    @Operation(summary = "Ajouter une nouvelle option (Manager/Cuisinier)")
    public ResponseEntity<OptionItemDto> create(@RequestBody OptionItemDto dto) {
        return new ResponseEntity<>(optionService.save(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les options (Tout le monde)")
    public ResponseEntity<List<OptionItemDto>> getAll() {
        return ResponseEntity.ok(optionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(optionService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUISINIER')")
    public ResponseEntity<OptionItemDto> update(@PathVariable Long id, @RequestBody OptionItemDto dto) {
        return ResponseEntity.ok(optionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Supprimer une option (Manager uniquement)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        optionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
