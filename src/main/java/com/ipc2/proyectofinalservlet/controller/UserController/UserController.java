
package com.ipc2.proyectofinalservlet.controller.UserController;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.EstadoSolicitudPostulante;
import com.ipc2.proyectofinalservlet.model.Employer.Estado;
import com.ipc2.proyectofinalservlet.model.User.Notificaciones;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.EmployerService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "UserManagerServlet", urlPatterns = {"/v1/user-servlet/*"})
public class UserController extends HttpServlet {

    private UserService userService;
    private String username;
    private String password;
    private User usuario;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        UserService userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        String username = parts[0];
        String password = parts[1];


        User user = userService.validarUsuario(conexion, username, password, username);

        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/listar-notificaciones")) {
            List<Notificaciones> notificaciones = listarNotificaciones(conexion, user.getCodigo());
            userService.enviarJson(resp, notificaciones);
            resp.setStatus(HttpServletResponse.SC_OK);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Conexion conexion = new Conexion();
        Connection connection = conexion.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(connection);
        User user = (User) userService.leerJson(resp,req,User.class);

        System.out.println(user);


        if ( user != null && user.getCodigo() != 0){
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

        if (uri.endsWith("/crear-usuario-solicitante")) {
            crearUsuarioSolicitante(connection, user, resp);
        }

        if (uri.endsWith("/crear-usuario-empleador")) {
            crearUsuarioEmpleador(connection, user, resp);

        }


        if (uri.endsWith("/crear-telefonos")) {
            String username = req.getParameter("username");
            System.out.println(username);
            Telefono telefono = (Telefono) userService.leerJson(resp,req, Telefono.class);
            Telefono telefono2 = readJsonTelefonos(resp,req);
            System.out.println(telefono);
            System.out.println(telefono2);
            crearTelefonos(connection, username, telefono);
        }




        if (uri.endsWith("/restablecer-contrasena")) {
            String email = req.getParameter("email");
            restablecerContrasena(connection, email, conexion, resp);

        }

    }

    private Telefono readJsonTelefonos(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Telefono telefono;
        try {
            telefono = objectMapper.readValue(req.getInputStream(), Telefono.class);
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        } catch (IOException e) {
            // Manejar la excepción, imprimir información de depuración, etc.
            e.printStackTrace();
            throw e; // O manejar de otra manera según tus requerimientos
        }

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
    private void crearTelefonos(Connection conexion, String user, Telefono telefono){
        System.out.println("CrearTelefono");
        userService = new UserService(conexion);
        if (user!=null){
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
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        userService.restablecerContrasena(email);

    }

    public List<Notificaciones> listarNotificaciones(Connection conexion, int usuario){
        userService = new UserService(conexion);
        return userService.listarNotificaciones(usuario);
    }


}
