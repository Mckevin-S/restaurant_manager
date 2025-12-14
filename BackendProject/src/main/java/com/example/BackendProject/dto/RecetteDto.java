package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class RecetteDto {

    private Long id;
    private Plat plat;
    private String nom;

}
