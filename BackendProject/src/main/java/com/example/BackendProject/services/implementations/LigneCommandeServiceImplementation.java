package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.dto.LigneCommandeDto;
import com.example.BackendProject.dto.RestaurantDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.mappers.LigneCommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.LigneCommandeRepository;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.services.interfaces.LigneCommandeServiceInterface;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LigneCommandeServiceImplementation implements LigneCommandeServiceInterface {

    private final LigneCommandeMapper ligneCommandeMapper;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final CommandeRepository commandeRepository;
    private final PlatRepository platRepository;
    private final CommandeMapper commandeMapper; // Ajouté
    private final SimpMessagingTemplate messagingTemplate; // Ajouté
    private RestaurantDto restaurantDto;

    public LigneCommandeServiceImplementation(LigneCommandeMapper ligneCommandeMapper,
                                              LigneCommandeRepository ligneCommandeRepository,
                                              CommandeRepository commandeRepository,
                                              PlatRepository platRepository,
                                              CommandeMapper commandeMapper,
                                              SimpMessagingTemplate messagingTemplate) {
        this.ligneCommandeMapper = ligneCommandeMapper;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.commandeRepository = commandeRepository;
        this.platRepository = platRepository;
        this.commandeMapper = commandeMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public LigneCommandeDto save(LigneCommandeDto ligneCommandeDto) {
        // Validation des champs obligatoires
        if (ligneCommandeDto.getCommande() == null || ligneCommandeDto.getCommande() == null) {
            throw new RuntimeException("La commande est obligatoire");
        }

        if (ligneCommandeDto.getPlat() == null || ligneCommandeDto.getPlat() == null) {
            throw new RuntimeException("Le plat est obligatoire");
        }

        if (ligneCommandeDto.getQuantite() == null || ligneCommandeDto.getQuantite() <= 0) {
            throw new RuntimeException("La quantité doit être supérieure à 0");
        }

        Long commandeId = ligneCommandeDto.getCommande();
        Long platId = ligneCommandeDto.getPlat();

        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + commandeId));

        // Vérifier que le plat existe
        Plat plat = platRepository.findById(platId)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé avec l'ID : " + platId));

        // Vérifier que le plat est disponible
        if (!plat.getDisponibilite()) {
            throw new RuntimeException("Le plat '" + plat.getNom() + "' n'est pas disponible actuellement");
        }

        // Définir le prix unitaire si non fourni
        if (ligneCommandeDto.getPrixUnitaire() == null) {
            ligneCommandeDto.setPrixUnitaire(plat.getPrix());
        }

        LigneCommande ligneCommande = ligneCommandeMapper.toEntity(ligneCommandeDto);
        ligneCommande.setCommande(commande);
        ligneCommande.setPlat(plat);

        LigneCommande saved = ligneCommandeRepository.save(ligneCommande);

        // RECALCUL ICI
        mettreAJourEtNotifierAddition(commandeId);

        return ligneCommandeMapper.toDto(saved);
    }

    @Override
    public List<LigneCommandeDto> getAll() {
        return ligneCommandeRepository.findAll()
                .stream()
                .map(ligneCommandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LigneCommandeDto getById(Long id) {
        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id));

        return ligneCommandeMapper.toDto(ligneCommande);
    }

    @Override
    public LigneCommandeDto update(Long id, LigneCommandeDto ligneCommandeDto) {
        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id));

        // Mise à jour de la quantité
        if (ligneCommandeDto.getQuantite() != null) {
            if (ligneCommandeDto.getQuantite() <= 0) {
                throw new RuntimeException("La quantité doit être supérieure à 0");
            }
            ligneCommande.setQuantite(ligneCommandeDto.getQuantite());
        }

        // Mise à jour du prix unitaire
        if (ligneCommandeDto.getPrixUnitaire() != null) {
            if (ligneCommandeDto.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Le prix unitaire doit être supérieur à 0");
            }
            ligneCommande.setPrixUnitaire(ligneCommandeDto.getPrixUnitaire());
        }

        // Mise à jour des notes
        if (ligneCommandeDto.getNotesCuisine() != null) {
            ligneCommande.setNotesCuisine(ligneCommandeDto.getNotesCuisine());
        }

        // Mise à jour du plat (si nécessaire)
        if (ligneCommandeDto.getPlat() != null && ligneCommandeDto.getPlat() != null) {
            Plat plat = platRepository.findById(ligneCommandeDto.getPlat())
                    .orElseThrow(() -> new RuntimeException("Plat non trouvé avec l'ID : " + ligneCommandeDto.getPlat()));

            if (!plat.getDisponibilite()) {
                throw new RuntimeException("Le plat '" + plat.getNom() + "' n'est pas disponible actuellement");
            }

            ligneCommande.setPlat(plat);
        }

        // Sauvegarde
        LigneCommande updated = ligneCommandeRepository.save(ligneCommande);

        // RECALCUL ICI
        mettreAJourEtNotifierAddition(updated.getCommande().getId());

        return ligneCommandeMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id));

        ligneCommandeRepository.delete(ligneCommande);

        // RECALCUL ICI (Après la suppression)
        mettreAJourEtNotifierAddition(id);
    }

    @Override
    public List<LigneCommandeDto> findByCommandeId(Long commandeId) {
        // Vérifier que la commande existe
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        return ligneCommandeRepository.findByCommandeId(commandeId)
                .stream()
                .map(ligneCommandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneCommandeDto> findByPlatId(Long platId) {
        // Vérifier que le plat existe
        if (!platRepository.existsById(platId)) {
            throw new RuntimeException("Plat non trouvé avec l'ID : " + platId);
        }

        return ligneCommandeRepository.findByPlatId(platId)
                .stream()
                .map(ligneCommandeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LigneCommandeDto ajouterLigneCommande(Long commandeId, Long platId, Integer quantite, String notes) {
        // Vérifier que la commande existe
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + commandeId));

        // Vérifier que le plat existe
        Plat plat = platRepository.findById(platId)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé avec l'ID : " + platId));

        // Vérifier que le plat est disponible
        if (!plat.getDisponibilite()) {
            throw new RuntimeException("Le plat '" + plat.getNom() + "' n'est pas disponible actuellement");
        }

        // Vérifier la quantité
        if (quantite == null || quantite <= 0) {
            throw new RuntimeException("La quantité doit être supérieure à 0");
        }

        // Créer la ligne de commande
        LigneCommande ligneCommande = new LigneCommande();
        ligneCommande.setCommande(commande);
        ligneCommande.setPlat(plat);
        ligneCommande.setQuantite(quantite);
        ligneCommande.setPrixUnitaire(plat.getPrix());
        ligneCommande.setNotesCuisine(notes);

        LigneCommande saved = ligneCommandeRepository.save(ligneCommande);

        return ligneCommandeMapper.toDto(saved);
    }

    @Override
    public LigneCommandeDto updateQuantite(Long id, Integer nouvelleQuantite) {
        if (nouvelleQuantite == null || nouvelleQuantite <= 0) {
            throw new RuntimeException("La quantité doit être supérieure à 0");
        }

        LigneCommande ligneCommande = ligneCommandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne de commande non trouvée avec l'ID : " + id));

        ligneCommande.setQuantite(nouvelleQuantite);

        LigneCommande updated = ligneCommandeRepository.save(ligneCommande);

        // RECALCUL ICI
        mettreAJourEtNotifierAddition(updated.getCommande().getId());

        return ligneCommandeMapper.toDto(updated);
    }

    @Override
    public BigDecimal calculateTotalCommande(Long commandeId) {
        // Vérifier que la commande existe
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        BigDecimal total = ligneCommandeRepository.calculateTotalCommande(commandeId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public void supprimerToutesLignesCommande(Long commandeId) {
        // Vérifier que la commande existe
        if (!commandeRepository.existsById(commandeId)) {
            throw new RuntimeException("Commande non trouvée avec l'ID : " + commandeId);
        }

        ligneCommandeRepository.deleteByCommandeId(commandeId);
    }

    private void mettreAJourEtNotifierAddition(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        // Calcul du Total HT (Somme des lignes)
        BigDecimal totalHt = commande.getLignes().stream()
                .map(l -> l.getPrixUnitaire().multiply(new BigDecimal(l.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcul TTC (TVA 19.25% Cameroun)
        BigDecimal tva = totalHt.multiply(new BigDecimal(String.valueOf(restaurantDto.getTauxTva())));
        BigDecimal totalTtc = totalHt.add(tva);

        commande.setTotalHt(totalHt);
        commande.setTotalTtc(totalTtc);

        commandeRepository.save(commande);

        // Notification Temps Réel au serveur et à la caisse
        CommandeDto commandeDto = commandeMapper.toDto(commande);
        messagingTemplate.convertAndSend("/topic/serveurs/addition/" + commandeId, commandeDto);
    }
}
