package com.ipc2.proyectofinalservlet.model.Applicant;

import lombok.*;

import java.sql.Date;
import java.sql.Time;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntrevistaOferta {
    private int codigo;
    private int usuario;
    private String fecha;
    private Time hora;
    private String ubicacion;
    private String estado;
    private String notas;
    private int codigoOferta;
    private String nombre;
    private String nombreOferta;
}
