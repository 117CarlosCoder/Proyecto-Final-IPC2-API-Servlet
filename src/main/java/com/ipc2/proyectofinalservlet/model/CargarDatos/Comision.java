package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comision {
    private int codigo;
    private BigDecimal cantidad;
}
