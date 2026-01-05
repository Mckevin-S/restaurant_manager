package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.dto.TableRestaurantDto;
import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.TableRestaurantRepository;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.implementations.CommandeServiceImplementation;
import com.example.BackendProject.utils.RoleType;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - CommandeService")
class CommandeServiceImplementationTest {

    @Mock
    private CommandeRepository commandeRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private TableRestaurantRepository tableRestaurantRepository;
    @Mock
    private CommandeMapper commandeMapper;

    @InjectMocks
    private CommandeServiceImplementation service;

    private Commande commande;
    private CommandeDto commandeDto;
    private Utilisateur serveur;
    private TableRestaurant table;

    @BeforeEach
    void setUp() {
        // Mock Serveur
        serveur = new Utilisateur();
        serveur.setId(1L);
        serveur.setRole(RoleType.SERVEUR);

        UtilisateurDto serveurDto = new UtilisateurDto();
        serveurDto.setId(1L);

        // Mock Table
        table = new TableRestaurant();
        table.setId(10L);

        TableRestaurantDto tableDto = new TableRestaurantDto();
        tableDto.setId(10L);

        // Mock Commande
        commande = new Commande();
        commande.setId(100L);
        commande.setStatut(StatutCommande.EN_ATTENTE);

        commandeDto = new CommandeDto();
        commandeDto.setTypeCommande(TypeCommande.SUR_PLACE);
        commandeDto.setServeur(serveur);
        commandeDto.setTable(table);
    }

    // ==================== TESTS DE SAUVEGARDE ====================

    @Test
    @DisplayName("Save - Succès d'une commande sur place")
    void save_ShouldSucceed_WhenValid() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(serveur));
        when(tableRestaurantRepository.existsById(10L)).thenReturn(true);
        when(commandeMapper.toEntity(any())).thenReturn(commande);
        when(commandeRepository.save(any())).thenReturn(commande);
        when(commandeMapper.toDto(any())).thenReturn(commandeDto);

        CommandeDto result = service.save(commandeDto);

        assertNotNull(result);
        assertEquals(StatutCommande.EN_ATTENTE, commandeDto.getStatut());
        assertEquals(BigDecimal.ZERO, commandeDto.getTotalHt());
    }

    @Test
    @DisplayName("Save - Échec si l'utilisateur n'est pas un serveur")
    void save_ShouldThrow_WhenUserIsNotServeur() {
        serveur.setRole(RoleType.MANAGER); // On change le rôle pour l'échec
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(serveur));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.save(commandeDto));
        assertTrue(ex.getMessage().contains("rôle SERVEUR"));
    }

    @Test
    @DisplayName("Save - Échec si table introuvable pour commande sur place")
    void save_ShouldThrow_WhenTableNotFound() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(serveur));
        when(tableRestaurantRepository.existsById(10L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.save(commandeDto));
    }

    // ==================== TESTS DE STATUTS ====================

    @Test
    @DisplayName("UpdateStatut - Échec transition invalide (Attente -> Prête)")
    void updateStatut_ShouldThrow_WhenInvalidTransition() {
        when(commandeRepository.findById(100L)).thenReturn(Optional.of(commande));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateStatut(100L, StatutCommande.PRETE));

        assertTrue(ex.getMessage().contains("doit d'abord passer par 'En préparation'"));
    }

    @Test
    @DisplayName("UpdateStatut - Échec modification commande déjà payée")
    void updateStatut_ShouldThrow_WhenCommandeAlreadyPaid() {
        commande.setStatut(StatutCommande.PAYEE);
        when(commandeRepository.findById(100L)).thenReturn(Optional.of(commande));

        assertThrows(RuntimeException.class,
                () -> service.updateStatut(100L, StatutCommande.ANNULEE));
    }

    // ==================== TESTS DE SUPPRESSION ====================

    @Test
    @DisplayName("Delete - Échec si déjà payée")
    void delete_ShouldThrow_WhenPaid() {
        commande.setStatut(StatutCommande.PAYEE);
        when(commandeRepository.findById(100L)).thenReturn(Optional.of(commande));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(100L));
        assertEquals("Impossible de supprimer une commande déjà payée", ex.getMessage());
    }

    // ==================== TESTS DE RECHERCHE ====================

    @Test
    @DisplayName("FindByDateRange - Échec si début > fin")
    void findByDateRange_ShouldThrow_WhenDatesInverted() {
        Timestamp debut = new Timestamp(System.currentTimeMillis());
        Timestamp fin = new Timestamp(System.currentTimeMillis() - 100000);

        assertThrows(RuntimeException.class, () -> service.findByDateRange(debut, fin));
    }
}
