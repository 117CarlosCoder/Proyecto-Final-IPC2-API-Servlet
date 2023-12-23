package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdf;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.Employer.EntrevistaFecha;
import com.ipc2.proyectofinalservlet.model.Employer.Estado;
import com.ipc2.proyectofinalservlet.model.Employer.EstadoOferta;
import com.ipc2.proyectofinalservlet.model.Employer.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.EmployerService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "EmployerManagerChangerReportsServlet", urlPatterns = {"/v1/employer-reports-changer-servlet/*"})
public class EmployerReportsChangerControler extends HttpServlet {

    private EmployerService employerService;

    private ApplicantService applicantService;
    private UserService userService;

    private User user;
    private String username;
    private String password;
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
        if (!user.getRol().equals("Empleador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/ofertas-costos")) {
            List<OfertaCostos> ofertaCostos = ofertaCostos(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ofertaCostos);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-estados")) {
            List<Estado> estados = listarEstados(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), estados);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-estados-oferta")) {
            List<EstadoOferta> estadoOfertas = listarEstadosOfertas(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), estadoOfertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-fechaEntrevista")) {
            System.out.println(req.getParameter("fecha"));
            Date fecha = Date.valueOf(req.getParameter("fecha"));
            String estado = req.getParameter("estado");
            System.out.println(estado);
            List<EntrevistaFecha> estados =entrevistaFechas(conexion,fecha, user.getCodigo(),estado);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), estados);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-fecha-Oferta")) {
            String fechaA = req.getParameter("fechaA");
            String fechaB = req.getParameter("fechaB");
            String estado = req.getParameter("estado");
            System.out.println(fechaA);
            System.out.println(fechaB);
            System.out.println(estado);
            System.out.println(user.getCodigo());
            List<OfertasEmpresaFecha> ofertas =listarOfertasFecha(conexion,fechaA, fechaB,estado, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }




    }


    public List<OfertaCostos> ofertaCostos(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarCostosOfertas();
    }

    public List<Estado> listarEstados(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarEstados();
    }

    public List<EstadoOferta> listarEstadosOfertas(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarEstadosOferta();
    }

    public List<OfertasEmpresaFecha> listarOfertasFecha(Connection conexion, String fechaA, String fechaB, String estado, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarFechaOferta(fechaA, fechaB,estado, empresa);
    }

    public List<EntrevistaFecha> entrevistaFechas(Connection conexion, java.sql.Date fecha, int empresa, String estado){
        employerService = new EmployerService(conexion);
        return employerService.listarFechaEntrevista(fecha,empresa, estado);
    }
}
