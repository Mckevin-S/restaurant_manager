package com.example.BackendProject.dto;

import com.example.BackendProject.entities.OptionItem;
import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



public class PlatOptionDto {

    private Long platId;
    private Long optionId;
    private Plat plat;
    private OptionItem option;


    public PlatOptionDto(Long platId, Long optionId, Plat plat, OptionItem option) {
        this.platId = platId;
        this.optionId = optionId;
        this.plat = plat;
        this.option = option;
    }

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
