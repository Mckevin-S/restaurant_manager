package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Commande {
    private Long id;
    private Long tableId;
    private Long serveurId;
    private Timestamp dateHeureCommande;
    private Object statut;
    private Object typeCommande;
    private BigDecimal totalHt;
    private BigDecimal totalTtc;

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
    @Column(name = "table_id", nullable = true)
    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    @Basic
    @Column(name = "serveur_id", nullable = true)
    public Long getServeurId() {
        return serveurId;
    }

    public void setServeurId(Long serveurId) {
        this.serveurId = serveurId;
    }

    @Basic
    @Column(name = "date_heure_commande", nullable = true)
    public Timestamp getDateHeureCommande() {
        return dateHeureCommande;
    }

    public void setDateHeureCommande(Timestamp dateHeureCommande) {
        this.dateHeureCommande = dateHeureCommande;
    }

    @Basic
    @Column(name = "statut", nullable = true)
    public Object getStatut() {
        return statut;
    }

    public void setStatut(Object statut) {
        this.statut = statut;
    }

    @Basic
    @Column(name = "type_commande", nullable = true)
    public Object getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(Object typeCommande) {
        this.typeCommande = typeCommande;
    }

    @Basic
    @Column(name = "total_ht", nullable = true, precision = 2)
    public BigDecimal getTotalHt() {
        return totalHt;
    }

    public void setTotalHt(BigDecimal totalHt) {
        this.totalHt = totalHt;
    }

    @Basic
    @Column(name = "total_ttc", nullable = true, precision = 2)
    public BigDecimal getTotalTtc() {
        return totalTtc;
    }

    public void setTotalTtc(BigDecimal totalTtc) {
        this.totalTtc = totalTtc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commande commande = (Commande) o;
        return Objects.equals(id, commande.id) && Objects.equals(tableId, commande.tableId) && Objects.equals(serveurId, commande.serveurId) && Objects.equals(dateHeureCommande, commande.dateHeureCommande) && Objects.equals(statut, commande.statut) && Objects.equals(typeCommande, commande.typeCommande) && Objects.equals(totalHt, commande.totalHt) && Objects.equals(totalTtc, commande.totalTtc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tableId, serveurId, dateHeureCommande, statut, typeCommande, totalHt, totalTtc);
    }
}
