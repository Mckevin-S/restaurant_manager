package com.example.BackendProject.dto;

import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.entities.OptionItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
@Setter
public class OrderItemOptionDto {

    private Long id;
    private LigneCommande ligneCommande;
    private OptionItem option;
    private String nomOption;
    private BigDecimal prixSupplementaire;

}
