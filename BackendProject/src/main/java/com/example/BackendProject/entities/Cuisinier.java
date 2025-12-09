package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Cuisinier {
    private Long userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuisinier cuisinier = (Cuisinier) o;
        return Objects.equals(userId, cuisinier.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
