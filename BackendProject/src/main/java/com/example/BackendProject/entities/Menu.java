package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "menu")
public class Menu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private Boolean actif = true;

    @OneToMany(mappedBy = "menu")
    private List<Category> categories;
}
