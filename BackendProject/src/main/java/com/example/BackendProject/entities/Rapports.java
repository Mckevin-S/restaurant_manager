package com.example.BackendProject.entities;

import jakarta.persistence.*;

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
}
