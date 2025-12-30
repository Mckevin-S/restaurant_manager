package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.PlatDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PlatServiceInterface {

    PlatDto save(PlatDto platDto);

    List<PlatDto> getAll();

    PlatDto getById(Long id);

    void delete(Long id);

    PlatDto update(Long id, PlatDto platDto);

    PlatDto modifierDisponibilite(Long id, boolean estDisponible);

    List<PlatDto> getMenuActif();

    PlatDto uploadPlatImage(Long id, MultipartFile file) throws IOException;

    List<Map<String, Object>> getStatistiquesPlatsVendus();

//    PlatDto getMostSoldPlat();
//
//    List<PlatDto> getTopSoldPlats(int limit);


}
