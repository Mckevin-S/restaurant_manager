package com.example.BackendProject.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cuisinier")
public class Cuisinier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien avec l'utilisateur
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Un cuisinier peut avoir plusieurs recettes
    @OneToMany(mappedBy = "cuisinier", cascade = CascadeType.ALL)
    private List<Recette> recettes;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Recette> getRecettes() {
        return recettes;
    }

    public void setRecettes(List<Recette> recettes) {
        this.recettes = recettes;
    }

}
