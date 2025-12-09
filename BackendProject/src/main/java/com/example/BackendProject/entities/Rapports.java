package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

@Entity
public class Rapports {
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private BigDecimal chiffreAffaires;
    private Integer platsVendus;
    private String performanceServeurs;

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
    @Column(name = "date_debut", nullable = true)
    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    @Basic
    @Column(name = "date_fin", nullable = true)
    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    @Basic
    @Column(name = "chiffre_affaires", nullable = true, precision = 2)
    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    @Basic
    @Column(name = "plats_vendus", nullable = true)
    public Integer getPlatsVendus() {
        return platsVendus;
    }

    public void setPlatsVendus(Integer platsVendus) {
        this.platsVendus = platsVendus;
    }

    @Basic
    @Column(name = "performance_serveurs", nullable = true, length = -1)
    public String getPerformanceServeurs() {
        return performanceServeurs;
    }

    public void setPerformanceServeurs(String performanceServeurs) {
        this.performanceServeurs = performanceServeurs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rapports rapports = (Rapports) o;
        return Objects.equals(id, rapports.id) && Objects.equals(dateDebut, rapports.dateDebut) && Objects.equals(dateFin, rapports.dateFin) && Objects.equals(chiffreAffaires, rapports.chiffreAffaires) && Objects.equals(platsVendus, rapports.platsVendus) && Objects.equals(performanceServeurs, rapports.performanceServeurs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateDebut, dateFin, chiffreAffaires, platsVendus, performanceServeurs);
    }
}
