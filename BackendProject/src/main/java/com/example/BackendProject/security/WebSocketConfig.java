package com.example.BackendProject.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // C'est cette annotation qui crée le bean SimpMessagingTemplate
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Active un broker simple en mémoire pour renvoyer les messages aux clients
        config.enableSimpleBroker("/topic");

        // Préfixe pour les messages envoyés du client (Front-end) vers le serveur (Back-end)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point de connexion pour le Front-end (ex: React ou Angular)
        registry.addEndpoint("/ws-restaurant")
                .setAllowedOriginPatterns("*") // Important pour les tests sur réseau local au Cameroun
                .withSockJS();
    }
}
