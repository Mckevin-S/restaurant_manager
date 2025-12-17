package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.TableRestaurantDto;

import java.util.List;

public interface TableRestaurantServiceInterface {

    TableRestaurantDto addTable(TableRestaurantDto tableDto);

    List<TableRestaurantDto> getAllTables();

    TableRestaurantDto getTableById(Long id);

    TableRestaurantDto updateTable(Long id, TableRestaurantDto tableDto);

    void deleteTable(Long id);

    List<TableRestaurantDto> getTablesByZoneId(Long zoneId);

    List<TableRestaurantDto> searchTablesByNumero(String numero);
}
