package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
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

@WebServlet(name = "ApplicantManagerServlet", urlPatterns = {"/v1/applicant-servlet/*"})
public class ApplicantController extends HttpServlet{
    private ApplicantService applicantService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Connection conexion = (Connection) session.getAttribute("conexion");

        User user = (User) session.getAttribute("user");

        String uri = req.getRequestURI();

        if(uri.endsWith("/listar-ofertas")) {
            List<Ofertas> ofertas = listarOfertasEmpleo(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Connection conexion = (Connection) session.getAttribute("conexion");

        User user = (User) session.getAttribute("user");

        String uri = req.getRequestURI();

        if (uri.endsWith("/completar-informacion")) {
            CompletarInformacionApplicant completarInformacionApplicant = readJson(resp, req);
            completarInformacion(conexion, completarInformacionApplicant.getCurriculum(),user.getCodigo());
            for (Integer categoria:completarInformacionApplicant.getCategorias()) {
                crearCategorias(conexion,user.getCodigo(),categoria);
            }
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/aplicar-oferta")) {
            Solicitudes solicitudes = readJsonSolicitud(resp, req);
            aplicarOferta(conexion, solicitudes.getCodigoOferta(), user.getCodigo(), solicitudes.getMensaje());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
    }

    private void completarInformacion(Connection conexion, String curriculum, int usuario){
        applicantService = new ApplicantService(conexion);
        applicantService.completarInformacion(curriculum,usuario);
    }

    private void crearCategorias(Connection conexion, int usuario, int categoria){
        applicantService = new ApplicantService(conexion);
        applicantService.crearCategoria(usuario,categoria);
    }

    private void aplicarOferta(Connection conexion, int oferta, int usuario, String mensaje){
        applicantService = new ApplicantService(conexion);
        applicantService.aplicarOferta(oferta,usuario,mensaje);
    }

    private List<Ofertas> listarOfertasEmpleo(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfeta();
    }
    private void completarInformacionTarjeta(Connection conexion,int codigo, int codigoUsuario, String Titular,int numero,int codigoSeguridad){
        //employerService = new EmployerService(conexion);
//employerService.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad);
    }

    private CompletarInformacionApplicant readJson(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompletarInformacionApplicant completarInformacionApplicant = objectMapper.readValue(req.getInputStream(), CompletarInformacionApplicant.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return completarInformacionApplicant;
    }

    private Solicitudes readJsonSolicitud(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Solicitudes solicitudes = objectMapper.readValue(req.getInputStream(), Solicitudes.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return solicitudes;
    }
}
