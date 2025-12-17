package com.example.BackendProject.controllers;

import com.example.BackendProject.dto.ZoneDto;
import com.example.BackendProject.services.implementations.ZoneServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/zones"})
public class ZoneController {
    private ZoneServiceImplementation zoneService;

    public ZoneController(ZoneServiceImplementation zoneService) {
        this.zoneService = zoneService;
    }
    @PostMapping
    public ResponseEntity<?> createZone(ZoneDto zoneDto) {
        try {
            ZoneDto createdZone = zoneService.addZone(zoneDto);
            return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating zone: " + e.getMessage());
        }

    }
    @PutMapping
    public ResponseEntity<?> updateZone(Long id, ZoneDto zoneDto) {
        try {
            ZoneDto updatedZone = zoneService.updateZone(id, zoneDto);
            return new ResponseEntity<>(updatedZone, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating zone: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllZones() {
        try {
            return new ResponseEntity<>(zoneService.getAllZones(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving zones: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getZoneById(@PathVariable Long id) {
        try {
            ZoneDto zone = zoneService.getZoneById(id);
            return new ResponseEntity<>(zone, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving zone: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteZone(@PathVariable Long id) {
        try {
            zoneService.deleteZone(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting zone: " + e.getMessage());
        }
    }



}
