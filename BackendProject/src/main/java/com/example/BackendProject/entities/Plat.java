package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "plat")
public class Plat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal prix;
    private String photoUrl;
    private Boolean disponibilite = true;

    @ManyToMany
    @JoinTable(
            name = "plat_option",
            joinColumns = @JoinColumn(name = "plat_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<OptionItem> options;

    @OneToOne(mappedBy = "plat")
    private Recette recette;
}
