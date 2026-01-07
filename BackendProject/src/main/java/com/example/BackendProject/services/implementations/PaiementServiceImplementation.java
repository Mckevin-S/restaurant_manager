package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.PaiementDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Paiement;
import com.example.BackendProject.mappers.PaiementMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.PaiementRepository;
import com.example.BackendProject.services.interfaces.PaiementServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypePaiement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaiementServiceImplementation implements PaiementServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(PaiementServiceImplementation.class);
    private final PaiementMapper paiementMapper;
    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;

    public PaiementServiceImplementation(PaiementMapper paiementMapper,
                                         PaiementRepository paiementRepository,
                                         CommandeRepository commandeRepository) {
        this.paiementMapper = paiementMapper;
        this.paiementRepository = paiementRepository;
        this.commandeRepository = commandeRepository;
    }

    @Override
    public PaiementDto save(PaiementDto paiementDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'un paiement - Commande ID: {}, Montant: {}, Type: {}", 
                    context, paiementDto.getCommande(), paiementDto.getMontant(), paiementDto.getTypePaiement());
        // Validation des champs obligatoires
        if (paiementDto.getCommande() == null || paiementDto.getCommande() == null) {
            logger.error("{} Erreur de validation: la commande est obligatoire", context);
            throw new RuntimeException("La commande est obligatoire");
        }

        if (paiementDto.getMontant() == null || paiementDto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("{} Erreur de validation: le montant doit être supérieur à 0", context);
            throw new RuntimeException("Le montant doit être supérieur à 0");
        }

        if (paiementDto.getTypePaiement() == null) {
            logger.error("{} Erreur de validation: le type de paiement est obligatoire", context);
            throw new RuntimeException("Le type de paiement est obligatoire");
        }

        Long commandeId = paiementDto.getCommande();

        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> {
                    logger.error("{} Commande non trouvée avec l'ID: {}", context, commandeId);
                    return new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
                });

        // Vérifier que la commande n'est pas déjà payée
        if (paiementRepository.existsByCommandeId(commandeId)) {
            logger.warn("{} Tentative de paiement d'une commande déjà payée. Commande ID: {}", context, commandeId);
            throw new RuntimeException("Cette commande a déjà été payée");
        }

        // Définir la date de paiement si non fournie
        if (paiementDto.getDatePaiement() == null) {
            paiementDto.setDatePaiement(Timestamp.valueOf(LocalDateTime.now()));
        }

        // Générer une référence de transaction si non fournie
        if (paiementDto.getReferenceTransaction() == null || paiementDto.getReferenceTransaction().isEmpty()) {
            paiementDto.setReferenceTransaction(generateReference());
        }

        Paiement paiement = paiementMapper.toEntity(paiementDto);
        paiement.setCommande(commande);

        // Sauvegarder le paiement
        Paiement saved = paiementRepository.save(paiement);
        logger.info("{} Paiement sauvegardé avec succès. ID: {}, Référence: {}", context, saved.getId(), saved.getReferenceTransaction());

        // Mettre à jour le statut de la commande à PAYEE
        commande.setStatut(StatutCommande.PAYEE);
        commandeRepository.save(commande);
        logger.info("{} Statut de la commande ID: {} mis à jour à PAYEE", context, commandeId);

        return paiementMapper.toDto(saved);
    }

    @Override
    public List<PaiementDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les paiements", context);
        List<PaiementDto> paiements = paiementRepository.findAll()
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} paiements récupérés avec succès", context, paiements.size());
        return paiements;
    }

    @Override
    public PaiementDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération du paiement avec l'ID: {}", context, id);
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Paiement non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Paiement non trouvé avec l'ID : " + id);
                });
        logger.info("{} Paiement ID: {} récupéré avec succès - Montant: {}", context, id, paiement.getMontant());
        return paiementMapper.toDto(paiement);
    }

    @Override
    public PaiementDto update(Long id, PaiementDto paiementDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour du paiement ID: {}", context, id);
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Paiement non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Paiement non trouvé avec l'ID : " + id);
                });

        // Mise à jour du montant
        if (paiementDto.getMontant() != null) {
            if (paiementDto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Le montant doit être supérieur à 0");
            }
            paiement.setMontant(paiementDto.getMontant());
        }

        // Mise à jour du type de paiement
        if (paiementDto.getTypePaiement() != null) {
            paiement.setTypePaiement(paiementDto.getTypePaiement());
        }

        // Mise à jour de la référence
        if (paiementDto.getReferenceTransaction() != null && !paiementDto.getReferenceTransaction().isEmpty()) {
            paiement.setReferenceTransaction(paiementDto.getReferenceTransaction());
        }

        // Mise à jour de la date (normalement non modifiable)
        if (paiementDto.getDatePaiement() != null) {
            paiement.setDatePaiement(paiementDto.getDatePaiement());
        }

        // Sauvegarde
        Paiement updated = paiementRepository.save(paiement);
        logger.info("{} Paiement ID: {} mis à jour avec succès", context, id);
        return paiementMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression du paiement ID: {}", context, id);
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Paiement non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Paiement non trouvé avec l'ID : " + id);
                });

        // Remettre le statut de la commande à EN_ATTENTE
        Commande commande = paiement.getCommande();
        if (commande != null) {
            commande.setStatut(StatutCommande.EN_ATTENTE);
            commandeRepository.save(commande);
            logger.info("{} Statut de la commande ID: {} remis à EN_ATTENTE", context, commande.getId());
        }

        paiementRepository.delete(paiement);
        logger.info("{} Paiement ID: {} supprimé avec succès", context, id);
    }

    @Override
    public List<PaiementDto> findByCommandeId(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des paiements pour la commande ID: {}", context, commandeId);
        // Vérifier que la commande existe
        if (!commandeRepository.existsById(commandeId)) {
            logger.error("{} Commande non trouvée avec l'ID: {}", context, commandeId);
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        List<PaiementDto> paiements = paiementRepository.findByCommandeId(commandeId)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} paiements récupérés pour la commande ID: {}", context, paiements.size(), commandeId);
        return paiements;
    }

    @Override
    public List<PaiementDto> findByTypePaiement(TypePaiement typePaiement) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des paiements par type: {}", context, typePaiement);
        List<PaiementDto> paiements = paiementRepository.findByTypePaiement(typePaiement)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} paiements de type {} récupérés avec succès", context, paiements.size(), typePaiement);
        return paiements;
    }

    @Override
    public List<PaiementDto> findByDateRange(Timestamp debut, Timestamp fin) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des paiements entre {} et {}", context, debut, fin);
        if (debut.after(fin)) {
            logger.error("{} Date de début postérieure à la date de fin", context);
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        List<PaiementDto> paiements = paiementRepository.findByDatePaiementBetween(debut, fin)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} paiements récupérés pour la période spécifiée", context, paiements.size());
        return paiements;
    }

    @Override
    public PaiementDto findByReference(String referenceTransaction) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Recherche du paiement par référence: {}", context, referenceTransaction);
        if (referenceTransaction == null || referenceTransaction.isEmpty()) {
            logger.error("{} Référence de transaction vide ou nulle", context);
            throw new RuntimeException("La référence de transaction est obligatoire");
        }

        Paiement paiement = paiementRepository.findByReferenceTransaction(referenceTransaction)
                .orElseThrow(() -> {
                    logger.error("{} Aucun paiement trouvé avec la référence: {}", context, referenceTransaction);
                    return new RuntimeException("Aucun paiement trouvé avec la référence : " + referenceTransaction);
                });

        logger.info("{} Paiement trouvé avec la référence {} - ID: {}", context, referenceTransaction, paiement.getId());
        return paiementMapper.toDto(paiement);
    }

    @Override
    public PaiementDto effectuerPaiement(Long commandeId, BigDecimal montant, TypePaiement typePaiement) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative d'effectuer un paiement - Commande ID: {}, Montant: {}, Type: {}", context, commandeId, montant, typePaiement);
        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> {
                    logger.error("{} Commande non trouvée avec l'ID: {}", context, commandeId);
                    return new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
                });

        // Vérifier que la commande n'est pas déjà payée
        if (paiementRepository.existsByCommandeId(commandeId)) {
            logger.warn("{} Tentative de paiement d'une commande déjà payée. Commande ID: {}", context, commandeId);
            throw new RuntimeException("Cette commande a déjà été payée");
        }

        // Vérifier le montant
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("{} Montant invalide: {}", context, montant);
            throw new RuntimeException("Le montant doit être supérieur à 0");
        }

        // Créer le paiement
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMontant(montant);
        paiement.setTypePaiement(typePaiement);
        paiement.setDatePaiement(Timestamp.valueOf(LocalDateTime.now()));
        paiement.setReferenceTransaction(generateReference());

        // Sauvegarder le paiement
        Paiement saved = paiementRepository.save(paiement);

        // Mettre à jour le statut de la commande
        commande.setStatut(StatutCommande.PAYEE);
        commandeRepository.save(commande);
        logger.info("{} Paiement effectué avec succès. ID: {}, Référence: {}, Statut de la commande ID: {} mis à PAYEE", 
                    context, saved.getId(), saved.getReferenceTransaction(), commandeId);

        return paiementMapper.toDto(saved);
    }

    @Override
    public BigDecimal calculateTotalByPeriod(Timestamp debut, Timestamp fin) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Calcul du total des paiements pour la période {} à {}", context, debut, fin);
        if (debut.after(fin)) {
            logger.error("{} Date de début postérieure à la date de fin", context);
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        BigDecimal total = paiementRepository.calculateTotalByPeriod(debut, fin);
        BigDecimal result = total != null ? total : BigDecimal.ZERO;
        logger.info("{} Total calculé pour la période: {}", context, result);
        return result;
    }

    @Override
    public BigDecimal calculateTotalByTypeAndPeriod(TypePaiement type, Timestamp debut, Timestamp fin) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Calcul du total des paiements pour le type {} entre {} et {}", context, type, debut, fin);
        if (debut.after(fin)) {
            logger.error("{} Date de début postérieure à la date de fin", context);
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        BigDecimal total = paiementRepository.calculateTotalByTypeAndPeriod(type, debut, fin);
        BigDecimal result = total != null ? total : BigDecimal.ZERO;
        logger.info("{} Total calculé pour le type {}: {}", context, type, result);
        return result;
    }

    @Override
    public List<PaiementDto> getPaiementsAujourdhui() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération des paiements du jour", context);
        List<PaiementDto> paiements = paiementRepository.findPaiementsAujourdhui()
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} paiements du jour récupérés avec succès", context, paiements.size());
        return paiements;
    }

    @Override
    public boolean commandeEstPayee(Long commandeId) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Vérification du statut de paiement pour la commande ID: {}", context, commandeId);
        boolean estPayee = paiementRepository.existsByCommandeId(commandeId);
        logger.info("{} Statut de paiement vérifié pour la commande ID: {} - Payée: {}", context, commandeId, estPayee);
        return estPayee;
    }

    /**
     * Générer une référence de transaction unique
     */
    private String generateReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
