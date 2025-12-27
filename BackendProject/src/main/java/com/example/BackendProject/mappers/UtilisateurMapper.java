package com.example.BackendProject.mappers;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    @Mapping(source = "restaurant.id", target = "restaurantId")
    UtilisateurDto toDto(Utilisateur utilisateur);

    @Mapping(source = "restaurantId", target = "restaurant.id")
    Utilisateur toEntity(UtilisateurDto utilisateurDto);
}
