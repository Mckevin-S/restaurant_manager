package com.example.BackendProject.dto;

import com.example.BackendProject.entities.OptionItem;
import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



public class PlatOptionDto {

    private Long platId;
    private Long optionId;


    public PlatOptionDto(Long platId, Long optionId) {
        this.platId = platId;
        this.optionId = optionId;
    }

    public PlatOptionDto() {
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


}
