package com.ipc2.proyectofinalservlet.controller.UserController;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.sql.Connection;
import java.util.Random;

@WebServlet(name = "UserManagerServlet", urlPatterns = {"/v1/user-servlet/*"})
public class UserController extends HttpServlet {

    UserService userService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        HttpSession session = req.getSession();


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        String uri = req.getRequestURI();

        if (uri.endsWith("/crear-usuario-solicitante")) {
            User user = readJson(resp,req);
            if(user!=null){
                crearUsuarioSolicitante(conexion,user);
                conectar.desconectar(conexion);
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        if (uri.endsWith("/crear-usuario-empleador")) {
            User user = readJson(resp,req);
            if(user!=null){
                crearUsuarioEmpleador(conexion,user);
                conectar.desconectar(conexion);
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        if (uri.endsWith("/restablecer-contrasena")) {
                String email = req.getParameter("email");


                if (email!=null){
                    if (comprobarEmail(conexion,email)) {
                        restablecerContrasena(conexion, email);
                        conectar.desconectar(conexion);
                        resp.setStatus(HttpServletResponse.SC_CREATED);
                    }
                }

                else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
        }


    }



    private User readJson(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(req.getInputStream(), User.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return user;
    }

    private void crearUsuarioSolicitante(Connection conexion, User user){
        userService = new UserService(conexion);
        userService.crearUsuarioSolicitante(user);

    }

    private boolean comprobarEmail(Connection conexion,String email){
        userService = new UserService(conexion);
        return userService.comprobarCorreo(email);

    }

    private void crearUsuarioEmpleador(Connection conexion, User user){
        userService = new UserService(conexion);
        userService.crearUsuarioEmpleador(user);

    }

    private void restablecerContrasena(Connection conexion, String email){
        userService = new UserService(conexion);
        userService.restablecerContrasena(email);

    }

}
