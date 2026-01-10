package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Restaurant;
import com.example.BackendProject.entities.TableRestaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class ZoneDto {
    private Long id;
    private Long restaurantId; // Utilisation de l'ID uniquement
    private String nom;
    private String description;
    // Si vous voulez lister les tables, utilisez un DTO qui ne contient PAS la zone
    private List<TableRestaurantDto> tables;

    public ZoneDto(Long id, Long restaurantId, String nom, String description, List<TableRestaurantDto> tables) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.nom = nom;
        this.description = description;
        this.tables = tables;
    }

    public ZoneDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
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

    public List<TableRestaurantDto> getTables() {
        return tables;
    }

    public void setTables(List<TableRestaurantDto> tables) {
        this.tables = tables;
    }
}
