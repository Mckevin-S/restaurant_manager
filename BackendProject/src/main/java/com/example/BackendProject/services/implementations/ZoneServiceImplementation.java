package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.ZoneDto;
import com.example.BackendProject.entities.Zone;
import com.example.BackendProject.mappers.ZoneMapper;
import com.example.BackendProject.repository.RestaurantRepository;
import com.example.BackendProject.repository.ZoneRepository;
import com.example.BackendProject.services.interfaces.ZoneServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneServiceImplementation implements ZoneServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(ZoneServiceImplementation.class);
    private ZoneRepository zoneRepository;
    private RestaurantRepository restaurantRepository;
    private ZoneMapper zoneMapper;

    public ZoneServiceImplementation(ZoneRepository zoneRepository, RestaurantRepository restaurantRepository, ZoneMapper zoneMapper) {
        this.zoneRepository = zoneRepository;
        this.restaurantRepository = restaurantRepository;
        this.zoneMapper = zoneMapper;
    }

    @Override
    public ZoneDto addZone(ZoneDto zoneDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de création d'une zone - Nom: {}", context, zoneDto != null ? zoneDto.getNom() : "N/A");
        if (zoneDto.getNom()== null ||  !restaurantRepository.existsById(zoneDto.getRestaurantId())) {
            logger.error("{} Erreur de validation: données incorrectes pour la création de la zone", context);
            throw new RuntimeException("Données incorrectes");
        }
        Zone saved = zoneRepository.save(zoneMapper.toEntity(zoneDto));
        logger.info("{} Zone créée avec succès. ID: {}, Nom: {}", context, saved.getId(), saved.getNom());
        return zoneMapper.toDto(saved);
    }

    @Override
    public List<ZoneDto> getAllZones() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les zones", context);
        List<ZoneDto> zones = zoneRepository.findAll()
                .stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} zones récupérées avec succès", context, zones.size());
        return zones;
    }

    @Override
    public ZoneDto getZoneById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la zone avec l'ID: {}", context, id);
        try {
            Zone zone = zoneRepository.getZonesById(id);
            if (zone == null) {
                logger.error("{} Zone non trouvée avec l'ID: {}", context, id);
                throw new RuntimeException("Zone non trouvée avec l'ID : " + id);
            }
            logger.info("{} Zone ID: {} récupérée avec succès - Nom: {}", context, id, zone.getNom());
            return zoneMapper.toDto(zone);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la récupération de la zone ID: {} - {}", context, id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération de la zone avec l'ID : " + id, e);
        }
    }

    @Override
    public ZoneDto updateZone(Long id, ZoneDto zoneDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour de la zone ID: {}", context, id);
        Zone zone = zoneRepository.getZonesById(id);
        try {
            if (zone == null) {
                logger.error("{} Zone non trouvée avec l'ID: {}", context, id);
                throw new RuntimeException("Zone non trouvée avec l'ID : " + id);
            }
            zone.setNom(zoneDto.getNom());
            Zone updatedZone = zoneRepository.save(zone);
            logger.info("{} Zone ID: {} mise à jour avec succès - Nom: {}", context, id, updatedZone.getNom());
            return zoneMapper.toDto(updatedZone);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour de la zone ID: {} - {}", context, id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de la zone avec l'ID : " + id, e);
        }
    }

    @Override
    public void deleteZone(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de la zone ID: {}", context, id);
        Zone zone = zoneRepository.getZonesById(id);
        if (zone == null) {
            logger.error("{} Zone non trouvée avec l'ID: {}", context, id);
            throw new RuntimeException("Zone non trouvée avec l'ID : " + id);
        }
        zoneRepository.delete(zone);
        logger.info("{} Zone ID: {} supprimée avec succès", context, id);
    }

    @Override
    public List<ZoneDto> getZonesByRestaurantId(Long restaurantId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des zones pour le restaurant ID: {}", context, restaurantId);
        List<ZoneDto> zones = zoneRepository.findZonesByRestaurantId(restaurantId)
                .stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} zones récupérées pour le restaurant ID: {}", context, zones.size(), restaurantId);
        return zones;
    }

    @Override
    public List<ZoneDto> searchZonesByName(String nom) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche de zones par nom: {}", context, nom);
        List<ZoneDto> zones = zoneRepository.findByNom(nom)
                .stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} zones trouvées pour le nom: {}", context, zones.size(), nom);
        return zones;
    }
}
