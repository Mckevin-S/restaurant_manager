package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.IngredientDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.mappers.IngredientMapper;
import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.services.interfaces.IngredientServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class IngredientServiceImplementation implements IngredientServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(IngredientServiceImplementation.class);
    private final IngredientMapper ingredientMapper;
    private final IngredientRepository ingredientRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public IngredientServiceImplementation(IngredientMapper ingredientMapper,
                                           IngredientRepository ingredientRepository,
                                           SimpMessagingTemplate messagingTemplate) {
        this.ingredientMapper = ingredientMapper;
        this.ingredientRepository = ingredientRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * LOGIQUE TEMPS RÉEL : Vérifie le seuil et envoie une alerte WebSocket
     */
    private void verifierEtAlerter(Ingredient ingredient) {
        String context = LoggingUtils.getLogContext();
        if (ingredient.getQuantiteActuelle().compareTo(ingredient.getSeuilAlerte()) <= 0) {
            
            Map<String, Object> alerte = new HashMap<>();
            alerte.put("id", ingredient.getId());
            alerte.put("nom", ingredient.getNom());
            alerte.put("quantite", ingredient.getQuantiteActuelle());
            alerte.put("unite", ingredient.getUniteMesure());

            String status = ingredient.getQuantiteActuelle().compareTo(BigDecimal.ZERO) <= 0 ? "RUPTURE" : "FAIBLE";
            alerte.put("status", status);
            alerte.put("message", "Attention : Stock " + status + " pour " + ingredient.getNom());

            logger.warn("{} ALERTE STOCK - Ingrédient: {}, Statut: {}, Quantité: {}", 
                        context, ingredient.getNom(), status, ingredient.getQuantiteActuelle());

            messagingTemplate.convertAndSend("/topic/stock/alertes", (Object) alerte);
        }
    }

    @Override
    public IngredientDto save(IngredientDto ingredientDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Création d'un nouvel ingrédient: {}", context, ingredientDto.getNom());

        if (ingredientDto.getNom() == null || ingredientDto.getNom().trim().isEmpty()) {
            logger.error("{} Erreur: Le nom est obligatoire", context);
            throw new RuntimeException("Le nom de l'ingrédient est obligatoire");
        }
        
        if (ingredientRepository.existsByNomIgnoreCase(ingredientDto.getNom())) {
            logger.error("{} Erreur: L'ingrédient '{}' existe déjà", context, ingredientDto.getNom());
            throw new RuntimeException("Un ingrédient avec ce nom existe déjà");
        }

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
        if (ingredient.getQuantiteActuelle() == null) ingredient.setQuantiteActuelle(BigDecimal.ZERO);
        if (ingredient.getSeuilAlerte() == null) ingredient.setSeuilAlerte(BigDecimal.ZERO);

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        logger.info("{} Ingrédient sauvegardé avec succès ID: {}", context, savedIngredient.getId());

        verifierEtAlerter(savedIngredient);
        return ingredientMapper.toDto(savedIngredient);
    }

    @Override
    public IngredientDto update(Long id, IngredientDto ingredientDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Mise à jour de l'ingrédient ID: {}", context, id);

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Ingrédient non trouvé ID: {}", context, id);
                    return new RuntimeException("Ingrédient non trouvé avec l'ID : " + id);
                });

        // Mise à jour logique avec logs
        if (ingredientDto.getNom() != null && !ingredientDto.getNom().trim().isEmpty()) {
            ingredient.setNom(ingredientDto.getNom());
        }

        if (ingredientDto.getQuantiteActuelle() != null) {
            if (ingredientDto.getQuantiteActuelle().compareTo(BigDecimal.ZERO) < 0) {
                logger.error("{} Quantité négative refusée: {}", context, ingredientDto.getQuantiteActuelle());
                throw new RuntimeException("La quantité actuelle ne peut pas être négative");
            }
            ingredient.setQuantiteActuelle(ingredientDto.getQuantiteActuelle());
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        logger.info("{} Mise à jour réussie pour l'ingrédient: {}", context, updatedIngredient.getNom());

        verifierEtAlerter(updatedIngredient);
        return ingredientMapper.toDto(updatedIngredient);
    }

    @Override
    public IngredientDto ajouterQuantite(Long id, BigDecimal quantite) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Ajout de stock - ID: {}, Quantité: +{}", context, id, quantite);

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));

        ingredient.setQuantiteActuelle(ingredient.getQuantiteActuelle().add(quantite));
        Ingredient updated = ingredientRepository.save(ingredient);
        
        logger.info("{} Nouveau stock pour {}: {}", context, updated.getNom(), updated.getQuantiteActuelle());
        return ingredientMapper.toDto(updated);
    }

    @Override
    public IngredientDto retirerQuantite(Long id, BigDecimal quantite) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Retrait de stock - ID: {}, Quantité: -{}", context, id, quantite);

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));

        BigDecimal nouvelleQuantite = ingredient.getQuantiteActuelle().subtract(quantite);

        if (nouvelleQuantite.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("{} Stock insuffisant pour {}. Actuel: {}, Demandé: {}", 
                         context, ingredient.getNom(), ingredient.getQuantiteActuelle(), quantite);
            throw new RuntimeException("Stock insuffisant pour " + ingredient.getNom());
        }

        ingredient.setQuantiteActuelle(nouvelleQuantite);
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        verifierEtAlerter(updatedIngredient);
        return ingredientMapper.toDto(updatedIngredient);
    }

    @Override
    public List<IngredientDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la liste complète des ingrédients", context);
        return ingredientRepository.findAll().stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.warn("{} Suppression de l'ingrédient ID: {}", context, id);
        
        if (!ingredientRepository.existsById(id)) {
            logger.error("{} Échec suppression: ID {} inexistant", context, id);
            throw new RuntimeException("Ingrédient non trouvé");
        }
        ingredientRepository.deleteById(id);
        logger.info("{} Ingrédient ID: {} supprimé", context, id);
    }

    @Override
    public List<IngredientDto> findIngredientsEnAlerte() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche des ingrédients en seuil critique", context);
        return ingredientRepository.findIngredientsEnAlerte().stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    // ... Les autres méthodes de recherche suivent le même schéma de log
    @Override
    public IngredientDto getById(Long id) {
        return ingredientRepository.findById(id).map(ingredientMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));
    }

    @Override
    public List<IngredientDto> search(String keyword) {
        return ingredientRepository.findByNomContainingIgnoreCase(keyword).stream()
                .map(ingredientMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<IngredientDto> findByUniteMesure(String uniteMesure) {
        return ingredientRepository.findByUniteMesure(uniteMesure).stream()
                .map(ingredientMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public IngredientDto updateSeuilAlerte(Long id, BigDecimal nouveauSeuil) {
        String context = LoggingUtils.getLogContext();
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new RuntimeException("Non trouvé"));
        ingredient.setSeuilAlerte(nouveauSeuil);
        Ingredient updated = ingredientRepository.save(ingredient);
        logger.info("{} Nouveau seuil d'alerte pour {}: {}", context, updated.getNom(), nouveauSeuil);
        verifierEtAlerter(updated);
        return ingredientMapper.toDto(updated);
    }
}