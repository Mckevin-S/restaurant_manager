package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Recette")
public class Recette {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "plat_id")
    private Plat plat;

    private String nom;

    @OneToMany(mappedBy = "recette")
    private List<RecetteItem> items;
}
