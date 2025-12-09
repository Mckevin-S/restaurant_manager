package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ingredient")
public class Ingredient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private BigDecimal quantiteActuelle;
    private String uniteMesure;
    private BigDecimal seuilAlerte;

    @OneToMany(mappedBy = "ingredient")
    private List<StockMovement> mouvements;

    @OneToMany(mappedBy = "ingredient")
    private List<RecetteItem> recetteItems;
}
