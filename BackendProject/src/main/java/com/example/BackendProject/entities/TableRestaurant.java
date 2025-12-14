package com.example.BackendProject.entities;

import com.example.BackendProject.utils.StatutTable;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "table_restaurant")
public class TableRestaurant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    private String numero;
    private Integer capacite;

    @Enumerated(EnumType.STRING)
    private StatutTable statut = StatutTable.Libre;

    @OneToMany(mappedBy = "table")
    private List<Commande> commandes;

    public TableRestaurant(Long id, Zone zone, String numero, Integer capacite, StatutTable statut, List<Commande> commandes) {
        this.id = id;
        this.zone = zone;
        this.numero = numero;
        this.capacite = capacite;
        this.statut = statut;
        this.commandes = commandes;
    }

    public TableRestaurant() {
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

    public StatutTable getStatut() {
        return statut;
    }

    public void setStatut(StatutTable statut) {
        this.statut = statut;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }
}
