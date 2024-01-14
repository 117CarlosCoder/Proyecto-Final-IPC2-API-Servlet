package com.ipc2.proyectofinalservlet.model.Employer;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntrevistaFecha {
    private int codigo;
    private String Oferta;
    private String fecha;
    private String Empresa;
    private String estado;
    private String usuario;
}
