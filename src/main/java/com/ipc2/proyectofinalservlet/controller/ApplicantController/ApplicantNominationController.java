package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Applicant.RegistroPostulacion;
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

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "ApplicantNominarionManagerServlet", urlPatterns = {"/v1/applicant-nomination-servlet/*"})
public class ApplicantNominationController extends HttpServlet {

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

        if(uri.endsWith("/listar-postulaciones")) {
            List<EstadoSolicitud> solicitudes = listarPostulaciones(conexion, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), solicitudes);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if(uri.endsWith("/listar-oferta-postulacion")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            boolean valor = Boolean.parseBoolean(req.getParameter("valor"));
            OfertasEmpresa oferta = null;
            if (valor){
                oferta = listarOfetaPostulacion(conexion, codigo, user.getCodigo());
            }
            else {
                oferta = listarOfetaPostulacionSinE(conexion, codigo, user.getCodigo());
            }
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), oferta);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        if(uri.endsWith("/resgistrar-retirada-postulacion")) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RegistroPostulacion registroPostulacion = objectMapper.readValue(req.getInputStream(), RegistroPostulacion.class);
            registroRetiroPostulacion(conexion,user.getCodigo(),registroPostulacion.getOferta(), registroPostulacion.getFecha());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        if(uri.endsWith("/eliminar-postulacion")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            eliminarPostulacione(conexion, codigo);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

    }

    private List<EstadoSolicitud> listarPostulaciones(Connection conexion, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarPostulaciones(usuario);
    }

    private OfertasEmpresa listarOfetaPostulacion(Connection conexion, int codigo, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfetaCodigo(codigo,usuario);
    }

    private OfertasEmpresa listarOfetaPostulacionSinE(Connection conexion, int codigo, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfetaCodigoSinEntrevista(codigo,usuario);
    }

    private void registroRetiroPostulacion(Connection conexion, int usuario, String oferta, String fecha){
        applicantService = new ApplicantService(conexion);
        applicantService.registrarPostulacion(usuario,oferta,fecha);
    }

    private void eliminarPostulacione(Connection conexion, int codigo){
        System.out.println("Eliminar postulacion");
        applicantService = new ApplicantService(conexion);
        applicantService.eliminarPostulacion(codigo);
    }
}
