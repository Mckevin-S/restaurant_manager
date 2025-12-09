package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "serveur")
public class Serveur {
    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}

