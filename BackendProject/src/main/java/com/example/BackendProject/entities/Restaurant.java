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
    private String email;
    private String logo;
    private String description;
    private BigDecimal tauxTva;
    
    @Column(name = "frais_service")
    private BigDecimal fraisService;
    
    private String devise;
    
    @Column(columnDefinition = "TEXT")
    private String heuresOuverture;
    
    @Column(name = "devise_symbole")
    private String deviseSymbole;

    @Column(name = "date_creation")
    private Timestamp dateCreation;

    @OneToMany(mappedBy = "restaurant")
    private List<Utilisateur> utilisateurs;

    @OneToMany(mappedBy = "restaurant")
    private List<Zone> zones;

    public Restaurant(Long id, String nom, String adresse, String telephone, String email, String logo, String description, BigDecimal tauxTva, BigDecimal fraisService, String devise, String deviseSymbole, String heuresOuverture, Timestamp dateCreation, List<Utilisateur> utilisateurs, List<Zone> zones) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.logo = logo;
        this.description = description;
        this.tauxTva = tauxTva;
        this.fraisService = fraisService;
        this.devise = devise;
        this.deviseSymbole = deviseSymbole;
        this.heuresOuverture = heuresOuverture;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(BigDecimal tauxTva) {
        this.tauxTva = tauxTva;
    }

    public BigDecimal getFraisService() {
        return fraisService;
    }

    public void setFraisService(BigDecimal fraisService) {
        this.fraisService = fraisService;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getHeuresOuverture() {
        return heuresOuverture;
    }

    public void setHeuresOuverture(String heuresOuverture) {
        this.heuresOuverture = heuresOuverture;
    }

    public String getDeviseSymbole() {
        return deviseSymbole;
    }

    public void setDeviseSymbole(String deviseSymbole) {
        this.deviseSymbole = deviseSymbole;
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
