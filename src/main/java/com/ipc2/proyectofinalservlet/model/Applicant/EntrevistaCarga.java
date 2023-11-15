package com.ipc2.proyectofinalservlet.model.Applicant;

import lombok.*;

import java.sql.Date;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntrevistaCarga {
    private int codigo;
    private int usuario;
    private Date fecha;
    private String hora;
    private String ubicacion;
    private String estado;
    private String notas;
}
