package com.example.BackendProject.dto;

import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.entities.User;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;


@AllArgsConstructor
@Getter
@Setter
public class CommandeDto {

    private Long id;
    private TableRestaurant table;
    private User serveur;
    private Timestamp dateHeureCommande;
    private StatutCommande statut;
    private TypeCommande typeCommande;
    private BigDecimal totalHt;
    private BigDecimal totalTtc;

}
