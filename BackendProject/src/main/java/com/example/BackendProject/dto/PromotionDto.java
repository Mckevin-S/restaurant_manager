package com.example.BackendProject.dto;

import com.example.BackendProject.utils.TypePromotion;

import java.math.BigDecimal;
import java.sql.Date;

public class PromotionDto {

    private Long id;
    private String nom;
    private TypePromotion type;
    private BigDecimal valeur;
    private Boolean actif;
    private Date dateExpiration;

}
