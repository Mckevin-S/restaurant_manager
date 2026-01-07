// java
package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.TableRestaurantDto;
import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.entities.Zone;
import com.example.BackendProject.mappers.TableRestaurantMapper;
import com.example.BackendProject.repository.TableRestaurantRepository;
import com.example.BackendProject.repository.ZoneRepository;
import com.example.BackendProject.services.interfaces.TableRestaurantServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableRestaurantServiceImplementation implements TableRestaurantServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(TableRestaurantServiceImplementation.class);
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
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de création d'une table - Numéro: {}", context, tableDto != null ? tableDto.getNumero() : "N/A");
        if (tableDto == null
                || tableDto.getNumero() == null || tableDto.getNumero().isBlank()
                || tableDto.getZone() == null || tableDto.getZone().getId() == null || !zoneRepository.existsById(tableDto.getZone().getId())) {
            logger.error("{} Erreur de validation: données incorrectes pour la création de la table", context);
            throw new RuntimeException("Données incorrectes pour la création de la table");
        }
        try {
            TableRestaurant saved = tableRestaurantRepository.save(tableRestaurantMapper.toEntity(tableDto));
            logger.info("{} Table créée avec succès. ID: {}, Numéro: {}", context, saved.getId(), saved.getNumero());
            return tableRestaurantMapper.toDto(saved);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la création de la table: {}", context, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création de la table : " + e.getMessage());
        }
    }

    @Override
    public List<TableRestaurantDto> getAllTables() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les tables", context);
        List<TableRestaurantDto> tables = tableRestaurantRepository.findAll()
                .stream()
                .map(tableRestaurantMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} tables récupérées avec succès", context, tables.size());
        return tables;
    }

    @Override
    public TableRestaurantDto getTableById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la table avec l'ID: {}", context, id);
        return tableRestaurantRepository.findById(id)
                .map(tableRestaurantMapper::toDto)
                .orElseThrow(() -> {
                    logger.error("{} Table non trouvée avec l'ID: {}", context, id);
                    return new RuntimeException("Table introuvable avec l'ID : " + id);
                });
    }

    @Override
    public TableRestaurantDto updateTable(Long id, TableRestaurantDto tableDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour de la table ID: {}", context, id);
        if (tableDto == null) {
            logger.error("{} Données incorrectes pour la mise à jour de la table ID: {}", context, id);
            throw new RuntimeException("Données incorrectes pour la mise à jour");
        }

        TableRestaurant existing = tableRestaurantRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Table non trouvée avec l'ID: {}", context, id);
                    return new RuntimeException("Table introuvable avec l'ID : " + id);
                });

        if (tableDto.getNumero() != null) existing.setNumero(tableDto.getNumero());
        if (tableDto.getCapacite() != null) existing.setCapacite(tableDto.getCapacite());
       // if (tableDto.getS() != null) existing.setStatut(tableDto.getStatut());

        if (tableDto.getZone() != null && tableDto.getZone().getId() != null) {
            Zone zone = zoneRepository.findById(tableDto.getZone().getId())
                    .orElseThrow(() -> {
                        logger.error("{} Zone non trouvée avec l'ID: {}", context, tableDto.getZone().getId());
                        return new RuntimeException("Zone introuvable avec l'ID : " + tableDto.getZone().getId());
                    });
            existing.setZone(zone);
        }

        TableRestaurant updated = tableRestaurantRepository.save(existing);
        logger.info("{} Table ID: {} mise à jour avec succès - Numéro: {}", context, id, updated.getNumero());
        return tableRestaurantMapper.toDto(updated);
    }

    @Override
    public void deleteTable(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de la table ID: {}", context, id);
        if (!tableRestaurantRepository.existsById(id)) {
            logger.error("{} Table non trouvée avec l'ID: {}", context, id);
            throw new RuntimeException("Table introuvable avec l'ID : " + id);
        }
        tableRestaurantRepository.deleteById(id);
        logger.info("{} Table ID: {} supprimée avec succès", context, id);
    }

    @Override
    public List<TableRestaurantDto> getTablesByZoneId(Long zoneId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des tables pour la zone ID: {}", context, zoneId);
        if (zoneId == null) {
            logger.warn("{} Zone ID est null", context);
            return List.of();
        }
        List<TableRestaurantDto> tables = tableRestaurantRepository.findAll()
                .stream()
                .filter(t -> t.getZone() != null && zoneId.equals(t.getZone().getId()))
                .map(tableRestaurantMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} tables récupérées pour la zone ID: {}", context, tables.size(), zoneId);
        return tables;
    }

    @Override
    public List<TableRestaurantDto> searchTablesByNumero(String numero) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche de tables par numéro: {}", context, numero);
        if (numero == null || numero.isBlank()) {
            logger.warn("{} Numéro de recherche vide ou null", context);
            return List.of();
        }
        String lower = numero.toLowerCase();
        List<TableRestaurantDto> tables = tableRestaurantRepository.findAll()
                .stream()
                .filter(t -> t.getNumero() != null && t.getNumero().toLowerCase().contains(lower))
                .map(tableRestaurantMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} tables trouvées pour le numéro: {}", context, tables.size(), numero);
        return tables;
    }
}
