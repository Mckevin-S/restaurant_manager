package com.example.BackendProject.dto;

import com.example.BackendProject.utils.StatutTable;
import com.example.BackendProject.entities.Zone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class TableRestaurantDto {
    private Long id;
    private Long zoneId; // Utilisation de l'ID uniquement pour casser la boucle
    private String numero;
    private Integer capacite;
    private StatutTable statut;

    public TableRestaurantDto(Long id, Long zoneId, String numero, Integer capacite, StatutTable statut) {
        this.id = id;
        this.zoneId = zoneId;
        this.numero = numero;
        this.capacite = capacite;
        this.statut = statut;
    }

    public TableRestaurantDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }
    public void setStatut(StatutTable statut){
        this.statut = statut;
    }
    public StatutTable getStatut(){
        return statut;
    }
}
