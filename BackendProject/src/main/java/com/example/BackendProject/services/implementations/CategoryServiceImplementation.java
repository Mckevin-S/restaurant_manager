package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CategoryDto;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Menu;
import com.example.BackendProject.mappers.CategoryMapper;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.repository.MenuRepository;
import com.example.BackendProject.services.interfaces.CategoryServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImplementation implements CategoryServiceInterface {

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
        // Validation des champs obligatoires
        if (categoryDto.getNom() == null || categoryDto.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de la catégorie est obligatoire");
        }

        // Vérifier que le menu existe si fourni
        if (categoryDto.getMenu() != null && categoryDto.getMenu().getId() != null) {
            if (!menuRepository.existsById(categoryDto.getMenu().getId())) {
                throw new RuntimeException("Menu non trouvé avec l'ID : " + categoryDto.getMenu().getId());
            }
        }

        // Si l'ordre d'affichage n'est pas fourni, le définir automatiquement
        if (categoryDto.getOrdreAffichage() == null &&
                categoryDto.getMenu() != null &&
                categoryDto.getMenu().getId() != null) {
            Integer maxOrdre = categoryRepository.findMaxOrdreAffichageByMenuId(categoryDto.getMenu().getId());
            categoryDto.setOrdreAffichage(maxOrdre != null ? maxOrdre + 1 : 1);
        }

        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + id));

        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + id));

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
        if (categoryDto.getMenu() != null && categoryDto.getMenu().getId() != null) {
            if (!menuRepository.existsById(categoryDto.getMenu().getId())) {
                throw new RuntimeException("Menu non trouvé avec l'ID : " + categoryDto.getMenu().getId());
            }
            Menu menu = menuRepository.findById(categoryDto.getMenu().getId()).get();
            category.setMenu(menu);
        }

        // Sauvegarde
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + id));

//         Vérifier s'il y a des plats liés (optionnel)
         if (category.getPlats() != null && !category.getPlats().isEmpty()) {
             throw new RuntimeException("Impossible de supprimer une catégorie contenant des plats");
         }

        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> findByMenuId(Long menuId) {
        // Vérifier que le menu existe
        if (!menuRepository.existsById(menuId)) {
            throw new RuntimeException("Menu non trouvé avec l'ID : " + menuId);
        }

        // Retourner les catégories triées par ordre d'affichage
        return categoryRepository.findByMenuIdOrderByOrdreAffichageAsc(menuId)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }

        return categoryRepository.findByNomContainingIgnoreCase(keyword)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Réorganiser l'ordre d'affichage des catégories
     */
    public void reorderCategories(Long menuId, List<Long> categoryIds) {
        if (!menuRepository.existsById(menuId)) {
            throw new RuntimeException("Menu non trouvé avec l'ID : " + menuId);
        }

        for (int i = 0; i < categoryIds.size(); i++) {
            Long categoryId = categoryIds.get(i);
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + categoryId));

            if (!category.getMenu().getId().equals(menuId)) {
                throw new RuntimeException("La catégorie " + categoryId + " n'appartient pas au menu " + menuId);
            }

            category.setOrdreAffichage(i + 1);
            categoryRepository.save(category);
        }
    }
}
