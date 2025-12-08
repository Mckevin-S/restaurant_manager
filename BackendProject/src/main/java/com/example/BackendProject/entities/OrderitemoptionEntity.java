package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "orderitemoption", schema = "restaurant", catalog = "")
public class OrderitemoptionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "ligne_commande_id", nullable = true)
    private Long ligneCommandeId;
    @Basic
    @Column(name = "option_id", nullable = true)
    private Long optionId;
    @Basic
    @Column(name = "nom_option", nullable = true, length = 100)
    private String nomOption;
    @Basic
    @Column(name = "prix_supplementaire", nullable = true, precision = 2)
    private BigDecimal prixSupplementaire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLigneCommandeId() {
        return ligneCommandeId;
    }

    public void setLigneCommandeId(Long ligneCommandeId) {
        this.ligneCommandeId = ligneCommandeId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderitemoptionEntity that = (OrderitemoptionEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(ligneCommandeId, that.ligneCommandeId) && Objects.equals(optionId, that.optionId) && Objects.equals(nomOption, that.nomOption) && Objects.equals(prixSupplementaire, that.prixSupplementaire);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ligneCommandeId, optionId, nomOption, prixSupplementaire);
    }
}
