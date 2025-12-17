package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.ZoneDto;

import java.util.List;

public interface ZoneServiceInterface {
    ZoneDto addZone(ZoneDto zoneDto);
    List<ZoneDto> getAllZones();
    ZoneDto getZoneById(Long id);
    ZoneDto updateZone(Long id, ZoneDto zoneDto);
    void deleteZone(Long id);
    List<ZoneDto> getZonesByRestaurantId(Long restaurantId);
    List<ZoneDto> searchZonesByName(String nom);
}
