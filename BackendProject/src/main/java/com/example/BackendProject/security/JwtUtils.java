package com.example.BackendProject.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // ✅ Lecture depuis application.properties
    @Value("${jwt.secret:MonSuperSecretJWTKeyQuiFaitAuMoins32Caracteres123456789}")
    private String SECRET;

    @Value("${jwt.expiration:86400000}")
    private long EXPIRATION; // 24h par défaut

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Extrait le username du token JWT.
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expiré", e);
        } catch (JwtException e) {
            throw new RuntimeException("Token invalide", e);
        }
    }

    /**
     * Extrait la date d'expiration du token.
     */
    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    /**
     * Vérifie si le token est expiré.
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Génère un token JWT pour l'utilisateur.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valide le token : vérifie le username ET l'expiration.
     */
    public boolean validateToken(String token, UserDetails user) {
        try {
            String username = extractUsername(token);
            boolean usernameMatch = username.equals(user.getUsername());
            boolean notExpired = !isTokenExpired(token);

            return usernameMatch && notExpired;
        } catch (Exception e) {
            return false;
        }
    }
}
