package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.mappers.PlatMapper;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlatServiceImplementation {

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

    public void delete(Long id) {
        if (!platRepository.existsById(id)) {
            throw new RuntimeException("Plat non trouvé");
        }
        platRepository.deleteById(id);
    }
}
