package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CategoryDto {


    private Long id;
    private Menu menu;
    private String nom;
    private String description;
    private Integer ordreAffichage;

    public CategoryDto(Long id, Menu menu, String nom, String description, Integer ordreAffichage) {
        this.id = id;
        this.menu = menu;
        this.nom = nom;
        this.description = description;
        this.ordreAffichage = ordreAffichage;
    }

    public CategoryDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
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

    public Integer getOrdreAffichage() {
        return ordreAffichage;
    }

    public void setOrdreAffichage(Integer ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }
}
