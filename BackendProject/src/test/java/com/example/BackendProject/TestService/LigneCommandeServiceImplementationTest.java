package com.example.BackendProject.TestService;

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
import com.example.BackendProject.services.implementations.LigneCommandeServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Ligne de Commande")
class LigneCommandeServiceImplementationTest {

    @Mock private LigneCommandeRepository ligneCommandeRepository;
    @Mock private CommandeRepository commandeRepository;
    @Mock private PlatRepository platRepository;
    @Mock private LigneCommandeMapper ligneCommandeMapper;
    @Mock private CommandeMapper commandeMapper;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LigneCommandeServiceImplementation service;

    private Commande commande;
    private Plat plat;
    private LigneCommande ligneCommande;
    private LigneCommandeDto ligneDto;

    @BeforeEach
    void setUp() {
        // Mock du RestaurantDto (TVA à 0.1925 pour le Cameroun)
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setTauxTva(BigDecimal.valueOf(0.1925));
        ReflectionTestUtils.setField(service, "restaurantDto", restaurantDto);

        plat = new Plat();
        plat.setId(1L);
        plat.setNom("NDOLE");
        plat.setPrix(new BigDecimal("5000"));
        plat.setDisponibilite(true);

        commande = new Commande();
        commande.setId(10L);
        commande.setLignes(new ArrayList<>());

        ligneCommande = new LigneCommande();
        ligneCommande.setId(100L);
        ligneCommande.setCommande(commande);
        ligneCommande.setPlat(plat);
        ligneCommande.setQuantite(2);
        ligneCommande.setPrixUnitaire(new BigDecimal("5000"));

        ligneDto = new LigneCommandeDto();
        ligneDto.setCommande(10L);
        ligneDto.setPlat(1L);
        ligneDto.setQuantite(2);
    }

    @Test
    @DisplayName("Save - Succès et Recalcul des totaux")
    void save_ShouldCalculateTotalsAndNotify() {
        // Configuration
        commande.getLignes().add(ligneCommande); // Simule que la ligne est ajoutée

        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(platRepository.findById(1L)).thenReturn(Optional.of(plat));
        when(ligneCommandeMapper.toEntity(any())).thenReturn(ligneCommande);
        when(ligneCommandeRepository.save(any())).thenReturn(ligneCommande);
        when(ligneCommandeMapper.toDto(any())).thenReturn(ligneDto);

        // Exécution
        service.save(ligneDto);

        // Vérification des calculs (5000 * 2 = 10000 HT)
        assertTrue(new BigDecimal("10000").compareTo(commande.getTotalHt()) == 0);
        // TTC = 10000 + (10000 * 0.1925) = 11925
        assertTrue(new BigDecimal("11925.0").compareTo(commande.getTotalTtc()) == 0);

        // Vérification WebSocket
        verify(messagingTemplate).convertAndSend(eq("/topic/serveurs/addition/10"), (Object) any());
    }

    @Test
    @DisplayName("Save - Échec si le plat n'est pas disponible")
    void save_ShouldThrow_WhenPlatNotAvailable() {
        plat.setDisponibilite(false);
        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(platRepository.findById(1L)).thenReturn(Optional.of(plat));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.save(ligneDto));
        assertTrue(ex.getMessage().contains("pas disponible"));
    }

    @Test
    @DisplayName("UpdateQuantite - Succès et Recalcul")
    void updateQuantite_ShouldUpdateAndRecalculate() {
        commande.getLignes().add(ligneCommande);
        when(ligneCommandeRepository.findById(100L)).thenReturn(Optional.of(ligneCommande));
        when(commandeRepository.findById(10L)).thenReturn(Optional.of(commande));
        when(ligneCommandeRepository.save(any())).thenReturn(ligneCommande);

        service.updateQuantite(100L, 3); // On passe de 2 à 3

        assertEquals(3, ligneCommande.getQuantite());
        // Total HT doit être 3 * 5000 = 15000
        assertTrue(new BigDecimal("15000").compareTo(commande.getTotalHt()) == 0);
        verify(commandeRepository).save(commande);
    }

    @Test
    @DisplayName("Delete - Échec si ligne inexistante")
    void delete_ShouldThrow_WhenNotFound() {
        when(ligneCommandeRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.delete(999L));
    }

    @Test
    @DisplayName("CalculateTotalCommande - Utilisation du repository")
    void calculateTotalCommande_ShouldReturnSum() {
        when(commandeRepository.existsById(10L)).thenReturn(true);
        when(ligneCommandeRepository.calculateTotalCommande(10L)).thenReturn(new BigDecimal("25000"));

        BigDecimal total = service.calculateTotalCommande(10L);

        assertEquals(0, new BigDecimal("25000").compareTo(total));
    }
}
