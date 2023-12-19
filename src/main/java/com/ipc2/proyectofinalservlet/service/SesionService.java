package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.SesionDB;
import com.ipc2.proyectofinalservlet.data.UserDB;
import com.ipc2.proyectofinalservlet.model.User.Rol;
import com.ipc2.proyectofinalservlet.model.User.User;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.util.Optional;

public class SesionService {
    private final SesionDB sesionDB;


    public SesionService(Connection conexion){
        this.sesionDB = new SesionDB(conexion);
    }
    public User obtenerUsuario(String username, String password, String email) {
        System.out.println("Servicio obtener usuario");
        var oUsuario =  sesionDB.obtenerUsuario(username, password, email);
        return oUsuario.get();

    }
}