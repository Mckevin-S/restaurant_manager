package com.example.BackendProject.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "caissier")
public class Caissier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien avec l'utilisateur
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}
