package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
@Setter
public class OptionItemDto {

    private Long id;
    private String nom;
    private BigDecimal prixSupplementaire;

}
