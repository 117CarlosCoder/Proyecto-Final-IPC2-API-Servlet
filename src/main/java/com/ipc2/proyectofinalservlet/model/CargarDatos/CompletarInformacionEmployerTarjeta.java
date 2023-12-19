package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompletarInformacionEmployerTarjeta {
    private int codigo;
    private int codigoUsuario;
    private String Titular;
    private int numero;
    private int codigoSeguridad;
    private Date fechaExpiracion;
    private BigDecimal cantidad;
}

