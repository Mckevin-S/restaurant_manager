package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Recette {
    private Long id;
    private Long platId;
    private String nom;

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
    @Column(name = "plat_id", nullable = true)
    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    @Basic
    @Column(name = "nom", nullable = true, length = 100)
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
        Recette recette = (Recette) o;
        return Objects.equals(id, recette.id) && Objects.equals(platId, recette.platId) && Objects.equals(nom, recette.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, platId, nom);
    }
}
