package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "recette", schema = "restaurant", catalog = "")
public class RecetteEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "plat_id", nullable = true)
    private Long platId;
    @Basic
    @Column(name = "nom", nullable = true, length = 100)
    private String nom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecetteEntity that = (RecetteEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(platId, that.platId) && Objects.equals(nom, that.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, platId, nom);
    }
}
