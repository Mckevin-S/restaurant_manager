package com.example.BackendProject.services.implementations;

import com.example.BackendProject.repository.RecetteRepository;
import com.example.BackendProject.services.interfaces.RecetteServiceInterface;
import com.example.BackendProject.dto.RecetteDto;
import com.example.BackendProject.entities.Recette;
import com.example.BackendProject.mappers.RecetteMapper;
import com.example.BackendProject.utils.LoggingUtils;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecetteServiceImplementation implements RecetteServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RecetteServiceImplementation.class);
    private final RecetteRepository recetteRepository;
    private final RecetteMapper recetteMapper;

    public RecetteServiceImplementation(
            RecetteRepository recetteRepository,
            RecetteMapper recetteMapper
    ) {
        this.recetteRepository = recetteRepository;
        this.recetteMapper = recetteMapper;
    }

    @Override
    public RecetteDto save(RecetteDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'une recette - Plat ID: {}", 
                    context, dto.getPlat() != null ? dto.getPlat() : "N/A");
        Recette recette = recetteMapper.toEntity(dto);
        Recette saved = recetteRepository.save(recette);
        logger.info("{} Recette sauvegardée avec succès. ID: {}", context, saved.getId());
        return recetteMapper.toDto(saved);
    }

    @Override
    public RecetteDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la recette avec l'ID: {}", context, id);
        Recette recette = recetteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Recette non trouvée avec l'ID: {}", context, id);
                    return new EntityNotFoundException("Recette introuvable");
                });
        logger.info("{} Recette ID: {} récupérée avec succès", context, id);
        return recetteMapper.toDto(recette);
    }

    @Override
    public List<RecetteDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les recettes", context);
        List<RecetteDto> recettes = recetteRepository.findAll()
                .stream()
                .map(recetteMapper::toDto)
                .toList();
        logger.info("{} {} recettes récupérées avec succès", context, recettes.size());
        return recettes;
    }

    @Override
    public List<RecetteDto> getByPlat(Long platId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des recettes pour le plat ID: {}", context, platId);
        List<RecetteDto> recettes = recetteRepository.findByPlatId(platId)
                .stream()
                .map(recetteMapper::toDto)
                .toList();
        logger.info("{} {} recettes récupérées pour le plat ID: {}", context, recettes.size(), platId);
        return recettes;
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de la recette ID: {}", context, id);
        if (!recetteRepository.existsById(id)) {
            logger.error("{} Recette non trouvée avec l'ID: {}", context, id);
            throw new RuntimeException("Recette non trouvée avec l'ID: " + id);
        }
        recetteRepository.deleteById(id);
        logger.info("{} Recette ID: {} supprimée avec succès", context, id);
    }
}

