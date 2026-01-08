package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.OrderItemOptionDto;
import com.example.BackendProject.entities.OrderItemOption;
import com.example.BackendProject.mappers.OrderItemOptionMapper;
import com.example.BackendProject.repository.OrderItemOptionRepository;
import com.example.BackendProject.services.implementations.OrderItemOptionServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - OrderItemOption (Options de commande)")
class OrderItemOptionServiceImplementationTest {

    @Mock
    private OrderItemOptionRepository repository;

    @Mock
    private OrderItemOptionMapper mapper;

    @InjectMocks
    private OrderItemOptionServiceImplementation service;

    private OrderItemOption entity;
    private OrderItemOptionDto dto;

    @BeforeEach
    void setUp() {
        entity = new OrderItemOption();
        entity.setId(1L);
        entity.setNomOption("Sans oignons");
        entity.setPrixSupplementaire(BigDecimal.ZERO);

        dto = new OrderItemOptionDto();
        dto.setId(1L);
        dto.setNomOption("Sans oignons");
        dto.setPrixSupplementaire(BigDecimal.ZERO);
        dto.setLigneCommande(10L);
    }

    // ==================== TEST SAVE ====================

    @Test
    @DisplayName("Save - Devrait transformer le DTO, sauvegarder et retourner le résultat")
    void save_ShouldPersistAndReturnDto() {
        // Arrange
        when(mapper.toEntity(any(OrderItemOptionDto.class))).thenReturn(entity);
        when(repository.save(any(OrderItemOption.class))).thenReturn(entity);
        when(mapper.toDto(any(OrderItemOption.class))).thenReturn(dto);

        // Act
        OrderItemOptionDto result = service.save(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Sans oignons", result.getNomOption());
        verify(mapper).toEntity(dto);
        verify(repository).save(entity);
    }

    // ==================== TEST FIND BY LIGNE COMMANDE ====================

    @Test
    @DisplayName("GetByLigneCommande - Devrait retourner la liste des options pour une ligne donnée")
    void getByLigneCommande_ShouldReturnFilteredList() {
        // Arrange
        Long ligneId = 10L;
        List<OrderItemOption> list = Arrays.asList(entity, new OrderItemOption());

        when(repository.findByLigneCommandeId(ligneId)).thenReturn(list);
        when(mapper.toDto(any(OrderItemOption.class))).thenReturn(dto);

        // Act
        List<OrderItemOptionDto> result = service.getByLigneCommande(ligneId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findByLigneCommandeId(ligneId);
        verify(mapper, times(2)).toDto(any());
    }

    // ==================== TEST DELETE ====================

    @Test
    @DisplayName("Delete - Devrait appeler le repository pour suppression")
    void delete_ShouldCallRepository() {
        // Arrange
        Long id = 1L;
        // On simule que l'élément existe pour passer la validation du service
        when(repository.existsById(id)).thenReturn(true);

        // Act
        service.delete(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }
}
