package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.CommandePromotionDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.CommandePromotion;
import com.example.BackendProject.entities.CommandePromotionPK;
import com.example.BackendProject.entities.Promotion;
import com.example.BackendProject.mappers.CommandePromotionMapper;
import com.example.BackendProject.repository.CommandePromotionRepository;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.PromotionRepository;
import com.example.BackendProject.services.implementations.CommandePromotionServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - CommandePromotion")
class CommandePromotionServiceImplementationTest {

    @Mock
    private CommandePromotionRepository commandePromotionRepository;
    @Mock
    private CommandeRepository commandeRepository;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private CommandePromotionMapper commandePromotionMapper;

    @InjectMocks
    private CommandePromotionServiceImplementation service;

    private Commande commande;
    private Promotion promotion;
    private CommandePromotionDto dto;

    @BeforeEach
    void setUp() {
        commande = new Commande();
        commande.setId(10L);

        promotion = new Promotion();
        promotion.setId(20L);
        promotion.setActif(true);
        // Date d'expiration dans le futur pour les tests passants
        promotion.setDateExpiration(new java.sql.Date(System.currentTimeMillis() + 1000000));

        dto = new CommandePromotionDto();
        dto.setCommande(10L);
        dto.setPromotion(20L);
    }

    // ==================== TESTS DE VALIDATION (SAVE) ====================

    @Test
    @DisplayName("Save - Succès de l'application d'une promotion")
    void save_ShouldSucceed_WhenValid() {
        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(promotionRepository.findById(20L)).thenReturn(Optional.of(promotion));
        when(commandePromotionRepository.existsByCommandeIdAndPromotionId(10L, 20L)).thenReturn(false);
        when(commandePromotionRepository.save(any(CommandePromotion.class))).thenReturn(new CommandePromotion());
        when(commandePromotionMapper.toDto(any())).thenReturn(dto);

        CommandePromotionDto result = service.save(dto);

        assertNotNull(result);
        verify(commandePromotionRepository).save(any(CommandePromotion.class));
    }

    @Test
    @DisplayName("Save - Échec si la promotion est inactive")
    void save_ShouldThrow_WhenPromotionInactive() {
        promotion.setActif(false);
        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(promotionRepository.findById(20L)).thenReturn(Optional.of(promotion));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.save(dto));
        assertEquals("Cette promotion n'est pas active", ex.getMessage());
    }

    @Test
    @DisplayName("Save - Échec si la promotion est expirée")
    void save_ShouldThrow_WhenPromotionExpired() {
        // Date dans le passé
        promotion.setDateExpiration(new java.sql.Date(System.currentTimeMillis() - 1000000));;
        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(promotionRepository.findById(20L)).thenReturn(Optional.of(promotion));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.save(dto));
        assertEquals("Cette promotion a expiré", ex.getMessage());
    }

    @Test
    @DisplayName("Save - Échec si déjà appliquée")
    void save_ShouldThrow_WhenAlreadyApplied() {
        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(promotionRepository.findById(20L)).thenReturn(Optional.of(promotion));
        when(commandePromotionRepository.existsByCommandeIdAndPromotionId(10L, 20L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.save(dto));
    }

    // ==================== TESTS DE RECHERCHE & SUPPRESSION ====================

    @Test
    @DisplayName("GetById - Succès avec clé composée")
    void getById_ShouldReturnDto_WhenExists() {
        CommandePromotionPK pk = new CommandePromotionPK(10L, 20L);
        CommandePromotion cp = new CommandePromotion();

        when(commandePromotionRepository.findById(pk)).thenReturn(Optional.of(cp));
        when(commandePromotionMapper.toDto(cp)).thenReturn(dto);

        CommandePromotionDto result = service.getById(10L, 20L);

        assertEquals(10L, result.getCommande());
        verify(commandePromotionRepository).findById(pk);
    }

    @Test
    @DisplayName("Delete - Succès")
    void delete_ShouldCallRepository_WhenExists() {
        CommandePromotionPK pk = new CommandePromotionPK(10L, 20L);
        when(commandePromotionRepository.existsById(pk)).thenReturn(true);

        service.delete(10L, 20L);

        verify(commandePromotionRepository).deleteById(pk);
    }

    @Test
    @DisplayName("RetirerToutesPromotions - Succès")
    void retirerToutesPromotions_ShouldVerifyAndCallDelete() {
        when(commandeRepository.existsById(10L)).thenReturn(true);

        service.retirerToutesPromotions(10L);

        verify(commandePromotionRepository).deleteByCommandeId(10L);
    }
}
