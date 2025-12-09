package com.example.BackendProject.entities;

import com.example.BackendProject.utils.TypePaiement;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "paiement")
public class Paiement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    private TypePaiement typePaiement;

    private Timestamp datePaiement;
    private String referenceTransaction;
}
