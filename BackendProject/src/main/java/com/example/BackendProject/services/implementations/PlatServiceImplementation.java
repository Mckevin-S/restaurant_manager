package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.PlatDto;
import com.example.BackendProject.entities.Plat;
import com.example.BackendProject.mappers.PlatMapper;
import com.example.BackendProject.repository.LigneCommandeRepository;
import com.example.BackendProject.repository.PlatRepository;
import com.example.BackendProject.repository.CategoryRepository;
import com.example.BackendProject.services.interfaces.PlatServiceInterface;
import com.example.BackendProject.utils.LoggingUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    private static final Logger logger = LoggerFactory.getLogger(PlatServiceImplementation.class);
    private final PlatRepository platRepository;
    private final CategoryRepository categoryRepository;
    private final PlatMapper platMapper;
//    private final LigneCommandeRepository ligneCommandeRepository;

    // Dossier pour le stockage des images - Gusto/src/images
    // On construit le chemin dynamiquement à partir du répertoire parent du projet
    private String getUploadDir() {
        // Obtient le répertoire courant du projet (BackendProject)
        String currentDir = System.getProperty("user.dir");
        // Remonte d'un niveau et va vers Gusto/src/images
        // C:\...\RestaurantManager\BackendProject -> C:\...\RestaurantManager\Gusto\src\images
        String uploadDir = currentDir.replace("BackendProject", "Gusto") + File.separator + "src" + File.separator + "images";
        return uploadDir;
    }

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
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de sauvegarde d'un plat - Nom: {}", context, platDto.getNom());
        if (platDto.getCategory() != null) {
            categoryRepository.findById(platDto.getCategory())
                    .orElseThrow(() -> {
                        logger.error("{} Catégorie non trouvée avec l'ID: {}", context, platDto.getCategory());
                        return new RuntimeException("Catégorie non trouvée");
                    });
        }
        Plat plat = platMapper.toEntity(platDto);
        // Par défaut, un nouveau plat est disponible
        plat.setDisponibilite(true);
        Plat savedPlat = platRepository.save(plat);
        logger.info("{} Plat sauvegardé avec succès. ID: {}, Nom: {}", context, savedPlat.getId(), savedPlat.getNom());
        return platMapper.toDto(savedPlat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatDto> getAll() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération de tous les plats", context);
        List<PlatDto> plats = platRepository.findAll().stream()
                .map(platMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} plats récupérés avec succès", context, plats.size());
        return plats;
    }

    @Override
    @Transactional(readOnly = true)
    public PlatDto getById(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération du plat avec l'ID: {}", context, id);
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Plat non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Plat non trouvé avec l'ID : " + id);
                });
        logger.info("{} Plat ID: {} récupéré avec succès - Nom: {}", context, id, plat.getNom());
        return platMapper.toDto(plat);
    }

    @Override
    public PlatDto update(Long id, PlatDto platDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de mise à jour du plat ID: {}", context, id);
        Plat existingPlat = platRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Plat non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Impossible de mettre à jour : Plat non trouvé avec l'ID " + id);
                });

        existingPlat.setNom(platDto.getNom());
        existingPlat.setDescription(platDto.getDescription());
        existingPlat.setPrix(platDto.getPrix());
