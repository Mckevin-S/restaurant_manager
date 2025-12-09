package com.example.BackendProject.entities;

import com.example.BackendProject.utils.StatutTable;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "table_restaurant")
public class TableRestaurant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    private String numero;
    private Integer capacite;

    @Enumerated(EnumType.STRING)
    private StatutTable statut = StatutTable.Libre;

    @OneToMany(mappedBy = "table")
    private List<Commande> commandes;
}
