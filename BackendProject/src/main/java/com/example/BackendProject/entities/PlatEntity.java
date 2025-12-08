package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "plat", schema = "restaurant", catalog = "")
public class PlatEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "category_id", nullable = true)
    private Long categoryId;
    @Basic
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    @Basic
    @Column(name = "description", nullable = true, length = -1)
    private String description;
    @Basic
    @Column(name = "prix", nullable = false, precision = 2)
    private BigDecimal prix;
    @Basic
    @Column(name = "photo_url", nullable = true, length = 255)
    private String photoUrl;
    @Basic
    @Column(name = "disponibilite", nullable = true)
    private Byte disponibilite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

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
        PlatEntity that = (PlatEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(categoryId, that.categoryId) && Objects.equals(nom, that.nom) && Objects.equals(description, that.description) && Objects.equals(prix, that.prix) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(disponibilite, that.disponibilite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryId, nom, description, prix, photoUrl, disponibilite);
    }
}
