package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.RapportsDto;
import com.example.BackendProject.entities.Rapports;
import com.example.BackendProject.mappers.RapportsMapper;
import com.example.BackendProject.repository.RapportsRepository;
import com.example.BackendProject.services.interfaces.RapportServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RapportsServiceImplementation implements RapportServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RapportsServiceImplementation.class);
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
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de création d'un rapport", context);
        Rapports rapport = rapportsMapper.toEntity(rapportsDto);
        Rapports savedRapport = rapportsRepository.save(rapport);
        logger.info("{} Rapport créé avec succès. ID: {}", context, savedRapport.getId());
        return rapportsMapper.toDto(savedRapport);
    }

    // ================= GET BY ID =================
    @Override
    public RapportsDto getRapportById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération du rapport avec l'ID: {}", context, id);
        Rapports rapport = rapportsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Rapport non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Rapport non trouvé avec l'ID : " + id);
                });
        logger.info("{} Rapport ID: {} récupéré avec succès", context, id);
        return rapportsMapper.toDto(rapport);
    }

    // ================= GET ALL =================
    @Override
    public List<RapportsDto> getAllRapports() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les rapports", context);
        List<RapportsDto> rapports = rapportsRepository.findAll()
                .stream()
                .map(rapportsMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} rapports récupérés avec succès", context, rapports.size());
        return rapports;
    }

    // ================= UPDATE =================
    @Override
    public RapportsDto updateRapport(Long id, RapportsDto rapportsDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour du rapport ID: {}", context, id);
        Rapports rapport = rapportsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Rapport non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Rapport non trouvé avec l'ID : " + id);
                });

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
        logger.info("{} Rapport ID: {} mis à jour avec succès", context, id);
        return rapportsMapper.toDto(updatedRapport);
    }

    // ================= DELETE =================
    @Override
    public void deleteRapport(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression du rapport ID: {}", context, id);
        Rapports rapport = rapportsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Rapport non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Rapport non trouvé avec l'ID : " + id);
                });

        rapportsRepository.delete(rapport);
        logger.info("{} Rapport ID: {} supprimé avec succès", context, id);
    }
}
