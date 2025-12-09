package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

@Entity
public class Promotion {
    private Long id;
    private String nom;
    private Object type;
    private BigDecimal valeur;
    private Byte actif;
    private Date dateExpiration;

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
    @Column(name = "nom", nullable = false, length = 100)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Basic
    @Column(name = "type", nullable = true)
    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    @Basic
    @Column(name = "valeur", nullable = true, precision = 2)
    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    @Basic
    @Column(name = "actif", nullable = true)
    public Byte getActif() {
        return actif;
    }

    public void setActif(Byte actif) {
        this.actif = actif;
    }

    @Basic
    @Column(name = "date_expiration", nullable = true)
    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Promotion promotion = (Promotion) o;
        return Objects.equals(id, promotion.id) && Objects.equals(nom, promotion.nom) && Objects.equals(type, promotion.type) && Objects.equals(valeur, promotion.valeur) && Objects.equals(actif, promotion.actif) && Objects.equals(dateExpiration, promotion.dateExpiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, type, valeur, actif, dateExpiration);
    }
}
