package com.example.BackendProject.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête pour changer le mot de passe d'un utilisateur")
public class ChangePasswordRequest {

    @Schema(description = "Ancien mot de passe", example = "oldPassword123", required = true)
    private String oldPassword;

    @Schema(description = "Nouveau mot de passe (minimum 6 caractères)", example = "newPassword456", required = true)
    private String newPassword;

    // Constructeurs
    public ChangePasswordRequest() {}

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getters et Setters
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
