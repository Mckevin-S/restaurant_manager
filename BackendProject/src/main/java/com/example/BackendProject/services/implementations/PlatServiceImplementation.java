package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.mappers.PlatMapper;
import com.example.BackendProject.repository.LigneCommandeRepository;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.services.interfaces.PlatServiceInterface;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlatServiceImplementation implements PlatServiceInterface {

    private final PlatRepository platRepository;
    private final CategoryRepository categoryRepository;
    private final PlatMapper platMapper;
//    private final LigneCommandeRepository ligneCommandeRepository;

    // Dossier racine pour le stockage des images
    private final String UPLOAD_DIR = "uploads/plats/";

    public PlatServiceImplementation(PlatRepository platRepository,
                                     CategoryRepository categoryRepository,
                                     PlatMapper platMapper,
                                     LigneCommandeRepository ligneCommandeRepository) {
        this.platRepository = platRepository;
        this.categoryRepository = categoryRepository;
        this.platMapper = platMapper;
//        this.ligneCommandeRepository = ligneCommandeRepository;
    }

    // --- CRUD DE BASE ---

    @Override
    public PlatDto save(PlatDto platDto) {
        if (platDto.getCategory() != null) {
            categoryRepository.findById(platDto.getCategory())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        }
        Plat plat = platMapper.toEntity(platDto);
        // Par défaut, un nouveau plat est disponible
        plat.setDisponibilite(true);
        return platMapper.toDto(platRepository.save(plat));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatDto> getAll() {
        return platRepository.findAll().stream()
                .map(platMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlatDto getById(Long id) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé avec l'ID : " + id));
        return platMapper.toDto(plat);
    }

    @Override
    public PlatDto update(Long id, PlatDto platDto) {
        Plat existingPlat = platRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Impossible de mettre à jour : Plat non trouvé avec l'ID " + id));

        existingPlat.setNom(platDto.getNom());
        existingPlat.setDescription(platDto.getDescription());
        existingPlat.setPrix(platDto.getPrix());
//        existingPlat.setDisponibilite(platDto.isDisponible());
        existingPlat.setRecette(platDto.getRecette());

        // On ne met à jour l'URL photo ici que si elle est fournie explicitement (via texte)
        if (platDto.getPhotoUrl() != null) {
            existingPlat.setPhotoUrl(platDto.getPhotoUrl());
        }

        if (platDto.getCategory() != null) {
            var category = categoryRepository.findById(platDto.getCategory())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            existingPlat.setCategory(category);
        }

        return platMapper.toDto(platRepository.save(existingPlat));
    }

    @Override
    public void delete(Long id) {
        if (!platRepository.existsById(id)) {
            throw new RuntimeException("Plat non trouvé");
        }
        platRepository.deleteById(id);
    }

    // --- GESTION DE LA DISPONIBILITÉ ---

    @Override
    public List<PlatDto> getMenuActif() {
        return platRepository.findByDisponibiliteTrue().stream()
                .map(platMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlatDto modifierDisponibilite(Long id, boolean estDisponible) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé"));
        plat.setDisponibilite(estDisponible);
        return platMapper.toDto(platRepository.save(plat));
    }

    // --- GESTION DE L'IMAGE (UPLOAD & RESIZE) ---

    @Override
    public PlatDto uploadPlatImage(Long id, MultipartFile file) throws IOException {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé"));

        if (file.isEmpty()) throw new RuntimeException("Le fichier est vide");

        // Création du dossier si inexistant
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        // Suppression de l'ancienne image si elle existe (pour économiser l'espace)
        if (plat.getPhotoUrl() != null) {
            Path oldPath = Paths.get(plat.getPhotoUrl().replace("/api/images/", UPLOAD_DIR));
            Files.deleteIfExists(oldPath);
        }

        // Génération nom unique et redimensionnement
        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path filePath = uploadPath.resolve(fileName);

        Thumbnails.of(file.getInputStream())
                .size(800, 600)
                .outputFormat("jpg")
                .outputQuality(0.75) // Compression à 75%
                .toFile(filePath.toFile());

        plat.setPhotoUrl("/api/images/" + fileName);
        return platMapper.toDto(platRepository.save(plat));
    }

    // --- STATISTIQUES ---

    @Override
    public List<Map<String, Object>> getStatistiquesPlatsVendus() {
        List<Object[]> results = platRepository.findTopSellingPlats();

        return results.stream().map(result -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nomPlat", result[0]);
            stats.put("quantiteTotale", result[1]);
            return stats;
        }).collect(Collectors.toList());
    }
}