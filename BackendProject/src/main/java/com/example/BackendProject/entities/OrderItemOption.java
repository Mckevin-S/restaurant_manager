package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orderItemoption")
public class OrderItemOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ligne_commande_id")
    private LigneCommande ligneCommande;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private OptionItem option;

    private String nomOption;
    private BigDecimal prixSupplementaire;
}
