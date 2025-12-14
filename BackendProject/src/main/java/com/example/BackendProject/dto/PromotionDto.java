package com.example.BackendProject.dto;

import com.example.BackendProject.utils.TypePromotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;


@AllArgsConstructor
@Getter
@Setter
public class PromotionDto {

    private Long id;
    private String nom;
    private TypePromotion type;
    private BigDecimal valeur;
    private Boolean actif;
    private Date dateExpiration;

}
