package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


@Entity
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String adresse;
    private String telephone;
    private BigDecimal tauxTva;
    private String devise;

    @Column(name = "date_creation")
    private Timestamp dateCreation;

    @OneToMany(mappedBy = "restaurant")
    private List<Utilisateur> utilisateurs;

    @OneToMany(mappedBy = "restaurant")
    private List<Zone> zones;

    public Restaurant(Long id, String nom, String adresse, String telephone, BigDecimal tauxTva, String devise, Timestamp dateCreation, List<Utilisateur> utilisateurs, List<Zone> zones) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.tauxTva = tauxTva;
        this.devise = devise;
        this.dateCreation = dateCreation;
        this.utilisateurs = utilisateurs;
        this.zones = zones;
    }

    public Restaurant() {
    }

    public Long getId() {
        return id;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BigDecimal getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(BigDecimal tauxTva) {
        this.tauxTva = tauxTva;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<Utilisateur> getUsers() {
        return utilisateurs;
    }

    public void setUsers(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
}
