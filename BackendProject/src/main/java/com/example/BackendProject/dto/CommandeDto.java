package com.example.BackendProject.dto;

import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.entities.User;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;



public class CommandeDto {

    private Long id;
    private TableRestaurant table;
    private User serveur;
    private Timestamp dateHeureCommande;
    private StatutCommande statut;
    private TypeCommande typeCommande;
    private BigDecimal totalHt;
    private BigDecimal totalTtc;

    public CommandeDto(Long id, TableRestaurant table, User serveur, Timestamp dateHeureCommande, StatutCommande statut, TypeCommande typeCommande, BigDecimal totalHt, BigDecimal totalTtc) {
        this.id = id;
        this.table = table;
        this.serveur = serveur;
        this.dateHeureCommande = dateHeureCommande;
        this.statut = statut;
        this.typeCommande = typeCommande;
        this.totalHt = totalHt;
        this.totalTtc = totalTtc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TableRestaurant getTable() {
        return table;
    }

    public void setTable(TableRestaurant table) {
        this.table = table;
    }

    public User getServeur() {
        return serveur;
    }

    public void setServeur(User serveur) {
        this.serveur = serveur;
    }

    public Timestamp getDateHeureCommande() {
        return dateHeureCommande;
    }

    public void setDateHeureCommande(Timestamp dateHeureCommande) {
        this.dateHeureCommande = dateHeureCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public TypeCommande getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(TypeCommande typeCommande) {
        this.typeCommande = typeCommande;
    }

    public BigDecimal getTotalHt() {
        return totalHt;
    }

    public void setTotalHt(BigDecimal totalHt) {
        this.totalHt = totalHt;
    }

    public BigDecimal getTotalTtc() {
        return totalTtc;
    }

    public void setTotalTtc(BigDecimal totalTtc) {
        this.totalTtc = totalTtc;
    }
}
