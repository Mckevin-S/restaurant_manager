package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class ZoneDto {

    private Long id;
    private Restaurant restaurant;
    private String nom;
    private String description;

}
