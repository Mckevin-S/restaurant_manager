package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.OptionItemDto;
import com.example.BackendProject.entities.OptionItem;
import com.example.BackendProject.mappers.OptionItemMapper;
import com.example.BackendProject.repository.OptionItemRepository;
import com.example.BackendProject.services.interfaces.OptionItemServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OptionItemServiceImplementation implements OptionItemServiceInterface {

    private final OptionItemRepository optionItemRepository;
    private final OptionItemMapper optionItemMapper;

    public OptionItemServiceImplementation(OptionItemRepository optionItemRepository, OptionItemMapper optionItemMapper) {
        this.optionItemRepository = optionItemRepository;
        this.optionItemMapper = optionItemMapper;
    }

    @Override
    public OptionItemDto save(OptionItemDto dto) {
        OptionItem entity = optionItemMapper.toEntity(dto);
        OptionItem saved = optionItemRepository.save(entity);
        return optionItemMapper.toDto(saved);
    }

    @Override
    public OptionItemDto update(Long id, OptionItemDto dto) {
        OptionItem existing = optionItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option non trouvée avec l'ID: " + id));

        existing.setNom(dto.getNom());
        existing.setPrixSupplementaire(dto.getPrixSupplementaire());

        return optionItemMapper.toDto(optionItemRepository.save(existing));
    }

    @Override
    public List<OptionItemDto> getAll() {
        return optionItemRepository.findAll().stream()
                .map(optionItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OptionItemDto getById(Long id) {
        return optionItemRepository.findById(id)
                .map(optionItemMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Option non trouvée"));
    }

    @Override
    public void delete(Long id) {
        optionItemRepository.deleteById(id);
    }
}
