package com.ipc2.proyectofinalservlet.model.Employer;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {
    private String Titular;
    private BigDecimal numero;
    private int codigoSeguridad;


}