//        existingPlat.setDisponibilite(platDto.isDisponible());
//        existingPlat.setRecette(platDto.getRecetteId());

        // On ne met à jour l'URL photo ici que si elle est fournie explicitement (via texte)
        if (platDto.getPhotoUrl() != null) {
            existingPlat.setPhotoUrl(platDto.getPhotoUrl());
        }

        if (platDto.getCategory() != null) {
            var category = categoryRepository.findById(platDto.getCategory())
                    .orElseThrow(() -> {
                        logger.error("{} Catégorie non trouvée avec l'ID: {}", context, platDto.getCategory());
                        return new RuntimeException("Catégorie non trouvée");
                    });
            existingPlat.setCategory(category);
        }

        Plat updatedPlat = platRepository.save(existingPlat);
        logger.info("{} Plat ID: {} mis à jour avec succès - Nom: {}", context, id, updatedPlat.getNom());
        return platMapper.toDto(updatedPlat);
    }

    @Override
    public void delete(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative de suppression du plat ID: {}", context, id);
        if (!platRepository.existsById(id)) {
            logger.error("{} Plat non trouvé avec l'ID: {}", context, id);
            throw new RuntimeException("Plat non trouvé");
        }
        platRepository.deleteById(id);
        logger.info("{} Plat ID: {} supprimé avec succès", context, id);
    }

    // --- GESTION DE LA DISPONIBILITÉ ---

    @Override
    public List<PlatDto> getMenuActif() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Récupération du menu actif (plats disponibles)", context);
        List<PlatDto> platsDisponibles = platRepository.findByDisponibiliteTrue().stream()
                .map(platMapper::toDto)
                .collect(Collectors.toList());
        logger.info("{} {} plats disponibles récupérés avec succès", context, platsDisponibles.size());
        return platsDisponibles;
    }

    @Override
    public PlatDto modifierDisponibilite(Long id, boolean estDisponible) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Modification de la disponibilité du plat ID: {} - Disponible: {}", context, id, estDisponible);
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Plat non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Plat non trouvé");
                });
        plat.setDisponibilite(estDisponible);
        Plat updatedPlat = platRepository.save(plat);
        logger.info("{} Disponibilité du plat ID: {} modifiée avec succès - Disponible: {}", context, id, estDisponible);
        return platMapper.toDto(updatedPlat);
    }

    // --- GESTION DE L'IMAGE (UPLOAD & RESIZE) ---

    @Override
    public PlatDto uploadPlatImage(Long id, MultipartFile file) throws IOException {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Tentative d'upload d'image pour le plat ID: {} - Nom du fichier: {}", context, id, file.getOriginalFilename());
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("{} Plat non trouvé avec l'ID: {}", context, id);
                    return new RuntimeException("Plat non trouvé");
                });

        if (file.isEmpty()) {
            logger.error("{} Fichier vide pour l'upload d'image du plat ID: {}", context, id);
            throw new RuntimeException("Le fichier est vide");
        }

        // Valider le type MIME
        String contentType = file.getContentType();
        logger.info("{} Type MIME reçu: {}", context, contentType);
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.error("{} Type de fichier invalide: {}", context, contentType);
            throw new RuntimeException("Le fichier doit être une image (image/jpeg, image/png, etc.)");
        }

        // Création du dossier si inexistant
        String uploadDirPath = getUploadDir();
        Path uploadPath = Paths.get(uploadDirPath);
        logger.info("{} Chemin upload: {}", context, uploadDirPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("{} Dossier images créé: {}", context, uploadDirPath);
        }

        // Génération nom unique et redimensionnement
        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path filePath = uploadPath.resolve(fileName);

        try {
            logger.info("{} Début du redimensionnement d'image vers: {}", context, filePath.toString());
            Thumbnails.of(file.getInputStream())
                    .size(800, 600)
                    .outputFormat("jpg")
                    .outputQuality(0.75) // Compression à 75%
                    .toFile(filePath.toFile());
            logger.info("{} Image redimensionnée avec succès. Taille du fichier: {} bytes", context, Files.size(filePath));
        } catch (IOException e) {
            logger.error("{} Erreur lors du traitement de l'image: {} - Message complet: {}", context, e.getMessage(), e.toString(), e);
            // Vérifier les permissions du dossier
            File uploadDir = uploadPath.toFile();
            logger.error("{} Dossier upload existe: {}, writable: {}, readable: {}", context, uploadDir.exists(), uploadDir.canWrite(), uploadDir.canRead());
            throw new IOException("Erreur lors du redimensionnement de l'image: " + e.getMessage(), e);
        }

        // Suppression de l'ancienne image si elle existe (pour économiser l'espace)
        if (plat.getPhotoUrl() != null && !plat.getPhotoUrl().isEmpty()) {
            try {
                String oldFileName = plat.getPhotoUrl();
                // Si c'est un chemin complet, extraire juste le nom du fichier
                if (oldFileName.contains("/")) {
                    oldFileName = oldFileName.substring(oldFileName.lastIndexOf("/") + 1);
                }
                Path oldPath = uploadPath.resolve(oldFileName);
                if (Files.exists(oldPath)) {
                    Files.delete(oldPath);
                    logger.info("{} Ancienne image supprimée: {}", context, oldFileName);
                }
            } catch (IOException e) {
                logger.warn("{} Impossible de supprimer l'ancienne image: {}", context, e.getMessage());
                // Non bloquant - continue l'upload même si la suppression échoue
            }
        }

        // Stocker juste le nom du fichier (référence locale)
        plat.setPhotoUrl(fileName);
        Plat savedPlat = platRepository.save(plat);
        logger.info("{} Image uploadée avec succès pour le plat ID: {} - Fichier: {}", context, id, fileName);
        return platMapper.toDto(savedPlat);
    }

    // --- STATISTIQUES ---

    @Override
    public List<Map<String, Object>> getStatistiquesPlatsVendus() {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Calcul des statistiques des plats les plus vendus", context);
        List<Object[]> results = platRepository.findTopSellingPlats();
        List<Map<String, Object>> statistiques = results.stream().map(result -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nomPlat", result[0]);
            stats.put("quantiteTotale", result[1]);
            return stats;
        }).collect(Collectors.toList());
        logger.info("{} {} plats trouvés dans les statistiques", context, statistiques.size());
        return statistiques;
    }
}