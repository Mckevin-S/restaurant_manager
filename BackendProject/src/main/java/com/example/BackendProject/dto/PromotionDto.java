package com.example.BackendProject.dto;

import com.example.BackendProject.utils.TypePromotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;



public class PromotionDto {

    private Long id;
    private String nom;
    private TypePromotion type;
    private BigDecimal valeur;
    private Boolean actif;
    private Date dateExpiration;

    public PromotionDto(Long id, String nom, TypePromotion type, BigDecimal valeur, Boolean actif, Date dateExpiration) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.valeur = valeur;
        this.actif = actif;
        this.dateExpiration = dateExpiration;
    }

    public PromotionDto() {

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
