package com.example.BackendProject.mappers;

import com.example.BackendProject.entities.Commande;
import org.mapstruct.Mapper;
import com.example.BackendProject.entities.Paiement;
import com.example.BackendProject.dto.PaiementDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaiementMapper {

    // 1. Mapping de l'Entité vers le DTO
    // On extrait l'ID de l'objet Commande pour le mettre dans le Long du DTO
    @Mapping(target = "commande", source = "commande.id")
    PaiementDto toDto(Paiement entity);

    // 2. Mapping du DTO vers l'Entité
    // On utilise l'ID du DTO pour recréer une instance de Commande
    @Mapping(target = "commande", source = "commande")
    Paiement toEntity(PaiementDto dto);

    // 3. La méthode "magique" qui résout votre erreur
    // MapStruct l'utilisera automatiquement dès qu'il doit transformer un Long en Commande
    default Commande map(Long id) {
        if (id == null) return null;
        Commande commande = new Commande();
        commande.setId(id);
        return commande;
    }
}
