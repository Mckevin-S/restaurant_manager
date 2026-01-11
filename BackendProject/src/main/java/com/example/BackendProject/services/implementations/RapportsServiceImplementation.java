package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.RapportsDto;
import com.example.BackendProject.entities.Rapports;
import com.example.BackendProject.mappers.RapportsMapper;
import com.example.BackendProject.repository.RapportsRepository;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.services.interfaces.RapportServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.StatutCommande;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RapportsServiceImplementation implements RapportServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RapportsServiceImplementation.class);
    private final RapportsRepository rapportsRepository;
    private final RapportsMapper rapportsMapper;
    private final CommandeRepository commandeRepository;

    public RapportsServiceImplementation(RapportsRepository rapportsRepository,
                                         RapportsMapper rapportsMapper,
                                         CommandeRepository commandeRepository) {
        this.rapportsRepository = rapportsRepository;
        this.rapportsMapper = rapportsMapper;
        this.commandeRepository = commandeRepository;
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

        if (rapportsDto.getDateDebut() != null) rapport.setDateDebut(rapportsDto.getDateDebut());
        if (rapportsDto.getDateFin() != null) rapport.setDateFin(rapportsDto.getDateFin());
        if (rapportsDto.getChiffreAffaires() != null) rapport.setChiffreAffaires(rapportsDto.getChiffreAffaires());
        if (rapportsDto.getPlatsVendus() != null) rapport.setPlatsVendus(rapportsDto.getPlatsVendus());
        if (rapportsDto.getPerformanceServeurs() != null && !rapportsDto.getPerformanceServeurs().isEmpty()) {
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

    @Override
    public Object getStatistiques(String period) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des statistiques pour la période: {}", context, period);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        switch (period.toLowerCase()) {
            case "day":
                start = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "week":
                start = now.minusDays(7);
                break;
            case "year":
                start = now.withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "month":
            default:
                start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
        }

        Timestamp startTs = Timestamp.valueOf(start);
        Timestamp endTs = Timestamp.valueOf(now);

        List<com.example.BackendProject.entities.Commande> commandes = commandeRepository.findByDateHeureCommandeBetween(startTs, endTs);

        BigDecimal ca = commandes.stream()
                .filter(c -> c.getStatut() == StatutCommande.TERMINEE || c.getStatut() == StatutCommande.PAYEE)
                .map(c -> c.getTotalTtc() != null ? c.getTotalTtc() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long nbCommandes = commandes.size();
        BigDecimal ticketMoyen = nbCommandes > 0 ? ca.divide(BigDecimal.valueOf(nbCommandes), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        long nbClients = commandes.stream()
                .mapToLong(c -> c.getNombrePersonnes() != null ? c.getNombrePersonnes() : 1)
                .sum();

        List<Map<String, Object>> evolutionCA = new ArrayList<>();
        Map<String, BigDecimal> groupedByDate = commandes.stream()
                .filter(c -> c.getStatut() == StatutCommande.TERMINEE || c.getStatut() == StatutCommande.PAYEE)
                .collect(Collectors.groupingBy(
                        c -> c.getDateHeureCommande().toLocalDateTime().toLocalDate().toString(),
                        Collectors.reducing(BigDecimal.ZERO, c -> c.getTotalTtc() != null ? c.getTotalTtc() : BigDecimal.ZERO, BigDecimal::add)
                ));

        groupedByDate.forEach((date, value) -> {
            Map<String, Object> point = new HashMap<>();
            point.put("date", date);
            point.put("ca", value);
            evolutionCA.add(point);
        });

        evolutionCA.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));

        Map<String, Object> stats = new HashMap<>();
        stats.put("ca", ca);
        stats.put("nbCommandes", nbCommandes);
        stats.put("ticketMoyen", ticketMoyen);
        stats.put("nbClients", nbClients);
        stats.put("evolutionCA", evolutionCA);

        return stats;
    }
}
