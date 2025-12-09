package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(columnDefinition = "TEXT")
    private String permissions;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}
