package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.HelloServlet;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.EmployerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "EmployerNominationServlet", urlPatterns = {"/v1/employer-nomination-servlet/*"})

public class NominationController extends HelloServlet {
    private EmployerService employerService;
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");

        Connection conexion = (Connection) session.getAttribute("conexion");

        User user = (User) session.getAttribute("user");

        String uri = req.getRequestURI();

        if (uri.endsWith("/cargar-ofertas")) {
            List<Ofertas> ofertas = listarOfertasEmpresasPostulaciones(conexion, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/cargar-entrevistas")) {
            List<EntrevistaInfo> entrevistas = listarEntrevistas(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), entrevistas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");

        Connection conexion = (Connection) session.getAttribute("conexion");
        int codigo = 0;
        if (req.getParameter("codigo")!=null){
             codigo = Integer.parseInt(req.getParameter("codigo"));
        }

        int oferta = 0;
        if (req.getParameter("oferta")!=null){
            oferta = Integer.parseInt(req.getParameter("oferta"));
        }
        String uri = req.getRequestURI();

        if (uri.endsWith("/cargar-postulantes")) {
            List<EstadoSolicitudPostulante> solicitudes = listarPostulantes(conexion,codigo);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), solicitudes);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/obtener-postulante")) {
            Postulante postulante = obtenerPostulante(conexion,codigo);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            session.setAttribute("postulante",postulante);
            objectMapper.writeValue(resp.getWriter(), postulante);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/generar-entrevista")) {
            Postulante postulante = (Postulante) session.getAttribute("postulante");
            Entrevista entrevista = readJsonEntrevista(resp,req);
            generarEntrevista(conexion,postulante.getCodigo(),postulante.getCodigoOferta(),postulante.getCodigo(),entrevista.getFecha(),entrevista.getHora(),entrevista.getUbicacion());
            actualizarOfertaEstado(conexion,entrevista.getCodigoOferta());
            resp.setStatus(HttpServletResponse.SC_OK);
        }



    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");

        Connection conexion = (Connection) session.getAttribute("conexion");

        int codigo = 0;
        if (req.getParameter("codigo")!=null){
            codigo = Integer.parseInt(req.getParameter("codigo"));
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/finalizar-entrevista")) {
            Entrevista entrevista = readJsonEntrevista(resp,req);
            finalizarEntrevista(conexion,entrevista.getNotas(),entrevista.getUsuario(),entrevista.getCodigo());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    public List<Ofertas> listarOfertasEmpresasPostulaciones(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarOfertasEmpresaPostulaciones(empresa);
    }

    public void actualizarOfertaEstado(Connection conexion, int usuario){
        employerService = new EmployerService(conexion);
        employerService.actualizarOfertaEstado(usuario);
    }

    public List<EstadoSolicitudPostulante> listarPostulantes(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarPostulaciones(empresa);
    }

    public Postulante obtenerPostulante(Connection conexion, int usuario){
        employerService = new EmployerService(conexion);
        return employerService.obtenerPostulante(usuario);
    }

    private void generarEntrevista(Connection conexion, int codigo,int codigoOferta, int usuario, Date fecha, String hora, String ubicacion ){
        employerService = new EmployerService(conexion);
        employerService.generarEntrevista(codigo,codigoOferta,usuario,fecha,hora,ubicacion);
    }

    public List<EntrevistaInfo> listarEntrevistas(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarEntrevistas();
    }

    public void finalizarEntrevista(Connection conexion,String notas,int usuario, int codigo){
        employerService = new EmployerService(conexion);
        employerService.finalizarEntrevista(notas,usuario,codigo);
    }
    private Entrevista readJsonEntrevista(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Entrevista entrevista = objectMapper.readValue(req.getInputStream(), Entrevista.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return entrevista;
    }


}
