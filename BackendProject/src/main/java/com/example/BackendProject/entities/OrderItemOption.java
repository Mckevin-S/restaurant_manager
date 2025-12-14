package com.example.BackendProject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orderItemoption")
public class OrderItemOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ligne_commande_id")
    private LigneCommande ligneCommande;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private OptionItem option;

    private String nomOption;
    private BigDecimal prixSupplementaire;

    public OrderItemOption(Long id, LigneCommande ligneCommande, OptionItem option, String nomOption, BigDecimal prixSupplementaire) {
        this.id = id;
        this.ligneCommande = ligneCommande;
        this.option = option;
        this.nomOption = nomOption;
        this.prixSupplementaire = prixSupplementaire;
    }

    public OrderItemOption() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LigneCommande getLigneCommande() {
        return ligneCommande;
    }

    public void setLigneCommande(LigneCommande ligneCommande) {
        this.ligneCommande = ligneCommande;
    }

    public OptionItem getOption() {
        return option;
    }

    public void setOption(OptionItem option) {
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
