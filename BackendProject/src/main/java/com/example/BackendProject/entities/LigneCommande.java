package com.example.BackendProject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ligne_commande")
public class LigneCommande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "plat_id")
    private Plat plat;

    private Integer quantite;
    private BigDecimal prixUnitaire;

    @Column(columnDefinition = "TEXT")
    private String notesCuisine;

    @OneToMany(mappedBy = "ligneCommande")
    private List<OrderItemOption> options;

    public LigneCommande(Long id, Commande commande, Plat plat, Integer quantite, BigDecimal prixUnitaire, String notesCuisine, List<OrderItemOption> options) {
        this.id = id;
        this.commande = commande;
        this.plat = plat;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.notesCuisine = notesCuisine;
        this.options = options;
    }

    public LigneCommande() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Plat getPlat() {
        return plat;
    }

    public void setPlat(Plat plat) {
        this.plat = plat;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getNotesCuisine() {
        return notesCuisine;
    }

    public void setNotesCuisine(String notesCuisine) {
        this.notesCuisine = notesCuisine;
    }

    public List<OrderItemOption> getOptions() {
        return options;
    }

    public void setOptions(List<OrderItemOption> options) {
        this.options = options;
    }
}
