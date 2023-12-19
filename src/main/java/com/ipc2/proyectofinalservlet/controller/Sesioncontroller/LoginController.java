package com.ipc2.proyectofinalservlet.controller.Sesioncontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.CompletarInformacionApplicant;
import com.ipc2.proyectofinalservlet.model.User.Rol;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.model.User.login;
import com.ipc2.proyectofinalservlet.service.CargarDatosService;
import com.ipc2.proyectofinalservlet.service.SesionService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.entity.ContentType;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Objects;

import static java.sql.JDBCType.NULL;

@WebServlet(name = "SesionManagerServlet", urlPatterns = {"/v1/sesion-servlet/*"})
public class LoginController extends HttpServlet {
    private SesionService usuarioService;
    private User usergeneral;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        HttpSession session = req.getSession(true);

        String uri = req.getRequestURI();

        if (uri.endsWith("/cerrar-sesion")) {
            System.out.println("Cerrar Sesion");
            resp.setStatus(HttpServletResponse.SC_OK);
            conectar.desconectar(conexion);
            session.invalidate();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        HttpSession session = req.getSession(true);
        login user = (login) leerJson(resp,req,login.class);

        cargarDatosInicio(conexion,new BigDecimal(150));

        try {
            System.out.println("iniciando sesion");
            if (validarUsuario(conexion,user.getUsername(), user.getPassword(), user.getUsername())!=null) {
                if (null == usergeneral.getCurriculum() && usergeneral.getRol().equals("Solicitante")) {
                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    Rol rol = new Rol(usergeneral.getRol());
                    enviarJson(resp,rol);
                }
                if (null == usergeneral.getMision() && usergeneral.getRol().equals("Empleador")) {
                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    Rol rol = new Rol(usergeneral.getRol());
                    enviarJson(resp,rol);

                }
                else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Rol rol = new Rol(usergeneral.getRol());
                    enviarJson(resp,rol);
                }
            }
            else {
                System.out.println("Usuario no valido ");
                conectar.desconectar(conexion);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            conectar.desconectar(conexion);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        }

    }

    public User validarUsuario(Connection conexion,String username, String password, String email) {
        System.out.println("validar : ");
        usuarioService = new SesionService(conexion);
        usergeneral = usuarioService.obtenerUsuario(username, password, email);
        return usergeneral;
    }
    public Object leerJson(HttpServletResponse resp, HttpServletRequest req , Class clase) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Object valor = objectMapper.readValue(req.getInputStream(), clase);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return valor;
    }

    public void enviarJson(HttpServletResponse resp, Object valor) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.writeValue(resp.getWriter(), valor);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());

    }

    public void cargarDatosInicio(Connection conexion, BigDecimal cantidad){
        CargarDatosService cargarDatosService = new CargarDatosService(conexion);
        if (!cargarDatosService.listarComision()){
            cargarDatosService.crearComision(cantidad);
        }
    }





}
