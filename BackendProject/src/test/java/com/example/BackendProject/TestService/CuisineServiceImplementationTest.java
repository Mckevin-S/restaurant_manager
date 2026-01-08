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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Cuisine (Gestion de la préparation)")
class CuisineServiceImplementationTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private CommandeMapper commandeMapper;

    @InjectMocks
    private CuisineServiceImplementation cuisineService;

    private Commande commandeAttente;
    private CommandeDto commandeDto;

    @BeforeEach
    void setUp() {
        commandeAttente = new Commande();
        commandeAttente.setId(1L);
        commandeAttente.setStatut(StatutCommande.EN_ATTENTE);

        commandeDto = new CommandeDto();
        commandeDto.setId(1L);
    }

    // ==================== TEST LISTE CUISINE ====================

    @Test
    @DisplayName("Cuisine - Récupérer les commandes à préparer (Attente + En préparation)")
    void getListeAPreparer_ShouldReturnFilteredCommandes() {
        // Liste simulée contenant une commande en attente et une en préparation
        List<Commande> commandes = Arrays.asList(commandeAttente, new Commande());

        when(commandeRepository.findByStatutInOrderByDateHeureCommandeAsc(
                Arrays.asList(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION)
        )).thenReturn(commandes);

        when(commandeMapper.toDto(any(Commande.class))).thenReturn(new CommandeDto());

        List<CommandeDto> result = cuisineService.getListeAPreparer();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commandeRepository).findByStatutInOrderByDateHeureCommandeAsc(any());
    }

    // ==================== TEST COMMENCER PREPARATION ====================

    @Test
    @DisplayName("Cuisine - Commencer la préparation d'une commande EN_ATTENTE")
    void commencerPreparation_ShouldChangeStatusToEnPreparation() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commandeAttente));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commandeAttente);
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);

        cuisineService.commencerPreparation(1L);

        assertEquals(StatutCommande.EN_PREPARATION, commandeAttente.getStatut());
        verify(commandeRepository).save(commandeAttente);
    }

//    @Test
//    @DisplayName("Cuisine - Commencer préparation : Ne rien changer si déjà en préparation")
//    void commencerPreparation_ShouldDoNothing_WhenAlreadyInPrep() {
//        commandeAttente.setStatut(StatutCommande.EN_PREPARATION);
//        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commandeAttente));
//        when(commandeRepository.save(any(Commande.class))).thenReturn(commandeAttente);
//        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);
//
//        cuisineService.commencerPreparation(1L);
//
//        assertEquals(StatutCommande.EN_PREPARATION, commandeAttente.getStatut());
//        // Vérifie qu'on sauvegarde quand même l'état actuel
//        verify(commandeRepository).save(commandeAttente);
//    }

    // ==================== TEST MARQUER COMME PRETE ====================

    @Test
    @DisplayName("Cuisine - Marquer une commande comme PRÊTE")
    void marquerCommePrete_ShouldChangeStatusToPrete() {
        commandeAttente.setStatut(StatutCommande.EN_PREPARATION);
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commandeAttente));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commandeAttente);
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);

        cuisineService.marquerCommePrete(1L);

        assertEquals(StatutCommande.PRETE, commandeAttente.getStatut());
        verify(commandeRepository).save(commandeAttente);
    }

    // ==================== TEST CAS D'ERREUR ====================

    @Test
    @DisplayName("Cuisine - Erreur si la commande n'existe pas")
    void action_ShouldThrowException_WhenCommandeNotFound() {
        when(commandeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cuisineService.commencerPreparation(99L));
        assertThrows(RuntimeException.class, () -> cuisineService.marquerCommePrete(99L));
    }
}
