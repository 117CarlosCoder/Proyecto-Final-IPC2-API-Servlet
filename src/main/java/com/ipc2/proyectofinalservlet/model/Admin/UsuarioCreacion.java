package com.ipc2.proyectofinalservlet.model.Admin;

import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreacion {
    private User usuario;
    private Telefono telefonos;
}
