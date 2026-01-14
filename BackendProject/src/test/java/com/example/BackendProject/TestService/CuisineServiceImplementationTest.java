package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.services.implementations.CuisineServiceImplementation;
import com.example.BackendProject.utils.StatutCommande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - Cuisine Service")
class CuisineServiceImplementationTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private CommandeMapper commandeMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private CuisineServiceImplementation cuisineService;

    private Commande sampleCommande;
    private CommandeDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleCommande = new Commande();
        sampleCommande.setId(1L);
        sampleCommande.setStatut(StatutCommande.EN_ATTENTE);

        sampleDto = new CommandeDto();
        sampleDto.setId(1L);
        sampleDto.setStatut(StatutCommande.valueOf(StatutCommande.EN_ATTENTE.name()));
    }

    @Test
    @DisplayName("Get Liste à Préparer - Devrait retourner les commandes en attente ou préparation")
    void getListeAPreparer_ShouldReturnList() {
        // Arrange
        List<Commande> commandes = Arrays.asList(sampleCommande);
        when(commandeRepository.findByStatutInOrderByDateHeureCommandeAsc(anyList())).thenReturn(commandes);
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(sampleDto);

        // Act
        List<CommandeDto> result = cuisineService.getListeAPreparer();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commandeRepository).findByStatutInOrderByDateHeureCommandeAsc(anyList());
    }

    @Test
    @DisplayName("Commencer Préparation - Succès (EN_ATTENTE -> EN_PREPARATION)")
    void commencerPreparation_Success() {
        // Arrange
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(sampleCommande));
        when(commandeRepository.save(any(Commande.class))).thenReturn(sampleCommande);
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(sampleDto);

        // Act
        CommandeDto result = cuisineService.commencerPreparation(1L);

        // Assert
        assertEquals(StatutCommande.EN_PREPARATION, sampleCommande.getStatut());
        verify(commandeRepository).save(sampleCommande);
    }

    @Test
    @DisplayName("Commencer Préparation - Échec si statut n'est pas EN_ATTENTE")
    void commencerPreparation_InvalidStatus() {
        // Arrange
        sampleCommande.setStatut(StatutCommande.PRETE);
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(sampleCommande));
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(sampleDto);

        // Act
        cuisineService.commencerPreparation(1L);

        // Assert
        assertEquals(StatutCommande.PRETE, sampleCommande.getStatut()); // N'a pas changé
        verify(commandeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Marquer comme Prête - Succès et Notification WebSocket")
    void marquerCommePrete_Success() {
        // Arrange
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(sampleCommande));
        when(commandeRepository.save(any(Commande.class))).thenReturn(sampleCommande);
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(sampleDto);

        // Act
        CommandeDto result = cuisineService.marquerCommePrete(1L);

        // Assert
        assertEquals(StatutCommande.PRETE, sampleCommande.getStatut());
        verify(commandeRepository).save(sampleCommande);

        // Vérifie l'envoi du message WebSocket à la salle
        verify(messagingTemplate).convertAndSend(eq("/topic/salle/prete"), eq(sampleDto));
    }

    @Test
    @DisplayName("Marquer comme Prête - Erreur si Commande inexistante")
    void marquerCommePrete_NotFound() {
        // Arrange
        when(commandeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cuisineService.marquerCommePrete(1L));
    }
}