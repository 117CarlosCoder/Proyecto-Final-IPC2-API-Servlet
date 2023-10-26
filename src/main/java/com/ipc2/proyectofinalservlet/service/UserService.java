package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.SesionDB;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.sql.Connection;
import java.util.Optional;

public class UserService  {
    private final SesionDB usuarioDB;

    public UserService(Connection conexion){
        this.usuarioDB = new SesionDB(conexion);
    }
    public Optional<User> obtenerUsuario(String username, String password, String email) {
        System.out.println("Servicio obtener usuario");
        return usuarioDB.obtenerUsuario(username, password, email);
    }
}
