package com.example.BackendProject.services.implementations;

import com.example.BackendProject.entities.Ingredient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockAlerteService {

    private final SimpMessagingTemplate messagingTemplate;

    public StockAlerteService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void verifierSeuilIngredient(Ingredient ingredient) {
        // BigDecimal : compareTo renvoie -1 si inférieur, 0 si égal, 1 si supérieur
        // On alerte si quantiteActuelle <= seuilAlerte
        if (ingredient.getQuantiteActuelle().compareTo(ingredient.getSeuilAlerte()) <= 0) {

            Map<String, Object> alerte = new HashMap<>();
            alerte.put("id", ingredient.getId());
            alerte.put("nom", ingredient.getNom());
            alerte.put("quantite", ingredient.getQuantiteActuelle());
            alerte.put("unite", ingredient.getUniteMesure());
            alerte.put("status", ingredient.getQuantiteActuelle().compareTo(BigDecimal.ZERO) <= 0 ? "RUPTURE" : "FAIBLE");

            // Envoi en temps réel aux administrateurs/économes
            messagingTemplate.convertAndSend("/topic/stock/alertes", (Object) alerte);
        }
    }
}
