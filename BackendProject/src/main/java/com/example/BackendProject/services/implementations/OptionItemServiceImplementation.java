package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.OptionItemDto;
import com.example.BackendProject.entities.OptionItem;
import com.example.BackendProject.mappers.OptionItemMapper;
import com.example.BackendProject.repository.OptionItemRepository;
import com.example.BackendProject.services.interfaces.OptionItemServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OptionItemServiceImplementation implements OptionItemServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(OptionItemServiceImplementation.class);
    private final OptionItemRepository optionItemRepository;
    private final OptionItemMapper optionItemMapper;

    public OptionItemServiceImplementation(OptionItemRepository optionItemRepository, OptionItemMapper optionItemMapper) {
        this.optionItemRepository = optionItemRepository;
        this.optionItemMapper = optionItemMapper;
    }

    @Override
    public OptionItemDto save(OptionItemDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'une option - Nom: {}", context, dto.getNom());
        OptionItem entity = optionItemMapper.toEntity(dto);
        OptionItem saved = optionItemRepository.save(entity);
        logger.info("{} Option sauvegardée avec succès. ID: {}, Nom: {}", context, saved.getId(), saved.getNom());
        return optionItemMapper.toDto(saved);
    }

    @Override
    public OptionItemDto update(Long id, OptionItemDto dto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour de l'option ID: {}", context, id);
        OptionItem existing = optionItemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Option non trouvée avec l'ID: {}", context, id);
                    return new RuntimeException("Option non trouvée avec l'ID: " + id);
                });

        existing.setNom(dto.getNom());
        existing.setPrixSupplementaire(dto.getPrixSupplementaire());

        OptionItem updated = optionItemRepository.save(existing);
        logger.info("{} Option ID: {} mise à jour avec succès", context, id);
        return optionItemMapper.toDto(updated);
    }

    @Override
    public List<OptionItemDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les options", context);
        List<OptionItemDto> options = optionItemRepository.findAll().stream()
                .map(optionItemMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} options récupérées avec succès", context, options.size());
        return options;
    }

    @Override
    public OptionItemDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de l'option avec l'ID: {}", context, id);
        return optionItemRepository.findById(id)
                .map(optionItemMapper::toDto)
                .orElseThrow(() -> {
                    logger.error("{} Option non trouvée avec l'ID: {}", context, id);
                    return new RuntimeException("Option non trouvée");
                });
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression de l'option ID: {}", context, id);
        if (!optionItemRepository.existsById(id)) {
            logger.error("{} Option non trouvée avec l'ID: {}", context, id);
            throw new RuntimeException("Option non trouvée avec l'ID: " + id);
        }
        optionItemRepository.deleteById(id);
        logger.info("{} Option ID: {} supprimée avec succès", context, id);
    }
}
