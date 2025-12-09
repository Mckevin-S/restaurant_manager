package com.example.BackendProject.entities;

import java.io.Serializable;
import java.util.Objects;

public class PlatOptionPK implements Serializable {

    private Long platId;
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


}
