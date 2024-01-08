package com.ipc2.proyectofinalservlet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.data.SesionDB;
import com.ipc2.proyectofinalservlet.data.UserDB;
import com.ipc2.proyectofinalservlet.model.User.Rol;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.model.User.login;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

public class SesionService {
    private final SesionDB sesionDB;

    private UserService userService;
    private User usergeneral;

    public SesionService(Connection conexion){
        this.sesionDB = new SesionDB(conexion);
    }
    public User obtenerUsuario(String username, String password, String email) {
        System.out.println("Servicio obtener usuario");
        var oUsuario =  sesionDB.obtenerUsuario(username, password, email);
        return oUsuario.get();
    }

    public void habilitarUsuario(HttpServletResponse resp, Connection conexion) throws IOException {


        if (null == usergeneral.getCurriculum() && usergeneral.getRol().equals("Solicitante")) {
            resp.setStatus(HttpServletResponse.SC_OK);
            Roles(resp,conexion);
        }
        if (null == usergeneral.getMision() && usergeneral.getRol().equals("Empleador")) {
            resp.setStatus(HttpServletResponse.SC_OK);
            Roles(resp, conexion);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            Roles(resp, conexion);
        }
    }

    public void Roles(HttpServletResponse resp, Connection conexion) throws IOException {
        Rol rol = new Rol(usergeneral.getRol());
        System.out.println("rol : "+ rol);
        userService = new UserService(conexion);
        userService.enviarJson(resp,rol);
    }



    public void Validar(Connection conexion, HttpServletResponse resp, login user, Conexion conectar) throws IOException {
        if (validarUsuario(user.getUsername(), user.getPassword(), user.getUsername())!=null) {
            habilitarUsuario(resp, conexion);
        }
        else {
            System.out.println("Usuario no valido ");
            conectar.desconectar(conexion);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public User validarUsuario(String username, String password, String email) {
        System.out.println("validar : ");
        usergeneral = obtenerUsuario(username, password, email);
        return usergeneral;
    }

}