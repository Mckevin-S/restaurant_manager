package com.example.BackendProject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class RestaurantDto {

    private Long id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private String logo;
    private String description;
    private BigDecimal tauxTva;
    private BigDecimal fraisService;
    private String devise;
    private Object heuresOuverture;
    private String deviseSymbole;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Timestamp dateCreation;


    public RestaurantDto(Long id, String nom, String adresse, String telephone, String email, String logo, String description, BigDecimal tauxTva, BigDecimal fraisService, String devise, String deviseSymbole, Object heuresOuverture, Timestamp dateCreation) {
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
    }

    public RestaurantDto() {

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

    public Object getHeuresOuverture() {
        return heuresOuverture;
    }

    public void setHeuresOuverture(Object heuresOuverture) {
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
}
