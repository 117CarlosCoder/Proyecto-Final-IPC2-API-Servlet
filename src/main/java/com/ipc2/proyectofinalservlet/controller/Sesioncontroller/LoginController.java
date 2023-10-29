package com.ipc2.proyectofinalservlet.controller.Sesioncontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.Rol;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.model.User.login;
import com.ipc2.proyectofinalservlet.service.CargarDatosService;
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

@WebServlet(name = "SesionManagerServlet", urlPatterns = {"/v1/sesion-servlet/*"})
public class LoginController extends HttpServlet {
    private  UserService usuarioService;
    private User usergeneral;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        String uri = req.getRequestURI();

        if (uri.endsWith("/cerrar-sesion")) {
            conectar.desconectar();
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(3600);

        System.out.println("Sesion Abierta: " + session);
        session.setAttribute("conexion", conexion);

        login user = readJson(resp,req,conexion);
        cargarDatosInicio(conexion,150);

        try {
            System.out.println("iniciando sesion");
            if (validarUsuario(req,user.getUsername(), user.getPassword(), user.getUsername(), session)){
                System.out.println("valido : ");
                System.out.println(usergeneral);
                Rol rol = new Rol(usergeneral.getRol());
                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                objectMapper.writeValue(resp.getWriter(), rol);
                System.out.println(rol);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                System.out.println("no valido");
                conectar.desconectar();
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            conectar.desconectar();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    public boolean validarUsuario(HttpServletRequest req,String username, String password, String email,HttpSession session) {
        System.out.println("validar : ");
        var oUsuario = usuarioService.obtenerUsuario(username, password, email);
        if (oUsuario.isEmpty()) return false;
        usergeneral = oUsuario.get();
        session.setAttribute("user",usergeneral);
        return true ;
    }

    public login readJson(HttpServletResponse resp, HttpServletRequest req , Connection conexion) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        login user = objectMapper.readValue(req.getInputStream(), login.class);
        usuarioService = new UserService(conexion);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return user;
    }

    public void cargarDatosInicio(Connection conexion,int cantidad){
        CargarDatosService cargarDatosService = new CargarDatosService(conexion);
        if (!cargarDatosService.listarComision()){
            cargarDatosService.crearComision(cantidad);
        }
    }



}
