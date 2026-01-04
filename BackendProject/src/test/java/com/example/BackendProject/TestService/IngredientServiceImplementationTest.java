package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.IngredientDto;
import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.mappers.IngredientMapper;
import com.example.BackendProject.repository.IngredientRepository;
import com.example.BackendProject.services.implementations.IngredientServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Gestion des Ingrédients et Stock")
class IngredientServiceImplementationTest {

    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private IngredientMapper ingredientMapper;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private IngredientServiceImplementation ingredientService;

    private Ingredient ingredient;
    private IngredientDto ingredientDto;

    @BeforeEach
    void setUp() {
        ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setNom("Tomate");
        ingredient.setUniteMesure("KG");
        ingredient.setQuantiteActuelle(new BigDecimal("10.0"));
        ingredient.setSeuilAlerte(new BigDecimal("2.0"));

        ingredientDto = new IngredientDto();
        ingredientDto.setNom("Tomate");
        ingredientDto.setUniteMesure("KG");
        ingredientDto.setQuantiteActuelle(new BigDecimal("10.0"));
        ingredientDto.setSeuilAlerte(new BigDecimal("2.0"));
    }

    // ==================== TESTS DE VALIDATION & SAVE ====================

    @Test
    @DisplayName("Save - Succès et vérification de l'alerte")
    void save_ShouldSucceed_AndNotAlert_WhenStockIsHigh() {
        when(ingredientRepository.existsByNomIgnoreCase(anyString())).thenReturn(false);
        when(ingredientMapper.toEntity(any())).thenReturn(ingredient);
        when(ingredientRepository.save(any())).thenReturn(ingredient);
        when(ingredientMapper.toDto(any())).thenReturn(ingredientDto);

        ingredientService.save(ingredientDto);

        // Pas d'alerte car 10.0 > 2.0
        verify(messagingTemplate, never()).convertAndSend(eq("/topic/stock/alertes"), any(Object.class));
    }

    @Test
    @DisplayName("Save - Échec si le nom existe déjà")
    void save_ShouldThrow_WhenNameExists() {
        when(ingredientRepository.existsByNomIgnoreCase("Tomate")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> ingredientService.save(ingredientDto));
    }

    // ==================== TESTS DE GESTION DES QUANTITÉS ====================

    @Test
    @DisplayName("RetirerQuantite - Succès et déclenchement d'alerte WebSocket")
    @SuppressWarnings("unchecked")
    void retirerQuantite_ShouldTriggerWebsocket_WhenBelowSeuil() {
        // On retire 9kg, il reste 1kg (seuil à 2kg)
        BigDecimal quantiteARetirer = new BigDecimal("9.0");
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any())).thenReturn(ingredient);
        when(ingredientMapper.toDto(any())).thenReturn(ingredientDto);

        ingredientService.retirerQuantite(1L, quantiteARetirer);

        // Capturer l'objet envoyé par WebSocket
        ArgumentCaptor<Object> alertCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/stock/alertes"), alertCaptor.capture());

        Map<String, Object> alertMap = (Map<String, Object>) alertCaptor.getValue();
        assertEquals("FAIBLE", alertMap.get("status"));
        assertEquals("Tomate", alertMap.get("nom"));
        assertEquals(new BigDecimal("1.0"), alertMap.get("quantite"));
    }

    @Test
    @DisplayName("RetirerQuantite - Échec si stock insuffisant")
    void retirerQuantite_ShouldThrow_WhenStockInsufficient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        BigDecimal tropGrand = new BigDecimal("50.0");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ingredientService.retirerQuantite(1L, tropGrand));
        assertTrue(ex.getMessage().contains("Stock insuffisant"));
    }

    @Test
    @DisplayName("AjouterQuantite - Succès")
    void ajouterQuantite_ShouldIncreaseStock() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any())).thenReturn(ingredient);
        when(ingredientMapper.toDto(any())).thenReturn(ingredientDto);

        ingredientService.ajouterQuantite(1L, new BigDecimal("5.0"));

        assertEquals(new BigDecimal("15.0"), ingredient.getQuantiteActuelle());
        verify(ingredientRepository).save(ingredient);
    }

    // ==================== TESTS D'ALERTE RUPTURE ====================

    @Test
    @DisplayName("Alerte - Statut RUPTURE quand stock tombe à zéro")
    @SuppressWarnings("unchecked")
    void verifierEtAlerter_ShouldSendRuptureStatus() {
        // Initialiser le stock à 1.0
        ingredient.setQuantiteActuelle(new BigDecimal("1.0"));

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);
        when(ingredientMapper.toDto(any())).thenReturn(ingredientDto);

        // Retirer exactement 1.0 pour arriver à ZÉRO (ne pas dépasser pour éviter l'exception)
        ingredientService.retirerQuantite(1L, new BigDecimal("1.0"));

        // Capturer l'alerte
        ArgumentCaptor<Object> alertCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/stock/alertes"), alertCaptor.capture());

        Map<String, Object> alertMap = (Map<String, Object>) alertCaptor.getValue();

        // Vérifications
        assertTrue(BigDecimal.ZERO.compareTo(ingredient.getQuantiteActuelle()) == 0,
                "La quantité actuelle devrait être égale à zéro");
        assertEquals("RUPTURE", alertMap.get("status"));
        assertEquals("Attention : Stock RUPTURE pour Tomate", alertMap.get("message"));
    }
}
