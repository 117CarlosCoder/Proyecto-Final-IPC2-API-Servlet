package com.ipc2.proyectofinalservlet.model.Admin;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IngresoTotal {
    private int codigo;
    private String nombre;
    private int cantidadOfertas;
    private BigDecimal total;
}
