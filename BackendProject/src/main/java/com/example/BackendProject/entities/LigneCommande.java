package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ligne_commande")
public class LigneCommande {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "plat_id")
    private Plat plat;

    private Integer quantite;
    private BigDecimal prixUnitaire;

    @Column(columnDefinition = "TEXT")
    private String notesCuisine;

    @OneToMany(mappedBy = "ligneCommande")
    private List<OrderItemOption> options;
}
