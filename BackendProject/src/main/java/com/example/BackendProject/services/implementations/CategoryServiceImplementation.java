package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CategoryDto;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.mappers.CategoryMapper;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.repository.MenuRepository;
import com.example.BackendProject.services.interfaces.CategoryServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImplementation implements CategoryServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImplementation.class);
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;

    public CategoryServiceImplementation(CategoryMapper categoryMapper,
                                         CategoryRepository categoryRepository,
                                         MenuRepository menuRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de création d'une catégorie : {}", context, categoryDto.getNom());

        // Validation des champs obligatoires
        if (categoryDto.getNom() == null || categoryDto.getNom().trim().isEmpty()) {
            logger.error("{} Erreur validation : Le nom de la catégorie est obligatoire", context);
            throw new RuntimeException("Le nom de la catégorie est obligatoire");
        }

        // Vérifier que le menu existe si fourni
        if (categoryDto.getMenuId() != null && categoryDto.getMenuId() != null) {
            if (!menuRepository.existsById(categoryDto.getMenuId())) {
                logger.error("{} Menu non trouvé avec l'ID : {}", context, categoryDto.getMenuId());
                throw new RuntimeException("Menu non trouvé avec l'ID : " + categoryDto.getMenuId());
            }
        }

        // Si l'ordre d'affichage n'est pas fourni, le définir automatiquement
        if (categoryDto.getOrdreAffichage() == null &&
                categoryDto.getMenuId() != null &&
                categoryDto.getMenuId() != null) {
            Integer maxOrdre = categoryRepository.findMaxOrdreAffichageByMenuId(categoryDto.getMenuId());
            categoryDto.setOrdreAffichage(maxOrdre != null ? maxOrdre + 1 : 1);
            logger.info("{} Ordre d'affichage généré automatiquement : {}", context, categoryDto.getOrdreAffichage());
        }

        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        logger.info("{} Catégorie sauvegardée avec succès. ID: {}", context, savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public List<CategoryDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de toutes les catégories", context);
        List<CategoryDto> categories = categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} catégories récupérées", context, categories.size());
        return categories;
    }

    @Override
    public CategoryDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de la catégorie ID : {}", context, id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Catégorie non trouvée ID : {}", context, id);
                    return new RuntimeException("Catégorie non trouvée avec l'ID : " + id);
                });

        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour de la catégorie ID : {}", context, id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Catégorie non trouvée pour mise à jour ID : {}", context, id);
                    return new RuntimeException("Catégorie non trouvée avec l'ID : " + id);
                });

        // Mise à jour des champs
        if (categoryDto.getNom() != null && !categoryDto.getNom().trim().isEmpty()) {
            category.setNom(categoryDto.getNom());
        }

        if (categoryDto.getDescription() != null) {
            category.setDescription(categoryDto.getDescription());
        }

        if (categoryDto.getOrdreAffichage() != null) {
            category.setOrdreAffichage(categoryDto.getOrdreAffichage());
        }

        // Mise à jour du menu si fourni
        if (categoryDto.getMenuId() != null && categoryDto.getMenuId() != null) {
            if (!menuRepository.existsById(categoryDto.getMenuId())) {
                logger.error("{} Menu de remplacement non trouvé ID : {}", context, categoryDto.getMenuId());
                throw new RuntimeException("Menu non trouvé avec l'ID : " + categoryDto.getMenuId());
            }
            Menu menu = menuRepository.findById(categoryDto.getMenuId()).get();
            category.setMenu(menu);
        }

        Category updatedCategory = categoryRepository.save(category);
        logger.info("{} Catégorie ID : {} mise à jour avec succès", context, id);

        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.warn("{} Tentative de suppression de la catégorie ID : {}", context, id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Impossible de supprimer : Catégorie ID {} introuvable", context, id);
                    return new RuntimeException("Catégorie non trouvée avec l'ID : " + id);
                });

        // Vérifier s'il y a des plats liés
         if (category.getPlats() != null && !category.getPlats().isEmpty()) {
             logger.error("{} Échec suppression : La catégorie ID {} contient encore des plats", context, id);
             throw new RuntimeException("Impossible de supprimer une catégorie contenant des plats");
         }

        categoryRepository.delete(category);
        logger.info("{} Catégorie ID : {} supprimée avec succès", context, id);
    }

    @Override
    public List<CategoryDto> findByMenuId(Long menuId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des catégories pour le menu ID : {}", context, menuId);

        if (!menuRepository.existsById(menuId)) {
            logger.error("{} Menu non trouvé ID : {}", context, menuId);
            throw new RuntimeException("Menu non trouvé avec l'ID : " + menuId);
        }

        return categoryRepository.findByMenuIdOrderByOrdreAffichageAsc(menuId)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> search(String keyword) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche de catégories avec le mot-clé : '{}'", context, keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }

        List<CategoryDto> result = categoryRepository.findByNomContainingIgnoreCase(keyword)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        
        logger.info("{} {} résultats trouvés pour la recherche '{}'", context, result.size(), keyword);
        return result;
    }

    /**
     * Réorganiser l'ordre d'affichage des catégories
     */
    public void reorderCategories(Long menuId, List<Long> categoryIds) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Réorganisation des catégories pour le menu ID : {}", context, menuId);

        if (!menuRepository.existsById(menuId)) {
            logger.error("{} Menu non trouvé pour réorganisation ID : {}", context, menuId);
            throw new RuntimeException("Menu non trouvé avec l'ID : " + menuId);
        }

        for (int i = 0; i < categoryIds.size(); i++) {
            Long categoryId = categoryIds.get(i);
            int newOrder = i + 1;
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                        logger.error("{} Catégorie ID {} introuvable durant la réorganisation", context, categoryId);
                        return new RuntimeException("Catégorie non trouvée avec l'ID : " + categoryId);
                    });

            if (!category.getMenu().getId().equals(menuId)) {
                logger.error("{} Conflit : La catégorie {} n'appartient pas au menu {}", context, categoryId, menuId);
                throw new RuntimeException("La catégorie " + categoryId + " n'appartient pas au menu " + menuId);
            }

            category.setOrdreAffichage(newOrder);
            categoryRepository.save(category);
        }
        logger.info("{} Réorganisation terminée pour {} catégories", context, categoryIds.size());
    }
}