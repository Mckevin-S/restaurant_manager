package com.example.BackendProject.dto;

import com.example.BackendProject.entities.LigneCommande;
import com.example.BackendProject.entities.OptionItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



public class OrderItemOptionDto {

    private Long id;
    private Long ligneCommande;
    private Long option;
    private String nomOption;
    private BigDecimal prixSupplementaire;

    public OrderItemOptionDto() {
    }

    public OrderItemOptionDto(Long id, Long ligneCommande, Long option, String nomOption, BigDecimal prixSupplementaire) {
        this.id = id;
        this.ligneCommande = ligneCommande;
        this.option = option;
        this.nomOption = nomOption;
        this.prixSupplementaire = prixSupplementaire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLigneCommande() {
        return ligneCommande;
    }

    public void setLigneCommande(Long ligneCommande) {
        this.ligneCommande = ligneCommande;
    }

    public Long getOption() {
        return option;
    }

    public void setOption(Long option) {
        this.option = option;
    }

    public String getNomOption() {
        return nomOption;
    }

    public void setNomOption(String nomOption) {
        this.nomOption = nomOption;
    }

    public BigDecimal getPrixSupplementaire() {
        return prixSupplementaire;
    }

    public void setPrixSupplementaire(BigDecimal prixSupplementaire) {
        this.prixSupplementaire = prixSupplementaire;
    }
}
