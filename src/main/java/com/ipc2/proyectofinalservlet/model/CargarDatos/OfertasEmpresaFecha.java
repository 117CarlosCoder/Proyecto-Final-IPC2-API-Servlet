package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OfertasEmpresaFecha {
    private int codigo;
    private String nombre;
    private String descripcion;
    private String empresa;
    private int categoria;
    private String estado;
    private String fechaPublicacion;
    private Date fechaLimite;
    private BigDecimal salario;
    private String modalidad;
    private String ubicacion;
    private String detalles;
    private int usuarioElegido;
}
