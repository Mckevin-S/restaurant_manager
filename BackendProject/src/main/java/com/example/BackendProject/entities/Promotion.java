package com.example.BackendProject.entities;

import com.example.BackendProject.utils.TypePromotion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    // ENUM('Pourcentage', 'Montant fixe')
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypePromotion type;

    @Column(name = "valeur", precision = 2)
    private BigDecimal valeur;

    @Column(name = "actif")
    private Boolean actif;

    @Column(name = "date_expiration")
    private Date dateExpiration;

    public Promotion(Long id, String nom, TypePromotion type, BigDecimal valeur, Boolean actif, Date dateExpiration) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.valeur = valeur;
        this.actif = actif;
        this.dateExpiration = dateExpiration;
    }

    public Promotion() {
    }

    // ---------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------
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

    public TypePromotion getType() {
        return type;
    }

    public void setType(TypePromotion type) {
        this.type = type;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

}
