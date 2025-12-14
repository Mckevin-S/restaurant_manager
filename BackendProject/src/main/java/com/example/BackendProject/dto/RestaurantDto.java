package com.example.BackendProject.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RestaurantDto {

    private Long id;
    private String nom;
    private String adresse;
    private String telephone;
    private BigDecimal tauxTva;
    private String devise;
    private Timestamp dateCreation;

}
