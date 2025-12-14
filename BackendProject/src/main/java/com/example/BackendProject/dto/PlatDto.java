package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Category;
import com.example.BackendProject.entities.Recette;

import java.math.BigDecimal;

public class PlatDto {

    private Long id;
    private Category category;
    private String nom;
    private String description;
    private BigDecimal prix;
    private String photoUrl;
    private Recette recette;

}
