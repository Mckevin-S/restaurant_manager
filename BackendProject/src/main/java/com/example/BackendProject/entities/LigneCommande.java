package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ligne_commande", schema = "restaurant", catalog = "")
public class LigneCommande {
    private Long id;
    private Long commandeId;
    private Long platId;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private String notesCuisine;

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
    @Column(name = "commande_id", nullable = true)
    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    @Basic
    @Column(name = "plat_id", nullable = true)
    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    @Basic
    @Column(name = "quantite", nullable = true)
    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    @Basic
    @Column(name = "prix_unitaire", nullable = false, precision = 2)
    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    @Basic
    @Column(name = "notes_cuisine", nullable = true, length = -1)
    public String getNotesCuisine() {
        return notesCuisine;
    }

    public void setNotesCuisine(String notesCuisine) {
        this.notesCuisine = notesCuisine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LigneCommande that = (LigneCommande) o;
        return Objects.equals(id, that.id) && Objects.equals(commandeId, that.commandeId) && Objects.equals(platId, that.platId) && Objects.equals(quantite, that.quantite) && Objects.equals(prixUnitaire, that.prixUnitaire) && Objects.equals(notesCuisine, that.notesCuisine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commandeId, platId, quantite, prixUnitaire, notesCuisine);
    }
}
