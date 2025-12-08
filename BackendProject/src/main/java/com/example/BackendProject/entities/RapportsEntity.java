package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "rapports", schema = "restaurant", catalog = "")
public class RapportsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "date_debut", nullable = true)
    private Date dateDebut;
    @Basic
    @Column(name = "date_fin", nullable = true)
    private Date dateFin;
    @Basic
    @Column(name = "chiffre_affaires", nullable = true, precision = 2)
    private BigDecimal chiffreAffaires;
    @Basic
    @Column(name = "plats_vendus", nullable = true)
    private Integer platsVendus;
    @Basic
    @Column(name = "performance_serveurs", nullable = true, length = -1)
    private String performanceServeurs;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RapportsEntity that = (RapportsEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(dateDebut, that.dateDebut) && Objects.equals(dateFin, that.dateFin) && Objects.equals(chiffreAffaires, that.chiffreAffaires) && Objects.equals(platsVendus, that.platsVendus) && Objects.equals(performanceServeurs, that.performanceServeurs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateDebut, dateFin, chiffreAffaires, platsVendus, performanceServeurs);
    }
}
