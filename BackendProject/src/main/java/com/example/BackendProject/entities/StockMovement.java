package com.example.BackendProject.entities;

import com.example.BackendProject.utils.TypeMouvement;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "StockMovement")
public class StockMovement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    private TypeMouvement typeMouvement;

    private BigDecimal quantite;
    private Timestamp dateMouvement;

    @Column(columnDefinition = "TEXT")
    private String raison;
}
