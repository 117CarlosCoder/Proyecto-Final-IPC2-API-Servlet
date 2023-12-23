package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Applicant.EntrevistaOferta;
import com.ipc2.proyectofinalservlet.model.Applicant.RegistroPostulacion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.OfertasEmpresaFecha;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "ApplicantManagerReportsServlet", urlPatterns = {"/v1/applicant-reports-changer-servlet/*"})
public class ApplicantReportsChangerController extends HttpServlet {

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

        if(uri.endsWith("/listar-entrevistas-info")) {
            List<EntrevistaOferta> entrevistaInfos= listarEntrevistaInfo(conexion, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), entrevistaInfos);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-postulaciones-retiradas")) {
            String fechaA = req.getParameter("fechaA");
            String fechaB = req.getParameter("fechaB");
            List<RegistroPostulacion> registroPostulacions= listarRetiradaPostulacion(conexion, user.getCodigo(),fechaA,fechaB);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), registroPostulacions);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/ofertas-fecha")) {
            String fechaA = req.getParameter("fechaA");
            String fechaB = req.getParameter("fechaB");
            String estado = req.getParameter("estado");
            List<OfertasEmpresaFecha> listarOfertasFecha= listarOfertasFecha(conexion, user.getCodigo(), estado,fechaA,fechaB);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), listarOfertasFecha);
            resp.setStatus(HttpServletResponse.SC_OK);
        }


    }

    public List<EntrevistaOferta> listarEntrevistaInfo(Connection conexion, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarEntrevistaInfo(usuario);
    }

    public List<RegistroPostulacion> listarRetiradaPostulacion(Connection conexion, int usuario, String fechaA, String fechaB){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarRegistroPostulacio(usuario, fechaA, fechaB);
    }

    public List<OfertasEmpresaFecha> listarOfertasFecha(Connection conexion,int usuario, String estado, String fechaA, String fechaB){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfertasFecha(usuario,estado,fechaA,fechaB);
    }


}
