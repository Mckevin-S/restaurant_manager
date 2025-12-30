package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.IngredientDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.mappers.IngredientMapper;
import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.services.interfaces.IngredientServiceInterface;
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

    private final IngredientMapper ingredientMapper;
    private final IngredientRepository ingredientRepository;
    private final SimpMessagingTemplate messagingTemplate; // Pour le temps réel

    public IngredientServiceImplementation(IngredientMapper ingredientMapper,
                                           IngredientRepository ingredientRepository,
                                           SimpMessagingTemplate messagingTemplate) {
        this.ingredientMapper = ingredientMapper;
        this.ingredientRepository = ingredientRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     *  LOGIQUE TEMPS RÉEL : Vérifie le seuil et envoie une alerte WebSocket
     */
    private void verifierEtAlerter(Ingredient ingredient) {
        // Si quantité actuelle <= seuil d'alerte
        if (ingredient.getQuantiteActuelle().compareTo(ingredient.getSeuilAlerte()) <= 0) {

            Map<String, Object> alerte = new HashMap<>();
            alerte.put("id", ingredient.getId());
            alerte.put("nom", ingredient.getNom());
            alerte.put("quantite", ingredient.getQuantiteActuelle());
            alerte.put("unite", ingredient.getUniteMesure());

            String status = ingredient.getQuantiteActuelle().compareTo(BigDecimal.ZERO) <= 0 ? "RUPTURE" : "FAIBLE";
            alerte.put("status", status);
            alerte.put("message", "Attention : Stock " + status + " pour " + ingredient.getNom());

            // ✅ SOLUTION : Forcer l'interprétation de la Map comme un Object
            messagingTemplate.convertAndSend("/topic/stock/alertes", (Object) alerte);
        }
    }

    @Override
    public IngredientDto save(IngredientDto ingredientDto) {
        if (ingredientDto.getNom() == null || ingredientDto.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de l'ingrédient est obligatoire");
        }
        if (ingredientDto.getUniteMesure() == null || ingredientDto.getUniteMesure().trim().isEmpty()) {
            throw new RuntimeException("L'unité de mesure est obligatoire");
        }
        if (ingredientRepository.existsByNomIgnoreCase(ingredientDto.getNom())) {
            throw new RuntimeException("Un ingrédient avec ce nom existe déjà");
        }

        if (ingredientDto.getQuantiteActuelle() == null) ingredientDto.setQuantiteActuelle(BigDecimal.ZERO);
        if (ingredientDto.getSeuilAlerte() == null) ingredientDto.setSeuilAlerte(BigDecimal.ZERO);

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        // Vérification immédiate après création
        verifierEtAlerter(savedIngredient);

        return ingredientMapper.toDto(savedIngredient);
    }

    @Override
    public IngredientDto update(Long id, IngredientDto ingredientDto) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        if (ingredientDto.getNom() != null && !ingredientDto.getNom().trim().isEmpty()) {
            if (!ingredient.getNom().equalsIgnoreCase(ingredientDto.getNom()) &&
                    ingredientRepository.existsByNomIgnoreCase(ingredientDto.getNom())) {
                throw new RuntimeException("Un autre ingrédient avec ce nom existe déjà");
            }
            ingredient.setNom(ingredientDto.getNom());
        }

        if (ingredientDto.getQuantiteActuelle() != null) {
            if (ingredientDto.getQuantiteActuelle().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("La quantité actuelle ne peut pas être négative");
            }
            ingredient.setQuantiteActuelle(ingredientDto.getQuantiteActuelle());
        }

        if (ingredientDto.getUniteMesure() != null && !ingredientDto.getUniteMesure().trim().isEmpty()) {
            ingredient.setUniteMesure(ingredientDto.getUniteMesure());
        }

        if (ingredientDto.getSeuilAlerte() != null) {
            if (ingredientDto.getSeuilAlerte().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Le seuil d'alerte ne peut pas être négatif");
            }
            ingredient.setSeuilAlerte(ingredientDto.getSeuilAlerte());
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        // Alerte en cas de mise à jour manuelle vers un stock bas
        verifierEtAlerter(updatedIngredient);

        return ingredientMapper.toDto(updatedIngredient);
    }

    @Override
    public IngredientDto ajouterQuantite(Long id, BigDecimal quantite) {
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("La quantité à ajouter doit être positive");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        ingredient.setQuantiteActuelle(ingredient.getQuantiteActuelle().add(quantite));
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        // Pas forcément d'alerte ici (car le stock augmente), mais utile pour rafraîchir l'UI
        verifierEtAlerter(updatedIngredient);

        return ingredientMapper.toDto(updatedIngredient);
    }

    @Override
    public IngredientDto retirerQuantite(Long id, BigDecimal quantite) {
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("La quantité à retirer doit être positive");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        BigDecimal nouvelleQuantite = ingredient.getQuantiteActuelle().subtract(quantite);

        if (nouvelleQuantite.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Stock insuffisant pour " + ingredient.getNom());
        }

        ingredient.setQuantiteActuelle(nouvelleQuantite);
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        // ✅ CRUCIAL : Alerte immédiate si le retrait fait passer sous le seuil
        verifierEtAlerter(updatedIngredient);

        return ingredientMapper.toDto(updatedIngredient);
    }

    @Override
    public IngredientDto updateSeuilAlerte(Long id, BigDecimal nouveauSeuil) {
        if (nouveauSeuil == null || nouveauSeuil.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Le seuil d'alerte doit être positif ou nul");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        ingredient.setSeuilAlerte(nouveauSeuil);
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        // Vérifier si le nouveau seuil déclenche une alerte immédiatement
        verifierEtAlerter(updatedIngredient);

        return ingredientMapper.toDto(updatedIngredient);
    }

    // --- Méthodes de lecture (Lecture seule, pas besoin de temps réel ici) ---

    @Override
    public List<IngredientDto> getAll() {
        return ingredientRepository.findAll().stream().map(ingredientMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public IngredientDto getById(Long id) {
        return ingredientRepository.findById(id).map(ingredientMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé"));
    }

    @Override
    public void delete(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingrédient non trouvé");
        }
        ingredientRepository.deleteById(id);
    }

    @Override
    public List<IngredientDto> search(String keyword) {
        return ingredientRepository.findByNomContainingIgnoreCase(keyword).stream()
                .map(ingredientMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<IngredientDto> findIngredientsEnAlerte() {
        return ingredientRepository.findIngredientsEnAlerte().stream()
                .map(ingredientMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<IngredientDto> findByUniteMesure(String uniteMesure) {
        return ingredientRepository.findByUniteMesure(uniteMesure).stream()
                .map(ingredientMapper::toDto).collect(Collectors.toList());
    }
}