package com.example.BackendProject.dto;

import com.example.BackendProject.entities.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryDto {


    private Long id;
    private Menu menu;
    private String nom;
    private String description;
    private Integer ordreAffichage;

}
