package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Ofertas {
    private int codigo;
    private String nombre;
    private String descripcion;
    private int empresa;
    private int categoria;
    private String estado;
    private Date fechaPublicacion;
    private Date fechaLimite;
    private BigDecimal salario;
    private String modalidad;
    private String ubicacion;
    private String detalles;
    private int usuarioElegido;
}
