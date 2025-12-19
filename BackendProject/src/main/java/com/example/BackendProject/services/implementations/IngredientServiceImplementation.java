package com.example.BackendProject.services.implementations;


import com.example.BackendProject.dto.IngredientDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.mappers.IngredientMapper;
import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.services.interfaces.IngredientServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IngredientServiceImplementation implements IngredientServiceInterface {

    private final IngredientMapper ingredientMapper;
    private final IngredientRepository ingredientRepository;

    public IngredientServiceImplementation(IngredientMapper ingredientMapper,
                                           IngredientRepository ingredientRepository) {
        this.ingredientMapper = ingredientMapper;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public IngredientDto save(IngredientDto ingredientDto) {
        // Validation des champs obligatoires
        if (ingredientDto.getNom() == null || ingredientDto.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de l'ingrédient est obligatoire");
        }

        if (ingredientDto.getUniteMesure() == null || ingredientDto.getUniteMesure().trim().isEmpty()) {
            throw new RuntimeException("L'unité de mesure est obligatoire");
        }

        // Vérifier qu'un ingrédient avec ce nom n'existe pas déjà
        if (ingredientRepository.existsByNomIgnoreCase(ingredientDto.getNom())) {
            throw new RuntimeException("Un ingrédient avec ce nom existe déjà");
        }

        // Définir les valeurs par défaut
        if (ingredientDto.getQuantiteActuelle() == null) {
            ingredientDto.setQuantiteActuelle(BigDecimal.ZERO);
        }

        if (ingredientDto.getSeuilAlerte() == null) {
            ingredientDto.setSeuilAlerte(BigDecimal.ZERO);
        }

        // Validation des quantités
        if (ingredientDto.getQuantiteActuelle().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("La quantité actuelle ne peut pas être négative");
        }

        if (ingredientDto.getSeuilAlerte().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Le seuil d'alerte ne peut pas être négatif");
        }

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        return ingredientMapper.toDto(savedIngredient);
    }

    @Override
    public List<IngredientDto> getAll() {
        return ingredientRepository.findAll()
                .stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDto getById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        return ingredientMapper.toDto(ingredient);
    }

    @Override
    public IngredientDto update(Long id, IngredientDto ingredientDto) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        // Mise à jour du nom
        if (ingredientDto.getNom() != null && !ingredientDto.getNom().trim().isEmpty()) {
            // Vérifier que le nouveau nom n'est pas déjà utilisé par un autre ingrédient
            if (!ingredient.getNom().equalsIgnoreCase(ingredientDto.getNom()) &&
                    ingredientRepository.existsByNomIgnoreCase(ingredientDto.getNom())) {
                throw new RuntimeException("Un autre ingrédient avec ce nom existe déjà");
            }
            ingredient.setNom(ingredientDto.getNom());
        }

        // Mise à jour de la quantité actuelle
        if (ingredientDto.getQuantiteActuelle() != null) {
            if (ingredientDto.getQuantiteActuelle().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("La quantité actuelle ne peut pas être négative");
            }
            ingredient.setQuantiteActuelle(ingredientDto.getQuantiteActuelle());
        }

        // Mise à jour de l'unité de mesure
        if (ingredientDto.getUniteMesure() != null && !ingredientDto.getUniteMesure().trim().isEmpty()) {
            ingredient.setUniteMesure(ingredientDto.getUniteMesure());
        }

        // Mise à jour du seuil d'alerte
        if (ingredientDto.getSeuilAlerte() != null) {
            if (ingredientDto.getSeuilAlerte().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Le seuil d'alerte ne peut pas être négatif");
            }
            ingredient.setSeuilAlerte(ingredientDto.getSeuilAlerte());
        }

        // Sauvegarde
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        return ingredientMapper.toDto(updatedIngredient);
    }

    @Override
    public void delete(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        // Vérifier si l'ingrédient est utilisé dans des recettes (optionnel)
        // if (!ingredient.getRecetteItems().isEmpty()) {
        //     throw new RuntimeException("Impossible de supprimer un ingrédient utilisé dans des recettes");
        // }

        ingredientRepository.delete(ingredient);
    }

    @Override
    public List<IngredientDto> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }

        return ingredientRepository.findByNomContainingIgnoreCase(keyword)
                .stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<IngredientDto> findIngredientsEnAlerte() {
        return ingredientRepository.findIngredientsEnAlerte()
                .stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<IngredientDto> findByUniteMesure(String uniteMesure) {
        if (uniteMesure == null || uniteMesure.trim().isEmpty()) {
            throw new RuntimeException("L'unité de mesure ne peut pas être vide");
        }

        return ingredientRepository.findByUniteMesure(uniteMesure)
                .stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDto ajouterQuantite(Long id, BigDecimal quantite) {
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("La quantité à ajouter doit être positive");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrédient non trouvé avec l'ID : " + id));

        BigDecimal nouvelleQuantite = ingredient.getQuantiteActuelle().add(quantite);
        ingredient.setQuantiteActuelle(nouvelleQuantite);

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

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
            throw new RuntimeException("Quantité insuffisante. Stock actuel : " +
                    ingredient.getQuantiteActuelle() + " " + ingredient.getUniteMesure());
        }

        ingredient.setQuantiteActuelle(nouvelleQuantite);

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

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

        return ingredientMapper.toDto(updatedIngredient);
    }
}
