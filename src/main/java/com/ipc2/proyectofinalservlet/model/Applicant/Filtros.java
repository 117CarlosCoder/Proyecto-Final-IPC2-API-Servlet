package com.ipc2.proyectofinalservlet.model.Applicant;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Filtros {
    private String nombre;
    private String categoria;
    private String modalidad;
    private String ubicacion;
    private BigDecimal salario;
}
