package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.mappers.PlatMapper;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.services.interfaces.PlatServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlatServiceImplementation implements PlatServiceInterface {

    private final PlatRepository platRepository;
    private final CategoryRepository categoryRepository;
    private final PlatMapper platMapper;

    public PlatServiceImplementation(PlatRepository platRepository,
                                     CategoryRepository categoryRepository,
                                     PlatMapper platMapper) {
        this.platRepository = platRepository;
        this.categoryRepository = categoryRepository;
        this.platMapper = platMapper;
    }

    public PlatDto save(PlatDto platDto) {
        if (platDto.getCategory() != null) {
//            if (!categoryRepository.existsById(platDto.getCategory())) {
//                throw new RuntimeException("Catégorie non trouvée");
//            }
        }
        Plat plat = platMapper.toEntity(platDto);
        return platMapper.toDto(platRepository.save(plat));
    }

    @Transactional(readOnly = true)
    public List<PlatDto> getAll() {
        return platRepository.findAll().stream()
                .map(platMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlatDto getById(Long id) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé"));
        return platMapper.toDto(plat);
    }

    @Override
    public PlatDto update(Long id, PlatDto platDto) {
        // 1. Récupérer le plat existant ou lancer une erreur
        Plat existingPlat = platRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Impossible de mettre à jour : Plat non trouvé avec l'ID " + id));

        // 2. Mettre à jour les champs simples
        existingPlat.setCategory(platDto.getCategory());
        existingPlat.setNom(platDto.getNom());
        existingPlat.setDescription(platDto.getDescription());
        existingPlat.setPrix(platDto.getPrix());
        existingPlat.setPhotoUrl(platDto.getPhotoUrl());
        existingPlat.setRecette(platDto.getRecette());
        // existingPlat.setDisponible(platDto.isDisponible()); // Si vous avez ce champ

        // 3. Gérer la mise à jour de la catégorie (si fournie)
        if (platDto.getCategory() != null) {
            // Ici, on part du principe que vous avez une méthode dans CategoryRepository
            // pour récupérer l'entité Catégorie à partir de son ID contenu dans le DTO
            var category = categoryRepository.findById(platDto.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            existingPlat.setCategory(category);
        }

        // 4. Sauvegarder les modifications et retourner le DTO
        Plat updatedPlat = platRepository.save(existingPlat);
        return platMapper.toDto(updatedPlat);
    }

    public void delete(Long id) {
        if (!platRepository.existsById(id)) {
            throw new RuntimeException("Plat non trouvé");
        }
        platRepository.deleteById(id);
    }
}
