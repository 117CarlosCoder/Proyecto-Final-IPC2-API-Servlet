package com.ipc2.proyectofinalservlet.model.Admin;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {
    private int cantidad_empleadores;
    private int cantidad_solicitantes;
    private BigDecimal cantidad_cobros;
    private int total_vistas;

}
