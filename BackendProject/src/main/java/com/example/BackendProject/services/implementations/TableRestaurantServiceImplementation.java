// java
package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.TableRestaurantDto;
import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.entities.Zone;
import com.example.BackendProject.mappers.TableRestaurantMapper;
import com.example.BackendProject.repository.TableRestaurantRepository;
import com.example.BackendProject.repository.ZoneRepository;
import com.example.BackendProject.services.interfaces.TableRestaurantServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableRestaurantServiceImplementation implements TableRestaurantServiceInterface {

    private final TableRestaurantRepository tableRestaurantRepository;
    private final ZoneRepository zoneRepository;
    private final TableRestaurantMapper tableRestaurantMapper;

    public TableRestaurantServiceImplementation(TableRestaurantRepository tableRestaurantRepository,
                                                ZoneRepository zoneRepository,
                                                TableRestaurantMapper tableRestaurantMapper) {
        this.tableRestaurantRepository = tableRestaurantRepository;
        this.zoneRepository = zoneRepository;
        this.tableRestaurantMapper = tableRestaurantMapper;
    }

    @Override
    public TableRestaurantDto addTable(TableRestaurantDto tableDto) {
        if (tableDto == null
                || tableDto.getNumero() == null || tableDto.getNumero().isBlank()
                || tableDto.getZone() == null || tableDto.getZone().getId() == null || !zoneRepository.existsById(tableDto.getZone().getId())) {
            throw new RuntimeException("Données incorrectes pour la création de la table");
        }else {
            try{
                return tableRestaurantMapper.toDto(tableRestaurantRepository.save(tableRestaurantMapper.toEntity(tableDto)));
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la création de la table : " + e.getMessage());
            }
        }

    }

    @Override
    public List<TableRestaurantDto> getAllTables() {
        return tableRestaurantRepository.findAll()
                .stream()
                .map(tableRestaurantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TableRestaurantDto getTableById(Long id) {
        return tableRestaurantRepository.findById(id)
                .map(tableRestaurantMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Table introuvable avec l'ID : " + id));
    }

    @Override
    public TableRestaurantDto updateTable(Long id, TableRestaurantDto tableDto) {
        if (tableDto == null) {
            throw new RuntimeException("Données incorrectes pour la mise à jour");
        }

        TableRestaurant existing = tableRestaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table introuvable avec l'ID : " + id));

        if (tableDto.getNumero() != null) existing.setNumero(tableDto.getNumero());
        if (tableDto.getCapacite() != null) existing.setCapacite(tableDto.getCapacite());
       // if (tableDto.getS() != null) existing.setStatut(tableDto.getStatut());

        if (tableDto.getZone() != null && tableDto.getZone().getId() != null) {
            Zone zone = zoneRepository.findById(tableDto.getZone().getId())
                    .orElseThrow(() -> new RuntimeException("Zone introuvable avec l'ID : " + tableDto.getZone().getId()));
            existing.setZone(zone);
        }

        TableRestaurant updated = tableRestaurantRepository.save(existing);
        return tableRestaurantMapper.toDto(updated);
    }

    @Override
    public void deleteTable(Long id) {
        if (!tableRestaurantRepository.existsById(id)) {
            throw new RuntimeException("Table introuvable avec l'ID : " + id);
        }
        tableRestaurantRepository.deleteById(id);
    }

    @Override
    public List<TableRestaurantDto> getTablesByZoneId(Long zoneId) {
        if (zoneId == null) return List.of();
        return tableRestaurantRepository.findAll()
                .stream()
                .filter(t -> t.getZone() != null && zoneId.equals(t.getZone().getId()))
                .map(tableRestaurantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TableRestaurantDto> searchTablesByNumero(String numero) {
        if (numero == null || numero.isBlank()) return List.of();
        String lower = numero.toLowerCase();
        return tableRestaurantRepository.findAll()
                .stream()
                .filter(t -> t.getNumero() != null && t.getNumero().toLowerCase().contains(lower))
                .map(tableRestaurantMapper::toDto)
                .collect(Collectors.toList());
    }
}
