package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "paiement", schema = "restaurant", catalog = "")
public class PaiementEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "commande_id", nullable = true)
    private Long commandeId;
    @Basic
    @Column(name = "montant", nullable = false, precision = 2)
    private BigDecimal montant;
    @Basic
    @Column(name = "type_paiement", nullable = false)
    private Object typePaiement;
    @Basic
    @Column(name = "date_paiement", nullable = true)
    private Timestamp datePaiement;
    @Basic
    @Column(name = "reference_transaction", nullable = true, length = 100)
    private String referenceTransaction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Object getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(Object typePaiement) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaiementEntity that = (PaiementEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(commandeId, that.commandeId) && Objects.equals(montant, that.montant) && Objects.equals(typePaiement, that.typePaiement) && Objects.equals(datePaiement, that.datePaiement) && Objects.equals(referenceTransaction, that.referenceTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commandeId, montant, typePaiement, datePaiement, referenceTransaction);
    }
}
