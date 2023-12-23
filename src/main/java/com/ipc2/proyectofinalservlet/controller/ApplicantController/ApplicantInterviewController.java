package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdf;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.http.entity.ContentType;
import org.apache.commons.io.IOUtils;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ApplicantInterviewManagerServlet", urlPatterns = {"/v1/applicant-interview-servlet/*"})
public class ApplicantInterviewController extends HttpServlet {

    private ApplicantService applicantService;
    private UserService userService ;
    private String username;
    private String password;
    private User user;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Solicitante")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if(uri.endsWith("/listar-entrevistas")) {
            List<EntrevitaN> entrevistas = listarEntrevitas(conexion,user.getCodigo());
            System.out.println("usuario :" + user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), entrevistas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }



        if(uri.endsWith("/listar-oferta-postulacion")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            OfertasEmpresa oferta = listarOfetaPostulacion(conexion, codigo, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), oferta);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private List<EntrevitaN> listarEntrevitas(Connection conexion, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarEntrevistas(usuario);
    }

    private UsuarioPdf listarCurriculum(Connection conexion, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarCurriculum(usuario);
    }

    private OfertasEmpresa listarOfetaPostulacion(Connection conexion, int codigo,int usuarion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfetaCodigo(codigo, usuarion);
    }
}
