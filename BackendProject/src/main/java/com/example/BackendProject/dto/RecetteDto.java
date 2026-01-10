package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



public class RecetteDto {

    private Long id;
    private String nom;
    private Long platId;

    public RecetteDto(Long id, String nom, Long platId) {
        this.id = id;
        this.nom = nom;
        this.platId = platId;
    }

    public RecetteDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }
}
