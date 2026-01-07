package com.example.BackendProject.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Classe utilitaire pour extraire les informations de contexte pour la journalisation
 */
public class LoggingUtils {

    /**
     * Récupère l'identité de l'utilisateur actuellement authentifié
     * @return Le nom d'utilisateur ou "ANONYME" si non authentifié
     */
    public static String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "ANONYME";
    }

    /**
     * Récupère l'adresse IP du client depuis la requête HTTP
     * @param request La requête HTTP
     * @return L'adresse IP du client ou "INCONNUE" si non disponible
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "INCONNUE";
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Si plusieurs IPs (proxies), prendre la première
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress != null ? ipAddress : "INCONNUE";
    }

    /**
     * Formate les informations de contexte pour les logs
     * @param request La requête HTTP
     * @return Une chaîne formatée avec l'utilisateur et l'IP
     */
    public static String getLogContext(HttpServletRequest request) {
        String user = getCurrentUser();
        String ip = getClientIpAddress(request);
        return String.format("[Utilisateur: %s, IP: %s]", user, ip);
    }

    /**
     * Formate les informations de contexte pour les logs sans HttpServletRequest (pour les services)
     * @return Une chaîne formatée avec l'utilisateur seulement
     */
    public static String getLogContext() {
        String user = getCurrentUser();
        return String.format("[Utilisateur: %s]", user);
    }
}
