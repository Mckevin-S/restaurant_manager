
package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.mappers.RestaurantMapper;
import com.example.BackendProject.repository.RestaurantRepository;
import com.example.BackendProject.services.interfaces.RestaurantServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImplementation implements RestaurantServiceInterface {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImplementation(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public RestaurantDto createRestaurant(RestaurantDto restaurantDto) {
        if (restaurantDto == null || restaurantDto.getNom() == null || restaurantDto.getNom().isBlank()) {
            throw new RuntimeException("Données incorrectes pour la création du restaurant");
        }
        Restaurant saved = restaurantRepository.save(restaurantMapper.toEntity(restaurantDto));
        return restaurantMapper.toDto(saved);
    }

    @Override
    public RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto) {
        if (restaurantDto == null) {
            throw new RuntimeException("Données incorrectes pour la mise à jour");
        }else{
            Restaurant existingRestaurant = restaurantRepository.findById(id).get();
            if (existingRestaurant == null) {
                throw new RuntimeException("Restaurant introuvable avec l'ID : " + id);
            }else {
                try{
                    existingRestaurant.setNom(restaurantDto.getNom());
                    existingRestaurant.setAdresse(restaurantDto.getAdresse());
                    existingRestaurant.setTelephone(restaurantDto.getTelephone());
                    existingRestaurant.setDevise(restaurantDto.getDevise());
                    existingRestaurant.setDateCreation(restaurantDto.getDateCreation());
                    existingRestaurant.setTauxTva(restaurantDto.getTauxTva());
                    Restaurant updated = restaurantRepository.save(existingRestaurant);
                    return restaurantMapper.toDto(updated);
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors de la mise à jour du restaurant : " + e.getMessage());
                }
            }
        }

    }

    @Override
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant introuvable avec l'ID : " + id);
        }
        restaurantRepository.deleteById(id);
    }

    @Override
    public RestaurantDto getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Restaurant introuvable avec l'ID : " + id));
    }

    @Override
    public List<RestaurantDto> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toDto)
                .collect(Collectors.toList());
    }
}
