package com.ipc2.proyectofinalservlet.model.Admin;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegistroComision {
    private int codigo;
    private BigDecimal comision;
    private String fecha;
}
