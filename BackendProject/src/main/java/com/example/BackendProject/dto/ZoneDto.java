package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class ZoneDto {

    private Long id;
    private Restaurant restaurant;
    private String nom;
    private String description;

    public ZoneDto(Long id, Restaurant restaurant, String nom, String description) {
        this.id = id;
        this.restaurant = restaurant;
        this.nom = nom;
        this.description = description;
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
}
