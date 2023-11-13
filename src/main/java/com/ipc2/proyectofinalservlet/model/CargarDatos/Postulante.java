package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Postulante {
    private int codigo;
    private int codigoOferta;
    private String nombre;
    private String email;
    private String curriculum;
    private String mensaje;
}
