package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



public class RecetteDto {

    private Long id;
    private Plat plat;
    private String nom;

    public RecetteDto(Long id, Plat plat, String nom) {
        this.id = id;
        this.plat = plat;
        this.nom = nom;
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
}
