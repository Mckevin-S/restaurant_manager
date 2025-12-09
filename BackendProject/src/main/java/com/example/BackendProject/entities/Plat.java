package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Plat {
    private Long id;
    private Long categoryId;
    private String nom;
    private String description;
    private BigDecimal prix;
    private String photoUrl;
    private Byte disponibilite;

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
    @Column(name = "category_id", nullable = true)
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
    @Column(name = "prix", nullable = false, precision = 2)
    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    @Basic
    @Column(name = "photo_url", nullable = true, length = 255)
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Basic
    @Column(name = "disponibilite", nullable = true)
    public Byte getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(Byte disponibilite) {
        this.disponibilite = disponibilite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plat plat = (Plat) o;
        return Objects.equals(id, plat.id) && Objects.equals(categoryId, plat.categoryId) && Objects.equals(nom, plat.nom) && Objects.equals(description, plat.description) && Objects.equals(prix, plat.prix) && Objects.equals(photoUrl, plat.photoUrl) && Objects.equals(disponibilite, plat.disponibilite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryId, nom, description, prix, photoUrl, disponibilite);
    }
}
