package com.ipc2.proyectofinalservlet.controller.Sesioncontroller;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.login;
import com.ipc2.proyectofinalservlet.service.SesionService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "SesionManagerServlet", urlPatterns = {"/v1/sesion-servlet/*"})
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String uri = req.getRequestURI();

        if (uri.endsWith("/cerrar-sesion")) {
            System.out.println("Cerrar Sesion");
            resp.setStatus(HttpServletResponse.SC_OK);
            conectar.desconectar(conexion);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Conexion conexion = new Conexion();
        Connection conectar = conexion.obtenerConexion();
        UserService userService = new UserService(conectar);
        login user = (login) userService.leerJson(resp,req,login.class);

        try {
            System.out.println("iniciando sesion");
            SesionService sesionService = new SesionService(conectar);
            sesionService.Validar(conectar,resp,user,conexion);


        } catch (Exception e) {
            conexion.desconectar(conectar);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        }
    }
}
