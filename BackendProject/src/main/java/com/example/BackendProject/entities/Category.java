package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "category")
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer ordreAffichage;

    @OneToMany(mappedBy = "category")
    private List<Plat> plats;
}
