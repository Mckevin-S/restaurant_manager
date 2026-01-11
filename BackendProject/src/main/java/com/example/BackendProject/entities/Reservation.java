package com.example.BackendProject.entities;

import com.example.BackendProject.utils.StatutReservation;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomClient;

    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private Integer nombrePersonnes;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime heure;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableRestaurant table;

    public Reservation() {}

    public Reservation(Long id, String nomClient, String email, String telephone, Integer nombrePersonnes, LocalDate date, LocalTime heure, StatutReservation statut, String notes, TableRestaurant table) {
        this.id = id;
        this.nomClient = nomClient;
        this.email = email;
        this.telephone = telephone;
        this.nombrePersonnes = nombrePersonnes;
        this.date = date;
        this.heure = heure;
        this.statut = statut;
        this.notes = notes;
        this.table = table;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Integer getNombrePersonnes() { return nombrePersonnes; }
    public void setNombrePersonnes(Integer nombrePersonnes) { this.nombrePersonnes = nombrePersonnes; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeure() { return heure; }
    public void setHeure(LocalTime heure) { this.heure = heure; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public TableRestaurant getTable() { return table; }
    public void setTable(TableRestaurant table) { this.table = table; }
}
