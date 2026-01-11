package com.example.BackendProject.dto;

import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.entities.Utilisateur;
import com.example.BackendProject.utils.StatutCommande;
import com.example.BackendProject.utils.TypeCommande;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class CommandeDto {

    private Long id;
    
    @NotNull(message = "La table est obligatoire")
    private Long tableId;   // Renommé pour correspondre au Mapper
    
    // @NotNull(message = "Le serveur est obligatoire")
    // private Long serveurId; // Renommé pour correspondre au Mapper
    
    private Timestamp dateHeureCommande;
    
    @NotNull(message = "Le statut est obligatoire")
    private StatutCommande statut;
    
    @NotNull(message = "Le type de commande est obligatoire")
    private TypeCommande typeCommande;
    
    @DecimalMin(value = "0.0", message = "Le total HT doit être positif ou nul")
    private BigDecimal totalHt;
    
    @DecimalMin(value = "0.0", message = "Le total TTC doit être positif ou nul")
    private BigDecimal totalTtc;;

    public CommandeDto(Long id, Long tableId, Long serveurId, Timestamp dateHeureCommande, StatutCommande statut, TypeCommande typeCommande, BigDecimal totalHt, BigDecimal totalTtc) {
        this.id = id;
        this.tableId = tableId;
        // this.serveurId = serveurId;
        this.dateHeureCommande = dateHeureCommande;
        this.statut = statut;
        this.typeCommande = typeCommande;
        this.totalHt = totalHt;
        this.totalTtc = totalTtc;
    }

    public CommandeDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    // public Long getServeurId() {
    //     return serveurId;
    // }

    // public void setServeurId(Long serveurId) {
    //     this.serveurId = serveurId;
    // }

    public Timestamp getDateHeureCommande() {
        return dateHeureCommande;
    }

    public void setDateHeureCommande(Timestamp dateHeureCommande) {
        this.dateHeureCommande = dateHeureCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public TypeCommande getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(TypeCommande typeCommande) {
        this.typeCommande = typeCommande;
    }

    public BigDecimal getTotalHt() {
        return totalHt;
    }

    public void setTotalHt(BigDecimal totalHt) {
        this.totalHt = totalHt;
    }

    public BigDecimal getTotalTtc() {
        return totalTtc;
    }

    public void setTotalTtc(BigDecimal totalTtc) {
        this.totalTtc = totalTtc;
    }
}
