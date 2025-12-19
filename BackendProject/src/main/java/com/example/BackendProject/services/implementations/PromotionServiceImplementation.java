package com.example.BackendProject.services.implementations;


import com.example.BackendProject.dto.PromotionDto;
import com.example.BackendProject.entities.Promotion;
import com.example.BackendProject.mappers.PromotionMapper; // Remplacez par le nom exact de votre mapper
import com.example.BackendProject.repository.PromotionRepository;
import com.example.BackendProject.services.interfaces.PromotionServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImplementation implements PromotionServiceInterface {

    private final PromotionRepository repository;
    private final PromotionMapper mapper; // Injection de votre mapper déjà implémenté

    @Override
    public PromotionDto save(PromotionDto dto) {
        Promotion entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public List<PromotionDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Promotion introuvable avec l'ID : " + id));
    }

    @Override
    public PromotionDto update(Long id, PromotionDto dto) {
        return repository.findById(id)
                .map(existingEntity -> {
                    // On transforme le DTO de mise à jour en entité
                    Promotion updatedEntity = mapper.toEntity(dto);
                    updatedEntity.setId(id); // On s'assure de garder le même ID
                    return mapper.toDto(repository.save(updatedEntity));
                })
                .orElseThrow(() -> new RuntimeException("Impossible de mettre à jour : Promotion inexistante"));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Suppression impossible : ID inconnu");
        }
        repository.deleteById(id);
    }
}