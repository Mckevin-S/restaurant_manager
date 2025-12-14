package com.example.BackendProject.dto;

import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.entities.OptionItem;

import java.math.BigDecimal;

public class OrderItemOptionDto {

    private Long id;
    private LigneCommande ligneCommande;
    private OptionItem option;
    private String nomOption;
    private BigDecimal prixSupplementaire;

}
