package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;

import java.math.BigDecimal;

public class RecetteItemDto {

    private Long id;
    private Recette recette;
    private Ingredient ingredient;
    private BigDecimal quantiteRequise;

}
