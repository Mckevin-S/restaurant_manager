package com.example.BackendProject.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête pour réinitialiser le mot de passe d'un utilisateur")
public class ResetPasswordRequest {

    @Schema(description = "Nouveau mot de passe (minimum 6 caractères)", example = "resetPassword789", required = true)
    private String newPassword;

    // Constructeurs
    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    // Getters et Setters
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
