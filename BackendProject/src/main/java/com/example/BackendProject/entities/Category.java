package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Category {
    private Long id;
    private Long menuId;
    private String nom;
    private String description;
    private Integer ordreAffichage;

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
    @Column(name = "menu_id", nullable = true)
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
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

    @Basic
    @Column(name = "ordre_affichage", nullable = true)
    public Integer getOrdreAffichage() {
        return ordreAffichage;
    }

    public void setOrdreAffichage(Integer ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(menuId, category.menuId) && Objects.equals(nom, category.nom) && Objects.equals(description, category.description) && Objects.equals(ordreAffichage, category.ordreAffichage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, menuId, nom, description, ordreAffichage);
    }
}
