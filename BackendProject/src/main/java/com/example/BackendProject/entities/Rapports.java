package com.example.BackendProject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "rapports")
public class Rapports {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut;
    private Date dateFin;
    private BigDecimal chiffreAffaires;
    private Integer platsVendus;

    @Column(columnDefinition = "TEXT")
    private String performanceServeurs;

    public Rapports(Long id, Date dateDebut, Date dateFin, BigDecimal chiffreAffaires, Integer platsVendus, String performanceServeurs) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.chiffreAffaires = chiffreAffaires;
        this.platsVendus = platsVendus;
        this.performanceServeurs = performanceServeurs;
    }


    public Rapports() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public Integer getPlatsVendus() {
        return platsVendus;
    }

    public void setPlatsVendus(Integer platsVendus) {
        this.platsVendus = platsVendus;
    }

    public String getPerformanceServeurs() {
        return performanceServeurs;
    }

    public void setPerformanceServeurs(String performanceServeurs) {
        this.performanceServeurs = performanceServeurs;
    }
}
