package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Ingredient;
import com.example.BackendProject.utils.TypeMouvement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;


@AllArgsConstructor
@Getter
@Setter
public class StockMovementDto {

    private Long id;
    private Ingredient ingredient;
    private TypeMouvement typeMouvement;
    private BigDecimal quantite;
    private Timestamp dateMouvement;
    private String raison;

}
