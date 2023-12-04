package com.ipc2.proyectofinalservlet.controller.Sesioncontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.entity.ContentType;
import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

import static java.sql.JDBCType.NULL;

@WebServlet(name = "SesionManagerServlet", urlPatterns = {"/v1/sesion-servlet/*"})
public class LoginController extends HttpServlet {
    private SesionService usuarioService;
    private User usergeneral;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        System.out.println("sesion: "+ session);
        Conexion conectar = new Conexion();
        Connection conexion = (Connection) session.getAttribute("conexion");
        String uri = req.getRequestURI();


        if (uri.endsWith("/cerrar-sesion")) {
            System.out.println("Cerrar Sesion");
            conectar.desconectar(conexion);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        HttpSession session = req.getSession();
        System.out.println("sesion: "+ session);
        session.setMaxInactiveInterval(3600);
        String  sessionid = session.getId();
        //getServletContext().setAttribute("userSession", session);

        System.out.println("Sesion Abierta : " + session);
        session.setAttribute("conexion", conexion);

        login user = readJson(resp,req,conexion);
        cargarDatosInicio(conexion,150);

        try {
            System.out.println("iniciando sesion");
            if (validarUsuario(conexion,user.getUsername(), user.getPassword(), user.getUsername(), session)) {

                if (null == usergeneral.getCurriculum() && usergeneral.getRol().equals("Solicitante")) {

                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    System.out.println(1);
                    System.out.println("valido :");
                    Rol rol = new Rol(usergeneral.getRol(),sessionid);
                    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                    objectMapper.writeValue(resp.getWriter(), rol);
                    resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                    System.out.println(rol);
                    System.out.println(conexion);
                    System.out.println(usergeneral.getCurriculum());

                }
                if (null == usergeneral.getMision() && usergeneral.getRol().equals("Empleador")) {

                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    System.out.println(2);
                    System.out.println("valido :");
                    Rol rol = new Rol(usergeneral.getRol(), sessionid);
                    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                    objectMapper.writeValue(resp.getWriter(), rol);
                    resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                    System.out.println(rol);
                    System.out.println(conexion);
                    System.out.println(usergeneral.getMision());

                } else {
                    System.out.println(3);
                    Rol rol = new Rol(usergeneral.getRol(), sessionid);
                    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                    objectMapper.writeValue(resp.getWriter(), rol);
                    resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
                    System.out.println(rol);
                    System.out.println(conexion);
                    resp.setStatus(HttpServletResponse.SC_OK);
                }


            }
            else {
                System.out.println("no valido ");
                conectar.desconectar(conexion);
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            conectar.desconectar(conexion);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    public boolean validarUsuario(Connection conexion,String username, String password, String email,HttpSession session) {
        System.out.println("validar : ");
        usuarioService = new SesionService(conexion);
        var oUsuario = usuarioService.obtenerUsuario(username, password, email);
        if (oUsuario.isEmpty()) return false;
        usergeneral = oUsuario.get();
        session.setAttribute("user",usergeneral);
        return true ;
    }

    public login readJson(HttpServletResponse resp, HttpServletRequest req , Connection conexion) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        login user = objectMapper.readValue(req.getInputStream(), login.class);
        usuarioService = new SesionService(conexion);
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
