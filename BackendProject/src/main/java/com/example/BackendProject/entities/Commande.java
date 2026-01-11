package com.example.BackendProject.entities;

import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "commande")
public class Commande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableRestaurant table;

    @ManyToOne
    @JoinColumn(name = "serveur_id")
    private Utilisateur serveur;

    private Timestamp dateHeureCommande;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @Enumerated(EnumType.STRING)
    private TypeCommande typeCommande;

    private BigDecimal totalHt;
    private BigDecimal totalTtc;
    private Integer nombrePersonnes;

    @OneToMany(mappedBy = "commande")
    private List<LigneCommande> lignes;

    @OneToMany(mappedBy = "commande")
    private List<Paiement> paiements;

    @ManyToMany
    @JoinTable(
            name = "commande_promotion",
            joinColumns = @JoinColumn(name = "commande_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promotions;

    public Commande(Long id, TableRestaurant table, Utilisateur serveur, Timestamp dateHeureCommande, StatutCommande statut, TypeCommande typeCommande, BigDecimal totalHt, BigDecimal totalTtc, Integer nombrePersonnes, List<LigneCommande> lignes, List<Paiement> paiements, List<Promotion> promotions) {
        this.id = id;
        this.table = table;
        this.serveur = serveur;
        this.dateHeureCommande = dateHeureCommande;
        this.statut = statut;
        this.typeCommande = typeCommande;
        this.totalHt = totalHt;
        this.totalTtc = totalTtc;
        this.nombrePersonnes = nombrePersonnes;
        this.lignes = lignes;
        this.paiements = paiements;
        this.promotions = promotions;
    }

    public Commande() {
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

    public Utilisateur getServeur() {
        return serveur;
    }

    public void setServeur(Utilisateur serveur) {
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

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<Paiement> paiements) {
        this.paiements = paiements;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public Integer getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(Integer nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }
}
