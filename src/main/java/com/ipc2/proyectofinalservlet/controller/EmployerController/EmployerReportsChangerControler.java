package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.model.Applicant.EntrevistaFecha;
import com.ipc2.proyectofinalservlet.model.Applicant.Estado;
import com.ipc2.proyectofinalservlet.model.Applicant.Estados;
import com.ipc2.proyectofinalservlet.model.Applicant.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Postulante;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.EmployerService;
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

@WebServlet(name = "EmployerManagerChangerReportsServlet", urlPatterns = {"/v1/employer-reports-changer-servlet/*"})
public class EmployerReportsChangerControler extends HttpServlet {

    private EmployerService employerService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        Connection conexion = (Connection) session.getAttribute("conexion");
        User user = (User) session.getAttribute("user");

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


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    public List<OfertaCostos> ofertaCostos(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarCostosOfertas();
    }

    public List<Estado> listarEstados(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarEstados();
    }

    public List<EntrevistaFecha> entrevistaFechas(Connection conexion, java.sql.Date fecha, int empresa, String estado){
        employerService = new EmployerService(conexion);
        return employerService.listarFechaEntrevista(fecha,empresa, estado);
    }
}
