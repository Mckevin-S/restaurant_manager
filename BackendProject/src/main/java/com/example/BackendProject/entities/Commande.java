package com.example.BackendProject.entities;

import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "commande")
public class Commande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableRestaurant table;

    @ManyToOne
    @JoinColumn(name = "serveur_id")
    private User serveur;

    private Timestamp dateHeureCommande;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @Enumerated(EnumType.STRING)
    private TypeCommande typeCommande;

    private BigDecimal totalHt;
    private BigDecimal totalTtc;

    @OneToMany(mappedBy = "commande")
    private List<LigneCommande> lignes;

    @OneToMany(mappedBy = "commande")
    private List<Paiement> paiements;

    @ManyToMany
    @JoinTable(
            name = "commande_promotion",
            joinColumns = @JoinColumn(name = "commande_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promotions;
}
