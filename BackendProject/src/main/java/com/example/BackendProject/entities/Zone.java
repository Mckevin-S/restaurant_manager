package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Zone {
    private Long id;
    private Long restaurantId;
    private String nom;
    private String description;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "restaurant_id", nullable = true)
    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Basic
    @Column(name = "nom", nullable = false, length = 100)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(id, zone.id) && Objects.equals(restaurantId, zone.restaurantId) && Objects.equals(nom, zone.nom) && Objects.equals(description, zone.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, restaurantId, nom, description);
    }
}
