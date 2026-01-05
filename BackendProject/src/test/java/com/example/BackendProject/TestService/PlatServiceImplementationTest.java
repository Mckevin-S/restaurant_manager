package com.example.BackendProject.TestService;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.mappers.PlatMapper;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.services.implementations.PlatServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Service - Gestion des Plats")
class PlatServiceImplementationTest {

    @Mock private PlatRepository platRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private PlatMapper platMapper;

    @InjectMocks
    private PlatServiceImplementation platService;

    @TempDir
    Path tempDir; // Crée un dossier temporaire pour les tests d'upload

    private Plat plat;
    private PlatDto platDto;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setNom("Entrées");

        plat = new Plat();
        plat.setId(10L);
        plat.setNom("Salade César");
        plat.setPrix(new BigDecimal("12.50"));
        plat.setDisponibilite(true);
        plat.setCategory(category);

        platDto = new PlatDto();
        platDto.setId(10L);
        platDto.setNom("Salade César");
        platDto.setCategory(1L);

        // Injecter le dossier temporaire dans la constante UPLOAD_DIR via Reflection
        ReflectionTestUtils.setField(platService, "UPLOAD_DIR", tempDir.toString() + "/");
    }

    // ==================== TESTS CRUD ====================

    @Test
    @DisplayName("Save - Succès avec catégorie valide")
    void save_ShouldSucceed() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(platMapper.toEntity(any())).thenReturn(plat);
        when(platRepository.save(any())).thenReturn(plat);
        when(platMapper.toDto(any())).thenReturn(platDto);

        PlatDto result = platService.save(platDto);

        assertNotNull(result);
        verify(platRepository).save(any(Plat.class));
    }

    @Test
    @DisplayName("Update - Modifier les informations d'un plat")
    void update_ShouldModifyFields() {
        when(platRepository.findById(10L)).thenReturn(Optional.of(plat));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(platRepository.save(any())).thenReturn(plat);
        when(platMapper.toDto(any())).thenReturn(platDto);

        platDto.setNom("Salade César Deluxe");
        PlatDto result = platService.update(10L, platDto);

        assertEquals("Salade César Deluxe", plat.getNom());
        verify(platRepository).save(plat);
    }

    // ==================== TESTS DISPONIBILITÉ ====================

    @Test
    @DisplayName("ModifierDisponibilite - Basculer l'état du plat")
    void modifierDisponibilite_ShouldChangeStatus() {
        when(platRepository.findById(10L)).thenReturn(Optional.of(plat));
        when(platRepository.save(any())).thenReturn(plat);
        when(platMapper.toDto(any())).thenReturn(platDto);

        platService.modifierDisponibilite(10L, false);

        assertFalse(plat.getDisponibilite());
        verify(platRepository).save(plat);
    }

    // ==================== TESTS UPLOAD IMAGE ====================

//    @Test
//    @DisplayName("UploadImage - Sauvegarde physique et mise à jour URL")
//    void uploadPlatImage_ShouldSaveFileOnDisk() throws IOException {
//        // 1. GÉNÉRER UNE VRAIE IMAGE MINIMALE EN MÉMOIRE
//        BufferedImage canvas = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(canvas, "jpg", baos); // On écrit une vraie structure JPEG dans le flux
//        byte[] imageBytes = baos.toByteArray();
//
//        // 2. CRÉER LE MOCK MULTIPART FILE AVEC CES OCTETS
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "test.jpg",
//                "image/jpeg",
//                imageBytes
//        );
//
//        // 3. CONFIGURATION DES MOCKS
//        when(platRepository.findById(10L)).thenReturn(Optional.of(plat));
//        when(platRepository.save(any(Plat.class))).thenReturn(plat);
//        when(platMapper.toDto(any(Plat.class))).thenReturn(platDto);
//
//        // 4. EXÉCUTION
//        PlatDto result = platService.uploadPlatImage(10L, file);
//
//        // 5. ASSERTIONS
//        assertNotNull(plat.getPhotoUrl());
//        String fileName = plat.getPhotoUrl().replace("/api/images/", "");
//        assertTrue(tempDir.resolve(fileName).toFile().exists(), "Le fichier redimensionné doit exister sur le disque");
//    }

    // ==================== TESTS STATISTIQUES ====================

    @Test
    @DisplayName("GetStatistiques - Transformer les résultats de la requête native")
    void getStatistiquesPlatsVendus_ShouldFormatMap() {
        Object[] stat1 = {"Pizza", 50L};
        when(platRepository.findTopSellingPlats()).thenReturn(Collections.singletonList(stat1));

        List<Map<String, Object>> stats = platService.getStatistiquesPlatsVendus();

        assertEquals(1, stats.size());
        assertEquals("Pizza", stats.get(0).get("nomPlat"));
        assertEquals(50L, stats.get(0).get("quantiteTotale"));
    }
}
