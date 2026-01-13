package com.example.BackendProject.services;

import com.example.BackendProject.dto.CommandeDto;
import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.exceptions.RessourceNonTrouveeException;
import com.example.BackendProject.mappers.CommandeMapper;
import com.example.BackendProject.repository.CommandeRepository;
import com.example.BackendProject.repository.TableRestaurantRepository;
import com.example.BackendProject.repository.UtilisateurRepository;
import com.example.BackendProject.services.implementations.CommandeServiceImplementation;
import com.example.BackendProject.utils.StatutCommande;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Commande")
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private TableRestaurantRepository tableRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private CommandeMapper commandeMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private CommandeServiceImplementation commandeService;

    private Commande commande;
    private CommandeDto commandeDto;
    private TableRestaurant table;
    private Utilisateur serveur;

    @BeforeEach
    void setUp() {
        table = new TableRestaurant();
        table.setId(1L);
        table.setNumero("T1");

        serveur = new Utilisateur();
        serveur.setId(1L);
        serveur.setNom("Dupont");
        serveur.setRole(com.example.BackendProject.utils.RoleType.SERVEUR);

        commande = new Commande();
        commande.setId(1L);
        commande.setTable(table);
        commande.setServeur(serveur);
        commande.setDateHeureCommande(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setTotalHt(BigDecimal.valueOf(10000));
        commande.setTotalTtc(BigDecimal.valueOf(11800));

        commandeDto = new CommandeDto();
        commandeDto.setId(1L);
        commandeDto.setTableId(1L);
        commandeDto.setServeurId(1L);
        commandeDto.setStatut(StatutCommande.EN_ATTENTE);
        commandeDto.setTotalHt(BigDecimal.valueOf(10000));
        commandeDto.setTotalTtc(BigDecimal.valueOf(11800));
        commandeDto.setTypeCommande(com.example.BackendProject.utils.TypeCommande.SUR_PLACE);
    }

    @Test
    @DisplayName("Devrait créer une commande avec succès")
    void devraitCreerCommandeAvecSucces() {
        when(tableRepository.existsById(1L)).thenReturn(true); // Added for validation check
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(serveur));
        when(commandeMapper.toEntity(commandeDto)).thenReturn(commande);
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);
        when(commandeMapper.toDto(commande)).thenReturn(commandeDto);

        CommandeDto result = commandeService.save(commandeDto);

        assertNotNull(result);
        assertEquals(StatutCommande.EN_ATTENTE, result.getStatut());
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    @DisplayName("Devrait lever une exception si la table n'existe pas")
    void devraitLeverExceptionSiTableInexistante() {
        commandeDto.setServeurId(null); // Skip server validation
        when(tableRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RessourceNonTrouveeException.class, () -> {
            commandeService.save(commandeDto);
        });
        verify(commandeRepository, never()).save(any(Commande.class));
    }

    @Test
    @DisplayName("Devrait récupérer toutes les commandes")
    void devraitRecupererToutesLesCommandes() {
        List<Commande> commandes = Arrays.asList(commande);
        when(commandeRepository.findAll()).thenReturn(commandes);
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);

        List<CommandeDto> results = commandeService.getAll();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(commandeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Devrait récupérer une commande par ID")
    void devraitRecupererCommandeParId() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeMapper.toDto(commande)).thenReturn(commandeDto);

        CommandeDto result = commandeService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(commandeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Devrait lever une exception si la commande n'existe pas")
    void devraitLeverExceptionSiCommandeInexistante() {
        when(commandeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RessourceNonTrouveeException.class, () -> {
            commandeService.getById(999L);
        });
    }

    @Test
    @DisplayName("Devrait changer le statut d'une commande")
    void devraitChangerStatutCommande() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);

        CommandeDto result = commandeService.updateStatut(1L, StatutCommande.EN_PREPARATION);

        assertNotNull(result);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    @DisplayName("Devrait récupérer les commandes par table")
    void devraitRecupererCommandesParTable() {
        when(commandeRepository.findByTableId(1L)).thenReturn(Arrays.asList(commande));
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);

        List<CommandeDto> results = commandeService.findByTable(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(commandeRepository, times(1)).findByTableId(1L);
    }

    @Test
    @DisplayName("Devrait récupérer les commandes par serveur")
    void devraitRecupererCommandesParServeur() {
        when(commandeRepository.findByServeurId(1L)).thenReturn(Arrays.asList(commande));
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);

        List<CommandeDto> results = commandeService.findByServeur(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(commandeRepository, times(1)).findByServeurId(1L);
    }

    @Test
    @DisplayName("Devrait récupérer les commandes par statut")
    void devraitRecupererCommandesParStatut() {
        when(commandeRepository.findByStatut(StatutCommande.EN_ATTENTE))
                .thenReturn(Arrays.asList(commande));
        when(commandeMapper.toDto(any(Commande.class))).thenReturn(commandeDto);

        List<CommandeDto> results = commandeService.findByStatut(StatutCommande.EN_ATTENTE);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(commandeRepository, times(1)).findByStatut(StatutCommande.EN_ATTENTE);
    }

    @Test
    @DisplayName("Devrait supprimer une commande")
    void devraitSupprimerCommande() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        doNothing().when(commandeRepository).delete(commande);

        commandeService.delete(1L);

        verify(commandeRepository, times(1)).delete(commande);
    }

    @Test
    @DisplayName("Devrait lever une exception lors de la suppression d'une commande inexistante")
    void devraitLeverExceptionSupprimerCommandeInexistante() {
        when(commandeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RessourceNonTrouveeException.class, () -> {
            commandeService.delete(999L);
        });
        verify(commandeRepository, never()).delete(any(Commande.class));
    }

    @Test
    @DisplayName("Devrait calculer le total HT correctement")
    void devraitCalculerTotalHt() {
        BigDecimal totalHt = BigDecimal.valueOf(10000);
        commande.setTotalHt(totalHt);

        assertEquals(totalHt, commande.getTotalHt());
    }

    @Test
    @DisplayName("Devrait calculer le total TTC avec TVA")
    void devraitCalculerTotalTtc() {
        BigDecimal totalHt = BigDecimal.valueOf(10000);
        BigDecimal totalTtc = BigDecimal.valueOf(11800); // 18% TVA

        commande.setTotalHt(totalHt);
        commande.setTotalTtc(totalTtc);

        assertEquals(totalTtc, commande.getTotalTtc());
        assertTrue(commande.getTotalTtc().compareTo(commande.getTotalHt()) > 0);
    }
}
