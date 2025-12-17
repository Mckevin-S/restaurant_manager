package com.example.BackendProject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                        // ✅ Endpoints publics (sans token)
                        .requestMatchers("/api/auth/login",
                                "/api/users/**",
                                "/api/categories/**",
                                "/api/menus/**",
                                "/api/plats/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/test/**").permitAll() // Pour les tests

                        // ✅ Endpoints protégés par rôle
//                .requestMatchers("/api/personnel/**").hasAnyRole("ADMIN", "RESPONSSALE_ACCADEMIQUE")
//                .requestMatchers("/api/cours/**").hasAnyRole("ADMIN", "ENSEIGNANT", "RESPONSSALE_ACCADEMIQUE")
//                .requestMatchers("/api/salles/**").authenticated()

                        // ✅ Tous les autres endpoints nécessitent une authentification
                        .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
