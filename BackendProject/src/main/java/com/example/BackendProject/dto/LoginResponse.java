package com.example.BackendProject.dto;

public class LoginResponse {


        private String token;

        private String nom;
        
        private String role;

        public LoginResponse(String token, String nom, String role) {
                this.token = token;
                this.nom = nom;
                this.role = role;
        }

        public LoginResponse() {
        }


        public String getToken() {
                return token;
        }

        public void setToken(String token) {
                this.token = token;
        }

        public String getNom() {
                return nom;
        }

        public void setNom(String nom) {
                this.nom = nom;
        }

        public String getRole() {
                return role;
        }

        public void setRole(String role) {
                this.role = role;
        }
}
