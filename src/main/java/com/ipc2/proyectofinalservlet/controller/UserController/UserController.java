
package com.ipc2.proyectofinalservlet.controller.UserController;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.SesionService;
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
import java.util.Base64;
import java.util.List;
import java.util.Random;

@WebServlet(name = "UserManagerServlet", urlPatterns = {"/v1/user-servlet/*"})
public class UserController extends HttpServlet {

    private UserService userService;
    private String username;
    private SesionService sesionService;
    private String password;
    private User usuario;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        HttpSession session = req.getSession(true);

        String authorizationHeader = req.getHeader("Authorization");
        autorizacion(authorizationHeader,resp);

        usuario = validarUsuario(conexion,username,password,username);
        if (!usuario.getRol().equals("Administrador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/crear-telefonos")) {
            Telefono telefono = readJsonTelefonos(resp,req);
            if(usuario!=null) {
                crearTelefonos(conexion, usuario, telefono);
            }
        }


        if (uri.endsWith("/crear-usuario-solicitante")) {

            User user = readJson(resp,req);
            if(user!=null){
                if (!comprobarEmail(conexion,user.getEmail())) {
                    session.setAttribute("usuario",user);
                    crearUsuarioSolicitante(conexion, user);
                    System.out.println( session.getAttribute("usuario"));
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                }
            }
            else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        if (uri.endsWith("/crear-usuario-empleador")) {
            User user = readJson(resp,req);
            if(user!=null){
                if (!comprobarEmail(conexion,user.getEmail())) {
                    session.setAttribute("usuario",user);
                    crearUsuarioEmpleador(conexion, user);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                }
            }
            else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
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

        if (uri.endsWith("/cargar-datos")) {

        }


    }



    private User readJson(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(req.getInputStream(), User.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return user;
    }

    private Telefono readJsonTelefonos(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Telefono telefono = objectMapper.readValue(req.getInputStream(), Telefono.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return telefono;
    }

    private void crearUsuarioSolicitante(Connection conexion, User user){
        userService = new UserService(conexion);
        userService.crearUsuarioSolicitante(user,false);

    }
    private void crearTelefonos(Connection conexion, User user, Telefono telefono){
        System.out.println("CrearTelefono");
        userService = new UserService(conexion);
        if ((telefono.getTelefono1() != null)){
            userService.crearTelefono(telefono.getTelefono1(), user);
        }
        if ((telefono.getTelefono2() != null)){
            userService.crearTelefono(telefono.getTelefono2(),user);
        }
        if ((telefono.getTelefono3() != null)){
            userService.crearTelefono(telefono.getTelefono3(),user);
        }


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
    public User validarUsuario(Connection conexion,String username, String password, String email) {
        System.out.println("validar : ");
        sesionService = new SesionService(conexion);
        return sesionService.obtenerUsuario(username, password, email);
    }

    public void autorizacion(String authorizationHeader, HttpServletResponse resp) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);
            username = parts[0];
            password = parts[1];
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        }
        else {
            System.out.println("Usuario no aceptado");
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }

    }

}
