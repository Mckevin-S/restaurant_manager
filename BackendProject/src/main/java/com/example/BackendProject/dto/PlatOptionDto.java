package com.example.BackendProject.dto;

import com.example.BackendProject.entities.OptionItem;
import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class PlatOptionDto {

    private Long platId;
    private Long optionId;
    private Plat plat;
    private OptionItem option;

}
