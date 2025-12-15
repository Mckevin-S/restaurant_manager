package com.example.BackendProject.mappers;

import com.example.BackendProject.dto.UtilisateurDto;
import com.example.BackendProject.entities.Utilisateur;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    UtilisateurDto toDto(Utilisateur utilisateur);

    Utilisateur toEntity(UtilisateurDto utilisateurDto);
}
