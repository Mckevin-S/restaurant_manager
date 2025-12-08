package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "commande", schema = "restaurant", catalog = "")
public class CommandeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "table_id", nullable = true)
    private Long tableId;
    @Basic
    @Column(name = "serveur_id", nullable = true)
    private Long serveurId;
    @Basic
    @Column(name = "date_heure_commande", nullable = true)
    private Timestamp dateHeureCommande;
    @Basic
    @Column(name = "statut", nullable = true)
    private Object statut;
    @Basic
    @Column(name = "type_commande", nullable = true)
    private Object typeCommande;
    @Basic
    @Column(name = "total_ht", nullable = true, precision = 2)
    private BigDecimal totalHt;
    @Basic
    @Column(name = "total_ttc", nullable = true, precision = 2)
    private BigDecimal totalTtc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Long getServeurId() {
        return serveurId;
    }

    public void setServeurId(Long serveurId) {
        this.serveurId = serveurId;
    }

    public Timestamp getDateHeureCommande() {
        return dateHeureCommande;
    }

    public void setDateHeureCommande(Timestamp dateHeureCommande) {
        this.dateHeureCommande = dateHeureCommande;
    }

    public Object getStatut() {
        return statut;
    }

    public void setStatut(Object statut) {
        this.statut = statut;
    }

    public Object getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(Object typeCommande) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandeEntity that = (CommandeEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(tableId, that.tableId) && Objects.equals(serveurId, that.serveurId) && Objects.equals(dateHeureCommande, that.dateHeureCommande) && Objects.equals(statut, that.statut) && Objects.equals(typeCommande, that.typeCommande) && Objects.equals(totalHt, that.totalHt) && Objects.equals(totalTtc, that.totalTtc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tableId, serveurId, dateHeureCommande, statut, typeCommande, totalHt, totalTtc);
    }
}
