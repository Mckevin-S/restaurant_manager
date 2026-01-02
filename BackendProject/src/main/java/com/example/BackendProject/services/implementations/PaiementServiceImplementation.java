package com.example.BackendProject.services.implementations;


import com.example.BackendProject.dto.PaiementDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Paiement;
import com.example.BackendProject.mappers.PaiementMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.PaiementRepository;
import com.example.BackendProject.services.interfaces.PaiementServiceInterface;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypePaiement;
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
        // Validation des champs obligatoires
        if (paiementDto.getCommande() == null || paiementDto.getCommande() == null) {
            throw new RuntimeException("La commande est obligatoire");
        }

        if (paiementDto.getMontant() == null || paiementDto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le montant doit être supérieur à 0");
        }

        if (paiementDto.getTypePaiement() == null) {
            throw new RuntimeException("Le type de paiement est obligatoire");
        }

        Long commandeId = paiementDto.getCommande();

        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + commandeId));

        // Vérifier que la commande n'est pas déjà payée
        if (paiementRepository.existsByCommandeId(commandeId)) {
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

        // Mettre à jour le statut de la commande à PAYEE
        commande.setStatut(StatutCommande.PAYEE);
        commandeRepository.save(commande);

        return paiementMapper.toDto(saved);
    }

    @Override
    public List<PaiementDto> getAll() {
        return paiementRepository.findAll()
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaiementDto getById(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + id));

        return paiementMapper.toDto(paiement);
    }

    @Override
    public PaiementDto update(Long id, PaiementDto paiementDto) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + id));

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

        return paiementMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + id));

        // Remettre le statut de la commande à EN_ATTENTE
        Commande commande = paiement.getCommande();
        if (commande != null) {
            commande.setStatut(StatutCommande.EN_ATTENTE);
            commandeRepository.save(commande);
        }

        paiementRepository.delete(paiement);
    }

    @Override
    public List<PaiementDto> findByCommandeId(Long commandeId) {
        // Vérifier que la commande existe
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        return paiementRepository.findByCommandeId(commandeId)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaiementDto> findByTypePaiement(TypePaiement typePaiement) {
        return paiementRepository.findByTypePaiement(typePaiement)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaiementDto> findByDateRange(Timestamp debut, Timestamp fin) {
        if (debut.after(fin)) {
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        return paiementRepository.findByDatePaiementBetween(debut, fin)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaiementDto findByReference(String referenceTransaction) {
        if (referenceTransaction == null || referenceTransaction.isEmpty()) {
            throw new RuntimeException("La référence de transaction est obligatoire");
        }

        Paiement paiement = paiementRepository.findByReferenceTransaction(referenceTransaction)
                .orElseThrow(() -> new RuntimeException("Aucun paiement trouvé avec la référence : " + referenceTransaction));

        return paiementMapper.toDto(paiement);
    }

    @Override
    public PaiementDto effectuerPaiement(Long commandeId, BigDecimal montant, TypePaiement typePaiement) {
        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + commandeId));

        // Vérifier que la commande n'est pas déjà payée
        if (paiementRepository.existsByCommandeId(commandeId)) {
            throw new RuntimeException("Cette commande a déjà été payée");
        }

        // Vérifier le montant
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
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

        return paiementMapper.toDto(saved);
    }

    @Override
    public BigDecimal calculateTotalByPeriod(Timestamp debut, Timestamp fin) {
        if (debut.after(fin)) {
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        BigDecimal total = paiementRepository.calculateTotalByPeriod(debut, fin);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalByTypeAndPeriod(TypePaiement type, Timestamp debut, Timestamp fin) {
        if (debut.after(fin)) {
            throw new RuntimeException("La date de début doit être antérieure à la date de fin");
        }

        BigDecimal total = paiementRepository.calculateTotalByTypeAndPeriod(type, debut, fin);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<PaiementDto> getPaiementsAujourdhui() {
        return paiementRepository.findPaiementsAujourdhui()
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean commandeEstPayee(Long commandeId) {
        return paiementRepository.existsByCommandeId(commandeId);
    }

    /**
     * Générer une référence de transaction unique
     */
    private String generateReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
