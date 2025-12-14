package com.example.BackendProject.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class PlatOptionPKDto {

    private Long platId;
    private Long optionId;

    public PlatOptionPKDto(Long platId, Long optionId) {
        this.platId = platId;
        this.optionId = optionId;
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
