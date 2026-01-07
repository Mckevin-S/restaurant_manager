
package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.mappers.RestaurantMapper;
import com.example.BackendProject.repository.RestaurantRepository;
import com.example.BackendProject.services.interfaces.RestaurantServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImplementation implements RestaurantServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImplementation.class);
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImplementation(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public RestaurantDto createRestaurant(RestaurantDto restaurantDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de création d'un restaurant - Nom: {}", context, restaurantDto != null ? restaurantDto.getNom() : "N/A");
        if (restaurantDto == null || restaurantDto.getNom() == null || restaurantDto.getNom().isBlank()) {
            logger.error("{} Erreur de validation: données incorrectes pour la création du restaurant", context);
            throw new RuntimeException("Données incorrectes pour la création du restaurant");
        }
        Restaurant saved = restaurantRepository.save(restaurantMapper.toEntity(restaurantDto));
        logger.info("{} Restaurant créé avec succès. ID: {}, Nom: {}", context, saved.getId(), saved.getNom());
        return restaurantMapper.toDto(saved);
    }

    @Override
    public RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour du restaurant ID: {}", context, id);
        if (restaurantDto == null) {
            logger.error("{} Données incorrectes pour la mise à jour du restaurant ID: {}", context, id);
            throw new RuntimeException("Données incorrectes pour la mise à jour");
        }
        
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Restaurant non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Restaurant introuvable avec l'ID : " + id);
                });
        
        try {
            existingRestaurant.setNom(restaurantDto.getNom());
            existingRestaurant.setAdresse(restaurantDto.getAdresse());
            existingRestaurant.setTelephone(restaurantDto.getTelephone());
            existingRestaurant.setDevise(restaurantDto.getDevise());
            existingRestaurant.setDateCreation(restaurantDto.getDateCreation());
            existingRestaurant.setTauxTva(restaurantDto.getTauxTva());
            Restaurant updated = restaurantRepository.save(existingRestaurant);
            logger.info("{} Restaurant ID: {} mis à jour avec succès - Nom: {}", context, id, updated.getNom());
            return restaurantMapper.toDto(updated);
        } catch (Exception e) {
            logger.error("{} Erreur lors de la mise à jour du restaurant ID: {} - {}", context, id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du restaurant : " + e.getMessage());
        }
    }

    @Override
    public void deleteRestaurant(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression du restaurant ID: {}", context, id);
        if (!restaurantRepository.existsById(id)) {
            logger.error("{} Restaurant non trouvé avec l'ID: {}", context, id);
            throw new RuntimeException("Restaurant introuvable avec l'ID : " + id);
        }
        restaurantRepository.deleteById(id);
        logger.info("{} Restaurant ID: {} supprimé avec succès", context, id);
    }

    @Override
    public RestaurantDto getRestaurantById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération du restaurant avec l'ID: {}", context, id);
        return restaurantRepository.findById(id)
                .map(restaurantMapper::toDto)
                .orElseThrow(() -> {
                    logger.error("{} Restaurant non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Restaurant introuvable avec l'ID : " + id);
                });
    }

    @Override
    public List<RestaurantDto> getAllRestaurants() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les restaurants", context);
        List<RestaurantDto> restaurants = restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} restaurants récupérés avec succès", context, restaurants.size());
        return restaurants;
    }
}
