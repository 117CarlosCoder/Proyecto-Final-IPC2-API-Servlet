package com.ipc2.proyectofinalservlet.model.Employer;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaDatos {
    private String Titular;
    private BigDecimal numero;
    private int codigoSeguridad;
    private Date fechaExpiracion;
    private BigDecimal cantidad;
}
