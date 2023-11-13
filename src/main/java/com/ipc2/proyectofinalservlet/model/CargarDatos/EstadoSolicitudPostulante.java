package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EstadoSolicitudPostulante {
    private int codigo;
    private int codigoOferta;
    private int usuario;
    private String estado;
    private String nombre;
    private String nombreUsuario;
}
