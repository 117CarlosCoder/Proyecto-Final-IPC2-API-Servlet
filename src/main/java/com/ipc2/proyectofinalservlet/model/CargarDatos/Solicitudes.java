package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Solicitudes {
    private int codigo;
    private int codigoOferta;
    private int usuario;
    private String mensaje;
}
