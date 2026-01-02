package com.example.BackendProject.controllers;


import com.example.BackendProject.dto.RapportsDto;
import com.example.BackendProject.entities.Rapports;
import com.example.BackendProject.services.interfaces.RapportServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin(origins = "*")

public class RepportsController {
    private final RapportServiceInterface rapportService;

    public RepportsController(RapportServiceInterface rapportService) {
        this.rapportService = rapportService;
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<RapportsDto> createRapport(@RequestBody RapportsDto rapportsDto) {
        RapportsDto createdRapport = rapportService.createRapport(rapportsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRapport);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<RapportsDto> getRapportById(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.getRapportById(id));
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<List<RapportsDto>> getAllRapports() {
        return ResponseEntity.ok(rapportService.getAllRapports());
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<RapportsDto> updateRapport(
            @PathVariable Long id,
            @RequestBody RapportsDto rapportsDto
    ) {
        return ResponseEntity.ok(rapportService.updateRapport(id, rapportsDto));
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id) {
        rapportService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }
}
