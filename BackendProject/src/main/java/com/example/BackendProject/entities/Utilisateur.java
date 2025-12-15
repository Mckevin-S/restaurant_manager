package com.example.BackendProject.entities;


import com.example.BackendProject.utils.RoleType;
import jakarta.persistence.*;

import java.time.LocalDate;

import java.util.List;

@Entity
@Table(name = "user", schema = "restaurant")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -----------------------------
    // 🔗 RESTAURANT
    // -----------------------------
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    // -----------------------------
    // 🔐 ROLE
    // -----------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private RoleType role;

    // -----------------------------
    // INFOS USER
    // -----------------------------
    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(length = 30)
    private String telephone;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Column
    private Boolean actif = true;

    // -----------------------------
    // 🔗 COMMANDES (serveur)
    // -----------------------------
    @OneToMany(mappedBy = "serveur")
    private List<Commande> commandes;

    // -----------------------------
    // GETTERS / SETTERS
    // -----------------------------
    public Long getId() {
        return id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public Utilisateur(Long id, Restaurant restaurant, RoleType role, String nom, String prenom, String email, String motDePasse, String telephone, LocalDate dateEmbauche, Boolean actif, List<Commande> commandes) {
        this.id = id;
        this.restaurant = restaurant;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.dateEmbauche = dateEmbauche;
        this.actif = actif;
        this.commandes = commandes;
    }

    public Utilisateur() {
    }
}
