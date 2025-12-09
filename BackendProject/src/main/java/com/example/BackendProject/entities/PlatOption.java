package com.example.BackendProject.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "plat_option")
@IdClass(PlatOptionPK.class)
public class PlatOption {

    @Id
    @Column(name = "plat_id", nullable = false)
    private Long platId;

    @Id
    @Column(name = "option_id", nullable = false)
    private Long optionId;

    // ---------------------------------------------------
    // 🔗 Relations ManyToOne vers Plat et OptionItem
    // ---------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "plat_id", insertable = false, updatable = false)
    private Plat plat;

    @ManyToOne
    @JoinColumn(name = "option_id", insertable = false, updatable = false)
    private OptionItem option;

    // ---------------------------------------------------
    // Getters / Setters
    // ---------------------------------------------------
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

    public Plat getPlat() {
        return plat;
    }

    public void setPlat(Plat plat) {
        this.plat = plat;
    }

    public OptionItem getOption() {
        return option;
    }

    public void setOption(OptionItem option) {
        this.option = option;
    }
}

