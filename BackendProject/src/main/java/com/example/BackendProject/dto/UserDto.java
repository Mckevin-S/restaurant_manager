package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.utils.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;
    private Restaurant restaurant;
    private RoleType role;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private LocalDate dateEmbauche;

}
