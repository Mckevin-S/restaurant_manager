package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "table_restaurant", schema = "restaurant", catalog = "")
public class TableRestaurant {
    private Long id;
    private Long zoneId;
    private String numero;
    private Integer capacite;
    private Object statut;

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
    @Column(name = "zone_id", nullable = true)
    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    @Basic
    @Column(name = "numero", nullable = false, length = 20)
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Basic
    @Column(name = "capacite", nullable = true)
    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    @Basic
    @Column(name = "statut", nullable = true)
    public Object getStatut() {
        return statut;
    }

    public void setStatut(Object statut) {
        this.statut = statut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableRestaurant that = (TableRestaurant) o;
        return Objects.equals(id, that.id) && Objects.equals(zoneId, that.zoneId) && Objects.equals(numero, that.numero) && Objects.equals(capacite, that.capacite) && Objects.equals(statut, that.statut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, zoneId, numero, capacite, statut);
    }
}
