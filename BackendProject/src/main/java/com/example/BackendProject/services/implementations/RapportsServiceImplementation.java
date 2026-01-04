package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.RapportsDto;
import com.example.BackendProject.entities.Rapports;
import com.example.BackendProject.mappers.RapportsMapper;
import com.example.BackendProject.repository.RapportsRepository;
import com.example.BackendProject.services.interfaces.RapportServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RapportsServiceImplementation implements RapportServiceInterface {

    private final RapportsRepository rapportsRepository;
    private final RapportsMapper rapportsMapper;

    public RapportsServiceImplementation(RapportsRepository rapportsRepository,
                                         RapportsMapper rapportsMapper) {
        this.rapportsRepository = rapportsRepository;
        this.rapportsMapper = rapportsMapper;
    }

    // ================= CREATE =================
    @Override
    public RapportsDto createRapport(RapportsDto rapportsDto) {
        Rapports rapport = rapportsMapper.toEntity(rapportsDto);
        Rapports savedRapport = rapportsRepository.save(rapport);
        return rapportsMapper.toDto(savedRapport);
    }

    // ================= GET BY ID =================
    @Override
    public RapportsDto getRapportById(Long id) {
        Rapports rapport = rapportsRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Rapport non trouvé avec l'ID : " + id)
                );
        return rapportsMapper.toDto(rapport);
    }

    // ================= GET ALL =================
    @Override
    public List<RapportsDto> getAllRapports() {
        return rapportsRepository.findAll()
                .stream()
                .map(rapportsMapper::toDto)
                .collect(Collectors.toList());
    }

    // ================= UPDATE =================
    @Override
    public RapportsDto updateRapport(Long id, RapportsDto rapportsDto) {
        Rapports rapport = rapportsRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Rapport non trouvé avec l'ID : " + id)
                );

        // Mise à jour champ par champ (comme UtilisateurService)
        if (rapportsDto.getDateDebut() != null) {
            rapport.setDateDebut(rapportsDto.getDateDebut());
        }

        if (rapportsDto.getDateFin() != null) {
            rapport.setDateFin(rapportsDto.getDateFin());
        }

        if (rapportsDto.getChiffreAffaires() != null) {
            rapport.setChiffreAffaires(rapportsDto.getChiffreAffaires());
        }

        if (rapportsDto.getPlatsVendus() != null) {
            rapport.setPlatsVendus(rapportsDto.getPlatsVendus());
        }

        if (rapportsDto.getPerformanceServeurs() != null &&
                !rapportsDto.getPerformanceServeurs().isEmpty()) {
            rapport.setPerformanceServeurs(rapportsDto.getPerformanceServeurs());
        }

        Rapports updatedRapport = rapportsRepository.save(rapport);
        return rapportsMapper.toDto(updatedRapport);
    }

    // ================= DELETE =================
    @Override
    public void deleteRapport(Long id) {
        Rapports rapport = rapportsRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Rapport non trouvé avec l'ID : " + id)
                );

        rapportsRepository.delete(rapport);
    }
}
