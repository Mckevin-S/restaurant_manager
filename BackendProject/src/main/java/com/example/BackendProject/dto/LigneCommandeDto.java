package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
@Setter
public class LigneCommandeDto {

    private Long id;
    private Commande commande;
    private Plat plat;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private String notesCuisine;

}
