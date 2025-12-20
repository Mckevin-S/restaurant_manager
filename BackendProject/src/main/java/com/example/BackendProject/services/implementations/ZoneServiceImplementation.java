package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.ZoneDto;
import com.example.BackendProject.entities.Zone;
import com.example.BackendProject.mappers.ZoneMapper;
import com.example.BackendProject.repository.RestaurantRepository;
import com.example.BackendProject.repository.ZoneRepository;
import com.example.BackendProject.services.interfaces.ZoneServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneServiceImplementation implements ZoneServiceInterface {
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
        if (zoneDto.getNom()== null ||  !restaurantRepository.existsById(zoneDto.getRestaurant().getId())) {
            throw new RuntimeException("Données incorrectes");
        }else {
            return zoneMapper.toDto(zoneRepository.save(zoneMapper.toEntity(zoneDto)));

        }

    }

    @Override
    public List<ZoneDto> getAllZones() {
        return zoneRepository.findAll()
                .stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ZoneDto getZoneById(Long id) {
       try {
           Zone zone = zoneRepository.getZonesById(id);
           if (zone == null) {
               throw new RuntimeException("Zone non trouvée avec l'ID : " + id);
           }
           return zoneMapper.toDto(zone);
       } catch (Exception e) {
              throw new RuntimeException("Erreur lors de la récupération de la zone avec l'ID : " + id, e);
       }
    }

    @Override
    public ZoneDto updateZone(Long id, ZoneDto zoneDto) {
        Zone zone = zoneRepository.getZonesById(id);
        try{
            if (zone == null) {
                throw new RuntimeException("Zone non trouvée avec l'ID : " + id);
            }
            zone.setNom(zoneDto.getNom());
            zone.setRestaurant(zoneDto.getRestaurant());
            Zone updatedZone = zoneRepository.save(zone);
            return zoneMapper.toDto(updatedZone);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la zone avec l'ID : " + id, e);

        }

    }

    @Override
    public void deleteZone(Long id) {
        Zone zone = zoneRepository.getZonesById(id);
        if (zone == null) {
            throw new RuntimeException("Zone non trouvée avec l'ID : " + id);
        }
        zoneRepository.delete(zone);

    }

    @Override
    public List<ZoneDto> getZonesByRestaurantId(Long restaurantId) {
        return zoneRepository.findZonesByRestaurantId(restaurantId)
                .stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ZoneDto> searchZonesByName(String nom) {
<<<<<<< HEAD
        return zoneRepository.findByNom(nom)
=======
        return zoneRepository.findByName(nom)
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8
                .stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
    }
}
