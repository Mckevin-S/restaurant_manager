package com.example.BackendProject.services.implementations;

import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.repository.RecetteItemRepository;
import com.example.BackendProject.repository.RecetteRepository;
import com.example.BackendProject.services.interfaces.RecetteItemServiceInterface;
import com.example.BackendProject.dto.RecetteItemDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.entities.RecetteItem;
import com.example.BackendProject.mappers.RecetteItemMapper;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecetteItemServiceImplementation implements RecetteItemServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RecetteItemServiceImplementation.class);
    private final RecetteItemRepository recetteItemRepository;
    private final RecetteRepository recetteRepository;
    private final IngredientRepository ingredientRepository;
    private final RecetteItemMapper mapper;

    public RecetteItemServiceImplementation(
            RecetteItemRepository recetteItemRepository,
            RecetteRepository recetteRepository,
            IngredientRepository ingredientRepository,
            RecetteItemMapper mapper
    ) {
        this.recetteItemRepository = recetteItemRepository;
        this.recetteRepository = recetteRepository;
        this.ingredientRepository = ingredientRepository;
        this.mapper = mapper;
    }

    @Override
    public RecetteItemDto save(RecetteItemDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'un item de recette - Recette ID: {}, Ingrédient ID: {}", 
                    context, dto.getRecette(), dto.getIngredient());

        Recette recette = recetteRepository.findById(dto.getRecette())
                .orElseThrow(() -> {
                    logger.error("{} Recette introuvable avec l'ID: {}", context, dto.getRecette());
                    return new RuntimeException("Recette introuvable");
                });

        Ingredient ingredient = ingredientRepository.findById(dto.getIngredient())
                .orElseThrow(() -> {
                    logger.error("{} Ingrédient introuvable avec l'ID: {}", context, dto.getIngredient());
                    return new RuntimeException("Ingrédient introuvable");
                });

        RecetteItem entity = mapper.toEntity(dto);
        entity.setRecette(recette);
        entity.setIngredient(ingredient);

        RecetteItem saved = recetteItemRepository.save(entity);
        logger.info("{} Item de recette sauvegardé avec succès. ID: {}", context, saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public RecetteItemDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de l'item de recette avec l'ID: {}", context, id);
        return recetteItemRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                    logger.error("{} Item de recette non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("RecetteItem introuvable");
                });
    }

    @Override
    public List<RecetteItemDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les items de recette", context);
        List<RecetteItemDto> items = recetteItemRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
        logger.info("{} {} items de recette récupérés avec succès", context, items.size());
        return items;
    }

    @Override
    public List<RecetteItemDto> getByRecette(Long recetteId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des items de recette pour la recette ID: {}", context, recetteId);
        List<RecetteItemDto> items = recetteItemRepository.findByRecetteId(recetteId)
                .stream()
                .map(mapper::toDto)
                .toList();
        logger.info("{} {} items récupérés pour la recette ID: {}", context, items.size(), recetteId);
        return items;
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de l'item de recette ID: {}", context, id);
        if (!recetteItemRepository.existsById(id)) {
            logger.error("{} Item de recette non trouvé avec l'ID: {}", context, id);
            throw new RuntimeException("Item de recette non trouvé avec l'ID: " + id);
        }
        recetteItemRepository.deleteById(id);
        logger.info("{} Item de recette ID: {} supprimé avec succès", context, id);
    }
}

