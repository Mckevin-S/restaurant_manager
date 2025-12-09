package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "option_item")
public class OptionItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private BigDecimal prixSupplementaire;

    @ManyToMany(mappedBy = "options")
    private List<Plat> plats;
}
