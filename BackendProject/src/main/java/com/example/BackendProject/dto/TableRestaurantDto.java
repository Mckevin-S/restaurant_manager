package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Zone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class TableRestaurantDto {

    private Long id;
    private Zone zone;
    private String numero;
    private Integer capacite;

}
