package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.mappers.RestaurantMapper;
import com.example.BackendProject.repository.RestaurantRepository;
import com.example.BackendProject.services.interfaces.RestaurantServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImplementation implements RestaurantServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImplementation.class);
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "restaurant").toString() + "/";

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
            existingRestaurant.setEmail(restaurantDto.getEmail());
            existingRestaurant.setLogo(restaurantDto.getLogo());
            existingRestaurant.setDescription(restaurantDto.getDescription());
            existingRestaurant.setTauxTva(restaurantDto.getTauxTva());
            existingRestaurant.setFraisService(restaurantDto.getFraisService());
            existingRestaurant.setDevise(restaurantDto.getDevise());
            existingRestaurant.setDeviseSymbole(restaurantDto.getDeviseSymbole());
            existingRestaurant.setHeuresOuverture(restaurantMapper.toEntity(restaurantDto).getHeuresOuverture());
            existingRestaurant.setDateCreation(restaurantDto.getDateCreation());
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
    public RestaurantDto getSettings() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des paramètres du restaurant", context);
        return restaurantRepository.findAll().stream()
                .findFirst()
                .map(restaurantMapper::toDto)
                .orElseGet(() -> {
                    logger.info("{} Aucun restaurant trouvé, création d'un singleton par défaut", context);
                    Restaurant defaultResto = new Restaurant();
                    defaultResto.setNom("Mon Restaurant");
                    defaultResto.setAdresse("Adresse par défaut");
                    defaultResto.setTelephone("00000000");
                    defaultResto.setEmail("contact@restaurant.com");
                    defaultResto.setTauxTva(new BigDecimal("18.0"));
                    defaultResto.setFraisService(new BigDecimal("10.0"));
                    defaultResto.setDevise("FCFA");
                    defaultResto.setDeviseSymbole("FCFA");
                    // Initialisation des horaires par défaut en JSON
                    defaultResto.setHeuresOuverture("{\"lundi\":{\"ouvert\":true,\"debut\":\"09:00\",\"fin\":\"22:00\"},\"mardi\":{\"ouvert\":true,\"debut\":\"09:00\",\"fin\":\"22:00\"},\"mercredi\":{\"ouvert\":true,\"debut\":\"09:00\",\"fin\":\"22:00\"},\"jeudi\":{\"ouvert\":true,\"debut\":\"09:00\",\"fin\":\"22:00\"},\"vendredi\":{\"ouvert\":true,\"debut\":\"09:00\",\"fin\":\"23:00\"},\"samedi\":{\"ouvert\":true,\"debut\":\"10:00\",\"fin\":\"23:00\"},\"dimanche\":{\"ouvert\":false,\"debut\":\"10:00\",\"fin\":\"22:00\"}}");
                    
                    return restaurantMapper.toDto(restaurantRepository.save(defaultResto));
                });
    }

    @Override
    public RestaurantDto updateSettings(RestaurantDto restaurantDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Mise à jour des paramètres du restaurant", context);
        Restaurant restaurant = restaurantRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> new Restaurant());
        
        Restaurant updatedEntity = restaurantMapper.toEntity(restaurantDto);
        // On garde l'ID existant si on met à jour
        if (restaurant.getId() != null) {
            updatedEntity.setId(restaurant.getId());
        }
        
        return restaurantMapper.toDto(restaurantRepository.save(updatedEntity));
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

    @Override
    public RestaurantDto uploadLogo(MultipartFile file) throws IOException {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative d'upload de logo - Nom du fichier: {}", context, file.getOriginalFilename());
        
        // Récupérer les paramètres du restaurant (on assume qu'il y a un seul restaurant ou le premier)
        Restaurant restaurant = restaurantRepository.findAll().stream().findFirst()
                .orElseThrow(() -> {
                    logger.error("{} Aucun restaurant trouvé pour l'upload du logo", context);
                    return new RuntimeException("Aucun restaurant configuré");
                });

        if (file.isEmpty()) {
            logger.error("{} Fichier vide pour l'upload du logo", context);
            throw new RuntimeException("Le fichier est vide");
        }

        // Création du dossier si inexistant
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        // Suppression de l'ancien logo si il existe
        if (restaurant.getLogo() != null && !restaurant.getLogo().isEmpty()) {
            try {
                String oldFileName = restaurant.getLogo().substring(restaurant.getLogo().lastIndexOf("/") + 1);
                Path oldPath = uploadPath.resolve(oldFileName);
                if (Files.exists(oldPath)) {
                    Files.delete(oldPath);
                    logger.info("{} Ancien logo supprimé: {}", context, oldFileName);
                }
            } catch (IOException e) {
                logger.warn("{} Impossible de supprimer l'ancien logo: {}", context, e.getMessage());
            }
        }

        // Génération nom unique et redimensionnement
        String fileName = UUID.randomUUID().toString() + ".png";
        Path filePath = uploadPath.resolve(fileName);

        try {
            Thumbnails.of(file.getInputStream())
                    .size(300, 300)
                    .outputFormat("png")
                    .outputQuality(0.85)
                    .toFile(filePath.toFile());
        } catch (IOException e) {
            logger.error("{} Erreur lors du traitement du logo: {}", context, e.getMessage(), e);
            throw new IOException("Erreur lors du redimensionnement du logo: " + e.getMessage(), e);
        }

        restaurant.setLogo("/api/images/" + fileName);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        logger.info("{} Logo uploadé avec succès - URL: {}", context, savedRestaurant.getLogo());
        return restaurantMapper.toDto(savedRestaurant);
    }
}
