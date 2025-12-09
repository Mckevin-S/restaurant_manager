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
    private List<User> users;

    @OneToMany(mappedBy = "restaurant")
    private List<Zone> zones;
}
