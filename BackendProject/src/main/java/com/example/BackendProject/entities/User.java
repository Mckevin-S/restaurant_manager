package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;

    private Date dateEmbauche;
    private Boolean actif = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Serveur serveur;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Manager manager;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Caissier caissier;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cuisinier cuisinier;

    @OneToMany(mappedBy = "serveur")
    private List<Commande> commandes;
}
