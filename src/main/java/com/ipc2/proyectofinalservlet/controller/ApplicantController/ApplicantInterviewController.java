package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Entrevista;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Solicitudes;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "ApplicantInterviewManagerServlet", urlPatterns = {"/v1/applicant-interview-servlet/*"})
public class ApplicantInterviewController extends HttpServlet {

    private ApplicantService applicantService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Connection conexion = (Connection) session.getAttribute("conexion");

        User user = (User) session.getAttribute("user");

        String uri = req.getRequestURI();

        if(uri.endsWith("/listar-entrevistas")) {
            List<Entrevista> entrevistas = listarEntrevitas(conexion,user.getCodigo());
            System.out.println("usuario :" + user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), entrevistas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-oferta-postulacion")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            Ofertas oferta = listarOfetaPostulacion(conexion, codigo);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), oferta);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private List<Entrevista> listarEntrevitas(Connection conexion, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarEntrevistas(usuario);
    }

    private Ofertas listarOfetaPostulacion(Connection conexion, int codigo){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfetaCodigo(codigo);
    }
}
