package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.entities.Recette;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
@Setter
public class RecetteItemDto {

    private Long id;
    private Recette recette;
    private Ingredient ingredient;
    private BigDecimal quantiteRequise;

}
