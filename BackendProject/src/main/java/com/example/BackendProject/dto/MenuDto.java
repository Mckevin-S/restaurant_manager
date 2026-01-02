package com.example.BackendProject.dto;

public class MenuDto {

    private Long id;
    private String nom;

    public MenuDto(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public MenuDto() {

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


}
