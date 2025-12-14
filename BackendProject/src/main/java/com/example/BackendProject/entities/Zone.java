package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "zone")
public class Zone {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "zone")
    private List<TableRestaurant> tables;

    public Zone(Long id, Restaurant restaurant, String nom, String description, List<TableRestaurant> tables) {
        this.id = id;
        this.restaurant = restaurant;
        this.nom = nom;
        this.description = description;
        this.tables = tables;
    }

    public Zone() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TableRestaurant> getTables() {
        return tables;
    }

    public void setTables(List<TableRestaurant> tables) {
        this.tables = tables;
    }
}

