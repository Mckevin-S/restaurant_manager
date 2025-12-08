package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "restaurant", catalog = "")
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "restaurant_id", nullable = true)
    private Long restaurantId;
    @Basic
    @Column(name = "role_id", nullable = true)
    private Long roleId;
    @Basic
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    @Basic
    @Column(name = "prenom", nullable = true, length = 100)
    private String prenom;
    @Basic
    @Column(name = "email", nullable = false, length = 120)
    private String email;
    @Basic
    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;
    @Basic
    @Column(name = "telephone", nullable = true, length = 30)
    private String telephone;
    @Basic
    @Column(name = "date_embauche", nullable = true)
    private Date dateEmbauche;
    @Basic
    @Column(name = "actif", nullable = true)
    private Byte actif;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public Date getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(Date dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public Byte getActif() {
        return actif;
    }

    public void setActif(Byte actif) {
        this.actif = actif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(restaurantId, that.restaurantId) && Objects.equals(roleId, that.roleId) && Objects.equals(nom, that.nom) && Objects.equals(prenom, that.prenom) && Objects.equals(email, that.email) && Objects.equals(motDePasse, that.motDePasse) && Objects.equals(telephone, that.telephone) && Objects.equals(dateEmbauche, that.dateEmbauche) && Objects.equals(actif, that.actif);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, restaurantId, roleId, nom, prenom, email, motDePasse, telephone, dateEmbauche, actif);
    }
}
