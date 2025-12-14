package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
@Setter
public class IngredientDto {

    private Long id;
    private String nom;
    private BigDecimal quantiteActuelle;
    private String uniteMesure;
    private BigDecimal seuilAlerte;

}
