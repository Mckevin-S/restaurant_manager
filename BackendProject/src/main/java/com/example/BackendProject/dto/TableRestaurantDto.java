package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Zone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class TableRestaurantDto {

    private Long id;
    private Zone zone;
    private String numero;
    private Integer capacite;


    public TableRestaurantDto(Long id, Zone zone, String numero, Integer capacite) {
        this.id = id;
        this.zone = zone;
        this.numero = numero;
        this.capacite = capacite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
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
}
