package com.example.BackendProject.mappers;

import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.entities.OptionItem;
import org.mapstruct.Mapper;
import com.example.BackendProject.entities.OrderItemOption;
import com.example.BackendProject.dto.OrderItemOptionDto;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemOptionMapper {

    // 1. Entité -> DTO (Extraction des IDs)
    // On utilise "option.id" car l'erreur dit que "optionItem" n'existe pas
    @Mapping(target = "ligneCommande", source = "ligneCommande.id")
    @Mapping(target = "option", source = "option.id")
    OrderItemOptionDto toDto(OrderItemOption entity);

    // 2. DTO -> Entité (Conversion IDs -> Objets)
    @Mapping(target = "ligneCommande", source = "ligneCommande")
    @Mapping(target = "option", source = "option")
    OrderItemOption toEntity(OrderItemOptionDto dto);

    // 3. Méthodes de support pour la conversion Long -> Objet
    default LigneCommande mapLigne(Long id) {
        if (id == null) return null;
        LigneCommande ligne = new LigneCommande();
        ligne.setId(id);
        return ligne;
    }

    default OptionItem mapOption(Long id) {
        if (id == null) return null;
        OptionItem opt = new OptionItem();
        opt.setId(id);
        return opt;
    }
}