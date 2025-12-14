package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.entities.Plat;

import java.math.BigDecimal;

public class LigneCommandeDto {

    private Long id;
    private Commande commande;
    private Plat plat;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private String notesCuisine;

}
