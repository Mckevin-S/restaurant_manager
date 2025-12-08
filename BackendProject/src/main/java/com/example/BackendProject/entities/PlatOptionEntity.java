package com.example.BackendProject.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "plat_option", schema = "restaurant", catalog = "")
@IdClass(PlatOptionEntityPK.class)
public class PlatOptionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "plat_id", nullable = false)
    private Long platId;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "option_id", nullable = false)
    private Long optionId;

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlatOptionEntity that = (PlatOptionEntity) o;
        return Objects.equals(platId, that.platId) && Objects.equals(optionId, that.optionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platId, optionId);
    }
}
