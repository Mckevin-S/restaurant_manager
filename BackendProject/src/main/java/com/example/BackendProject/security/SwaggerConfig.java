package com.example.BackendProject.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Gestion Restaurant",
                version = "1.0",
                description = "API REST pour la gestion des utilisateurs et opérations du restaurant",
                contact = @Contact(
                        name = "Groupe 5"
//                        email = "support@restaurant.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:3006", description = "Serveur de développement")
//                @Server(url = "https://api.restaurant.com", description = "Serveur de production")
        }
)
public class SwaggerConfig {

}
