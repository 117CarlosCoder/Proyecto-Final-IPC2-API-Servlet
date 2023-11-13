package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntrevistaInfo {
    private int codigo;
    private int usuario;
    private Date fecha;
    private Time hora;
    private String ubicacion;
    private String estado;
    private String notas;
    private int codigoOferta;
    private String nombre;
    private String nombreOferta;
}
