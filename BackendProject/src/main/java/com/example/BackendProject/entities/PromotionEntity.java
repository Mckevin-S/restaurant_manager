package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "promotion", schema = "restaurant", catalog = "")
public class PromotionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    @Basic
    @Column(name = "type", nullable = true)
    private Object type;
    @Basic
    @Column(name = "valeur", nullable = true, precision = 2)
    private BigDecimal valeur;
    @Basic
    @Column(name = "actif", nullable = true)
    private Byte actif;
    @Basic
    @Column(name = "date_expiration", nullable = true)
    private Date dateExpiration;

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

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public Byte getActif() {
        return actif;
    }

    public void setActif(Byte actif) {
        this.actif = actif;
    }

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
        PromotionEntity that = (PromotionEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(nom, that.nom) && Objects.equals(type, that.type) && Objects.equals(valeur, that.valeur) && Objects.equals(actif, that.actif) && Objects.equals(dateExpiration, that.dateExpiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, type, valeur, actif, dateExpiration);
    }
}
