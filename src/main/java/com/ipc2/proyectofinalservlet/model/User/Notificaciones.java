package com.ipc2.proyectofinalservlet.model.User;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Notificaciones {
    private int codigo;
    private int codigoEmpresa;
    private int codigoUsuario;
    private String mensaje;
}
