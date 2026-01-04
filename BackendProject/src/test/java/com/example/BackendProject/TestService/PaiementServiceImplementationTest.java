package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.PaiementDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Paiement;
import com.example.BackendProject.mappers.PaiementMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.PaiementRepository;
import com.example.BackendProject.services.implementations.PaiementServiceImplementation;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypePaiement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Paiement (Gestion Financière)")
class PaiementServiceImplementationTest {

    @Mock
    private PaiementRepository paiementRepository;
    @Mock
    private CommandeRepository commandeRepository;
    @Mock
    private PaiementMapper paiementMapper;

    @InjectMocks
    private PaiementServiceImplementation service;

    private Commande commande;
    private Paiement paiement;
    private PaiementDto paiementDto;

    @BeforeEach
    void setUp() {
        commande = new Commande();
        commande.setId(50L);
        commande.setStatut(StatutCommande.EN_ATTENTE);

        paiement = new Paiement();
        paiement.setId(100L);
        paiement.setCommande(commande);
        paiement.setMontant(new BigDecimal("15000"));
        paiement.setTypePaiement(TypePaiement.Espèces);

        paiementDto = new PaiementDto();
        paiementDto.setCommande(50L);
        paiementDto.setMontant(new BigDecimal("15000"));
        paiementDto.setTypePaiement(TypePaiement.Espèces);
    }

    // ==================== TESTS DE SAUVEGARDE & LOGIQUE MÉTIER ====================

    @Test
    @DisplayName("Save - Succès et mise à jour du statut commande")
    void save_ShouldSucceed_AndSetCommandeToPayee() {
        // Arrange
        when(commandeRepository.findById(50L)).thenReturn(Optional.of(commande));
        when(paiementRepository.existsByCommandeId(50L)).thenReturn(false);
        when(paiementMapper.toEntity(any())).thenReturn(paiement);
        when(paiementRepository.save(any())).thenReturn(paiement);
        when(paiementMapper.toDto(any())).thenReturn(paiementDto);

        // Act
        PaiementDto result = service.save(paiementDto);

        // Assert
        assertNotNull(result);
        assertEquals(StatutCommande.PAYEE, commande.getStatut()); // Vérification cruciale
        assertNotNull(paiementDto.getReferenceTransaction()); // Vérifie la génération automatique
        verify(commandeRepository).save(commande);
        verify(paiementRepository).save(any(Paiement.class));
    }

    @Test
    @DisplayName("Save - Échec si déjà payé")
    void save_ShouldThrow_WhenAlreadyPaid() {
        when(commandeRepository.findById(50L)).thenReturn(Optional.of(commande));
        when(paiementRepository.existsByCommandeId(50L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.save(paiementDto));
        assertEquals("Cette commande a déjà été payée", ex.getMessage());
    }

    @Test
    @DisplayName("Save - Échec si montant invalide")
    void save_ShouldThrow_WhenMontantIsNegative() {
        paiementDto.setMontant(new BigDecimal("-10"));
        assertThrows(RuntimeException.class, () -> service.save(paiementDto));
    }

    // ==================== TESTS DE SUPPRESSION ====================

    @Test
    @DisplayName("Delete - Succès et remise à EN_ATTENTE de la commande")
    void delete_ShouldSucceed_AndResetCommandeStatus() {
        // Arrange
        when(paiementRepository.findById(100L)).thenReturn(Optional.of(paiement));

        // Act
        service.delete(100L);

        // Assert
        assertEquals(StatutCommande.EN_ATTENTE, commande.getStatut());
        verify(commandeRepository).save(commande);
        verify(paiementRepository).delete(paiement);
    }

    // ==================== TESTS DE CALCULS FINANCIERS ====================

    @Test
    @DisplayName("CalculateTotalByPeriod - Succès")
    void calculateTotalByPeriod_ShouldReturnSum() {
        java.sql.Timestamp debut = java.sql.Timestamp.valueOf("2023-01-01 00:00:00");
        java.sql.Timestamp fin = java.sql.Timestamp.valueOf("2023-12-31 23:59:59");

        when(paiementRepository.calculateTotalByPeriod(debut, fin)).thenReturn(new BigDecimal("50000"));

        BigDecimal total = service.calculateTotalByPeriod(debut, fin);

        // Comparaison BigDecimal avec 0 pour égalité de valeur
        assertTrue(new BigDecimal("50000").compareTo(total) == 0);
    }

    @Test
    @DisplayName("EffectuerPaiement - Test de la méthode simplifiée")
    void effectuerPaiement_ShouldCreateProperTransaction() {
        when(commandeRepository.findById(50L)).thenReturn(Optional.of(commande));
        when(paiementRepository.existsByCommandeId(50L)).thenReturn(false);
        when(paiementRepository.save(any())).thenReturn(paiement);
        when(paiementMapper.toDto(any())).thenReturn(paiementDto);

        service.effectuerPaiement(50L, new BigDecimal("1000"), TypePaiement.Carte);

        verify(paiementRepository).save(argThat(p ->
                p.getTypePaiement() == TypePaiement.Carte &&
                        p.getCommande().getId().equals(50L)
        ));
    }
}
