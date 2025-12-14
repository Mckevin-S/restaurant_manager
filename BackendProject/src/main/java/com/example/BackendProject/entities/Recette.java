package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Recette")
public class Recette {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "plat_id")
    private Plat plat;

    private String nom;

    @OneToMany(mappedBy = "recette")
    private List<RecetteItem> items;

    public Recette(Long id, Plat plat, String nom, List<RecetteItem> items) {
        this.id = id;
        this.plat = plat;
        this.nom = nom;
        this.items = items;
    }

    public Recette() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plat getPlat() {
        return plat;
    }

    public void setPlat(Plat plat) {
        this.plat = plat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<RecetteItem> getItems() {
        return items;
    }

    public void setItems(List<RecetteItem> items) {
        this.items = items;
    }
}
