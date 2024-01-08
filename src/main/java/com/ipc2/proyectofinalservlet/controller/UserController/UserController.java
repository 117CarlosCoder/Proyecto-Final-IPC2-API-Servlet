
package com.ipc2.proyectofinalservlet.controller.UserController;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "UserManagerServlet", urlPatterns = {"/v1/user-servlet/*"})
public class UserController extends HttpServlet {

    private UserService userService;
    private String username;
    private String password;
    private User usuario;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Conexion conexion = new Conexion();
        Connection connection = conexion.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(connection);
        User user = (User) userService.leerJson(resp,req,User.class);

        if (user !=  null){
            if (password == null && username == null || password == null || username == null ){
                String[] parts = userService.autorizacion(authorizationHeader,resp);
                username = parts[0];
                password = parts[1];

                usuario = userService.validarUsuario(connection,username,password,username);
                if (!usuario.getRol().equals("Administrador")) {
                    resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return;
                }
            }
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/crear-telefonos")) {
            Telefono telefono = readJsonTelefonos(resp,req);
            crearTelefonos(connection, usuario, telefono);
        }


        if (uri.endsWith("/crear-usuario-solicitante")) {
            crearUsuarioSolicitante(connection, user, resp);
        }

        if (uri.endsWith("/crear-usuario-empleador")) {
            crearUsuarioEmpleador(connection, user, resp);

        }

        if (uri.endsWith("/restablecer-contrasena")) {
            String email = req.getParameter("email");
            restablecerContrasena(connection, email, conexion, resp);

        }

    }

    private Telefono readJsonTelefonos(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Telefono telefono = objectMapper.readValue(req.getInputStream(), Telefono.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return telefono;
    }

    private void crearUsuarioSolicitante(Connection conexion, User user, HttpServletResponse resp){
        userService = new UserService(conexion);
        if(user!=null){
            if (!comprobarEmail(conexion,user.getEmail())) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                userService.crearUsuarioSolicitante(user,false, resp);
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    private void crearTelefonos(Connection conexion, User user, Telefono telefono){
        System.out.println("CrearTelefono");
        userService = new UserService(conexion);
        if (usuario!=null){
            userService.crearTelefono(telefono,user);
        }
    }

    private boolean comprobarEmail(Connection conexion,String email){
        userService = new UserService(conexion);
        return userService.comprobarCorreo(email);

    }

    private void crearUsuarioEmpleador(Connection conexion, User user, HttpServletResponse resp){
        userService = new UserService(conexion);
        if(user!=null){
            if (!comprobarEmail(conexion,user.getEmail())) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                userService.crearUsuarioEmpleador(user);
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }


    }

    private void restablecerContrasena(Connection connection, String email, Conexion conexion, HttpServletResponse resp){
        userService = new UserService(connection);
        if (email!=null){
            if (comprobarEmail(connection,email)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                conexion.desconectar(connection);
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        userService.restablecerContrasena(email);

    }



}
