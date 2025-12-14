package com.example.BackendProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;


@AllArgsConstructor
@Getter
@Setter
public class RapportsDto {

    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private BigDecimal chiffreAffaires;
    private Integer platsVendus;
    private String performanceServeurs;

}
