package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;


@AllArgsConstructor
@Getter
@Setter
public class RestaurantDto {

    private Long id;
    private String nom;
    private String adresse;
    private String telephone;
    private BigDecimal tauxTva;
    private String devise;
    private Timestamp dateCreation;

}
