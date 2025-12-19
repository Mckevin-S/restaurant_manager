package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.PromotionDto;

import java.util.List;

public interface PromotionServiceInterface {

    PromotionDto save(PromotionDto dto);

    PromotionDto update(Long id, PromotionDto dto);

    List<PromotionDto> findAll();

    PromotionDto findById(Long id);

    void delete(Long id);

}
