package com.ipc2.proyectofinalservlet.model.Admin;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cobros {
    private int codigo;
    private int usario;
    private BigDecimal cantidad;
    private String fecha;
    private int codigoOferta;
    private String oferta;
    private int categoria;
}
