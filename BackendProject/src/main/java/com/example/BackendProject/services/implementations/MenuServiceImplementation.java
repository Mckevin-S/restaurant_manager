package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.MenuDto;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.mappers.MenuMapper;
import com.example.BackendProject.repository.MenuRepository;
import com.example.BackendProject.services.interfaces.MenuServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImplementation implements MenuServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImplementation.class);
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public MenuServiceImplementation(MenuRepository menuRepository, MenuMapper menuMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
    }

    @Override
    public MenuDto save(MenuDto menuDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'un menu - Nom: {}", context, menuDto.getNom());
        if (menuDto.getNom() == null || menuDto.getNom().trim().isEmpty()) {
            logger.error("{} Erreur de validation: le nom du menu est obligatoire", context);
            throw new RuntimeException("Le nom du menu est obligatoire");
        }

        // Vérifier si un menu avec le même nom existe déjà
        if (menuRepository.findByNomIgnoreCase(menuDto.getNom()).isPresent()) {
            logger.warn("{} Tentative de création d'un menu avec un nom déjà existant: {}", context, menuDto.getNom());
            throw new RuntimeException("Un menu avec ce nom existe déjà");
        }

        Menu menu = menuMapper.toEntity(menuDto);
        Menu savedMenu = menuRepository.save(menu);
        logger.info("{} Menu sauvegardé avec succès. ID: {}, Nom: {}", context, savedMenu.getId(), savedMenu.getNom());
        return menuMapper.toDto(savedMenu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les menus", context);
        List<MenuDto> menus = menuRepository.findAll()
                .stream()
                .map(menuMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} menus récupérés avec succès", context, menus.size());
        return menus;
    }

    @Override
//    @Transactional(readOnly = true)
    public MenuDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération du menu avec l'ID: {}", context, id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Menu non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Menu non trouvé avec l'ID : " + id);
                });
        logger.info("{} Menu ID: {} récupéré avec succès - Nom: {}", context, id, menu.getNom());
        return menuMapper.toDto(menu);
    }

    @Override
    public MenuDto update(Long id, MenuDto menuDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour du menu ID: {}", context, id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Menu non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Menu non trouvé avec l'ID : " + id);
                });
        menu.setNom(menuDto.getNom());
        Menu updatedMenu = menuRepository.save(menu);
        logger.info("{} Menu ID: {} mis à jour avec succès - Nouveau nom: {}", context, id, updatedMenu.getNom());
        return menuMapper.toDto(updatedMenu);
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression du menu ID: {}", context, id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Menu non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Menu non trouvé avec l'ID : " + id);
                });

        // Vérification d'intégrité : Ne pas supprimer si des catégories y sont liées
        if (menu.getCategories() != null && !menu.getCategories().isEmpty()) {
            logger.warn("{} Tentative de suppression du menu ID: {} contenant {} catégories", context, id, menu.getCategories().size());
            throw new RuntimeException("Impossible de supprimer un menu contenant des catégories actives");
        }

        menuRepository.delete(menu);
        logger.info("{} Menu ID: {} supprimé avec succès", context, id);
    }
}
