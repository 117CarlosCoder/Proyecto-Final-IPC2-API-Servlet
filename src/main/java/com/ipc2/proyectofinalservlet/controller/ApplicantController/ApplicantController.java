package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.model.Applicant.Filtros;
import com.ipc2.proyectofinalservlet.model.Applicant.Salario;
import com.ipc2.proyectofinalservlet.model.Applicant.Ubicacion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.EmployerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "ApplicantManagerServlet", urlPatterns = {"/v1/applicant-servlet/*"})
public class ApplicantController extends HttpServlet{
    private ApplicantService applicantService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        System.out.println("sesion: "+ session);
        Connection conexion = (Connection) session.getAttribute("conexion");

        System.out.println(conexion);
        User user = (User) session.getAttribute("user");
        System.out.println(user);
        String uri = req.getRequestURI();

        if(uri.endsWith("/listar-ofertas")) {
            List<OfertasEmpresa> ofertas = listarOfertasEmpleo(conexion, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-ofertas-sugerencias")) {
            System.out.println("codigo : " + user.getCodigo());
            List<OfertasEmpresa> ofertas = listarOfertasSugerencia(conexion, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-categorias")) {
            List<Categoria> categorias = listarCategorias(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), categorias);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-ubicaciones")) {
            List<Ubicacion> ubicaciones = listarUbicaciones(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ubicaciones);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-salarios")) {
            List<Salario> salarios = listarSalarios(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), salarios);
            resp.setStatus(HttpServletResponse.SC_OK);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");

        Connection conexion = (Connection) session.getAttribute("conexion");
        User user = (User) session.getAttribute("user");
        String uri = req.getRequestURI();

        if (uri.endsWith("/completar-informacion")) {
                CompletarInformacionApplicant completarInformacionApplicant = readJson(resp, req);
                completarInformacion(conexion, completarInformacionApplicant.getCurriculum(), user.getCodigo());
                for (Integer categoria : completarInformacionApplicant.getCategorias()) {
                    crearCategorias(conexion, user.getCodigo(), categoria);
                }
                resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/completar-informacion-tarjeta")) {
            CompletarInformacionEmployerTarjeta completarInformacionEmployerTarjeta = readJsonTarjeta(resp, req);
            completarInformacionTarjeta(conexion, user.getCodigo(), user.getCodigo(), completarInformacionEmployerTarjeta.getTitular(),completarInformacionEmployerTarjeta.getNumero(),completarInformacionEmployerTarjeta.getCodigoSeguridad(),completarInformacionEmployerTarjeta.getFechaExpiracion());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/aplicar-oferta")) {
            Solicitudes solicitudes = readJsonSolicitud(resp, req);
            aplicarOferta(conexion, solicitudes.getCodigoOferta(), user.getCodigo(), solicitudes.getMensaje());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/buscar-empresa")) {
            Filtros filtros = readJsonFiltros(resp, req);
            System.out.println("Filtros  : "+filtros);
            List<OfertasEmpresa> ofertas = listarOfertasFiltros(conexion, filtros,user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-ofertas-filtros")) {
            Filtros filtros = readJsonFiltros(resp, req);
            System.out.println("Filtros  : "+filtros);
            List<OfertasEmpresa> ofertas = listarOfertasFiltros(conexion, filtros, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    public List<Categoria> listarCategorias(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarCategorias();
    }

    public List<OfertasEmpresa> listarOfertasNombre(Connection conexion, String Nombre, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.lisatarOfertaNombre(Nombre, usuario);
    }

    public List<OfertasEmpresa> listarOfertasSugerencia(Connection conexion, int codigo){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfertasSugerencia(codigo);
    }
    public List<OfertasEmpresa> listarOfertasFiltros(Connection conexion, Filtros filtros, int codigo){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfertasFiltrados(filtros,codigo);
    }

    public List<Salario> listarSalarios(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarSalarios();
    }

    public List<Ubicacion> listarUbicaciones(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarUbicaciones();
    }

    private CompletarInformacionEmployerTarjeta readJsonTarjeta(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompletarInformacionEmployerTarjeta completarInformacionEmployerTarjeta = objectMapper.readValue(req.getInputStream(), CompletarInformacionEmployerTarjeta.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return completarInformacionEmployerTarjeta;
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

    private List<OfertasEmpresa> listarOfertasEmpleo(Connection conexion, int usuarion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfeta(usuarion);
    }
    private void completarInformacionTarjeta(Connection conexion,int codigo, int codigoUsuario, String Titular,int numero,int codigoSeguridad, Date fechaExpiracion){
        applicantService = new ApplicantService(conexion);
        applicantService.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad, fechaExpiracion);
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

    private Filtros readJsonFiltros(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Filtros filtros = objectMapper.readValue(req.getInputStream(), Filtros.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return filtros;
    }
}
