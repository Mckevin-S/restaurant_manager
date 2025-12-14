package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Commande;
import com.example.BackendProject.utils.TypePaiement;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PaiementDto {

    private Long id;
    private Commande commande;
    private BigDecimal montant;
    private TypePaiement typePaiement;
    private Timestamp datePaiement;
    private String referenceTransaction;

}
