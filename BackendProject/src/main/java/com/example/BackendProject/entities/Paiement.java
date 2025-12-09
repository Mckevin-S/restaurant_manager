package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Paiement {
    private Long id;
    private Long commandeId;
    private BigDecimal montant;
    private Object typePaiement;
    private Timestamp datePaiement;
    private String referenceTransaction;

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
    @Column(name = "montant", nullable = false, precision = 2)
    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    @Basic
    @Column(name = "type_paiement", nullable = false)
    public Object getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(Object typePaiement) {
        this.typePaiement = typePaiement;
    }

    @Basic
    @Column(name = "date_paiement", nullable = true)
    public Timestamp getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Timestamp datePaiement) {
        this.datePaiement = datePaiement;
    }

    @Basic
    @Column(name = "reference_transaction", nullable = true, length = 100)
    public String getReferenceTransaction() {
        return referenceTransaction;
    }

    public void setReferenceTransaction(String referenceTransaction) {
        this.referenceTransaction = referenceTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paiement paiement = (Paiement) o;
        return Objects.equals(id, paiement.id) && Objects.equals(commandeId, paiement.commandeId) && Objects.equals(montant, paiement.montant) && Objects.equals(typePaiement, paiement.typePaiement) && Objects.equals(datePaiement, paiement.datePaiement) && Objects.equals(referenceTransaction, paiement.referenceTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commandeId, montant, typePaiement, datePaiement, referenceTransaction);
    }
}
