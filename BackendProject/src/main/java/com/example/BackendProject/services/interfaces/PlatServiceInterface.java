package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.PlatDto;

import java.util.List;

public interface PlatServiceInterface {

    PlatDto save(PlatDto platDto);

    List<PlatDto> getAll();

    PlatDto getById(Long id);

    void delete(Long id);

    PlatDto update(Long id, PlatDto platDto);

//    PlatDto getMostSoldPlat();
//
//    List<PlatDto> getTopSoldPlats(int limit);


}
