package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class EntrevitaN {
    private int codigo;
    private int usuario;
    private String fecha;
    private String hora;
    private String ubicacion;
    private String estado;
    private String notas;
    private int codigoOferta;
    private String empresa;
}
