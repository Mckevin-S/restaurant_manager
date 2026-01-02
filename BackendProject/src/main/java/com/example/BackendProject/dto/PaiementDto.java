package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.utils.TypePaiement;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class PaiementDto {

    private Long id;
    private Long commande;
    private BigDecimal montant;
    private TypePaiement typePaiement;
    private Timestamp datePaiement;
    private String referenceTransaction;

    public PaiementDto() {
    }

    public PaiementDto(Long id, Long commande, BigDecimal montant, TypePaiement typePaiement, Timestamp datePaiement, String referenceTransaction) {
        this.id = id;
        this.commande = commande;
        this.montant = montant;
        this.typePaiement = typePaiement;
        this.datePaiement = datePaiement;
        this.referenceTransaction = referenceTransaction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommande() {
        return commande;
    }

    public void setCommande(Long commande) {
        this.commande = commande;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public TypePaiement getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(TypePaiement typePaiement) {
        this.typePaiement = typePaiement;
    }

    public Timestamp getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Timestamp datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getReferenceTransaction() {
        return referenceTransaction;
    }

    public void setReferenceTransaction(String referenceTransaction) {
        this.referenceTransaction = referenceTransaction;
    }
}
