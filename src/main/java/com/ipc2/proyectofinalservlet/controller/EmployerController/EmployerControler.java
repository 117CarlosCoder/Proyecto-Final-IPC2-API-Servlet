package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.Employer.Modalidad;
import com.ipc2.proyectofinalservlet.model.Employer.Modalidades;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

@WebServlet(name = "EmployerManagerServlet", urlPatterns = {"/v1/employer-servlet/*"})
public class EmployerControler extends HttpServlet {

    private EmployerService employerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");

        Connection conexion = (Connection) session.getAttribute("conexion");
        User user = (User) session.getAttribute("user");
        String uri = req.getRequestURI();

        if (uri.endsWith("/cargar-ofertas")) {
            List<Ofertas> ofertas = listarOfertasEmpresas(conexion, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/cargar-ofertas-postulantes")) {
            List<Ofertas> ofertas = listarOfertaPostulaciones(conexion, user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
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
        if (uri.endsWith("/listar-modalidades")) {
            List<Modalidades> modalidad = listarModalidad(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), modalidad);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if(uri.endsWith("/listar-oferta")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            Ofertas oferta = listarOfetaPostulacion(conexion, codigo);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), oferta);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        Connection conexion = (Connection) session.getAttribute("conexion");
        String uri = req.getRequestURI();
        User user = (User) session.getAttribute("user");

        if (uri.endsWith("/completar-informacion")) {
            CompletarInformacionEmployer completarInformacionEmployer = readJson(resp, req);
            completarInformacion(conexion, completarInformacionEmployer.getMision(), completarInformacionEmployer.getVision(), user.getCodigo());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/completar-informacion-tarjeta")) {
            CompletarInformacionEmployerTarjeta completarInformacionEmployerTarjeta = readJsonTarjeta(resp, req);
            completarInformacionTarjeta(conexion, user.getCodigo(), user.getCodigo(), completarInformacionEmployerTarjeta.getTitular(),completarInformacionEmployerTarjeta.getNumero(),completarInformacionEmployerTarjeta.getCodigoSeguridad(),completarInformacionEmployerTarjeta.getFechaExpiracion(), completarInformacionEmployerTarjeta.getCantidad());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/crear-oferta")) {
            Ofertas ofertas = readJsonOferta(resp, req);
            crearOferta(conexion, ofertas.getNombre(),ofertas.getDescripcion(),user.getCodigo(),ofertas.getCategoria(),ofertas.getEstado(),ofertas.getFechaPublicacion(),ofertas.getFechaLimite(),ofertas.getSalario(),ofertas.getModalidad(),ofertas.getUbicacion(),ofertas.getDetalles(),ofertas.getUsuarioElegido());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");

        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();

        User user = (User) session.getAttribute("user");



        if (uri.endsWith("/actualizar-oferta")) {
            Ofertas ofertas = readJsonOferta(resp, req);
            System.out.println("Oferta : "+ ofertas);
            actualizarOfeta(conexion, ofertas.getNombre(),ofertas.getDescripcion(),ofertas.getEmpresa(),ofertas.getCategoria(),ofertas.getEstado(),ofertas.getFechaPublicacion(),ofertas.getFechaLimite(),ofertas.getSalario(),ofertas.getModalidad(),ofertas.getUbicacion(),ofertas.getDetalles(),ofertas.getUsuarioElegido(),ofertas.getCodigo());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");

        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();

        if (uri.endsWith("/eliminar-oferta")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            employerService =new EmployerService(conexion);
            employerService.eliminarOfetas(codigo);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
    }

    private void crearOferta(Connection conexion, String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido){
        employerService = new EmployerService(conexion);
        employerService.crearOferta(nombre,descripcion,empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
    }

    private void actualizarOfeta(Connection conexion, String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido, int codigo){
        employerService = new EmployerService(conexion);
        employerService.actualizarOferta(nombre,descripcion,empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido, codigo);
    }

    private void completarInformacion(Connection conexion, String mision, String vision, int id){
        employerService = new EmployerService(conexion);
        System.out.println("mision : " + mision + " vision : " + vision + " id : " +id );
        employerService.completarInformacion(mision,vision,id);
    }

    private void completarInformacionTarjeta(Connection conexion,int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad, java.sql.Date fechaExpiracion, BigDecimal catidad){
        employerService = new EmployerService(conexion);
        employerService.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad, fechaExpiracion, catidad);
    }

    public List<Ofertas> listarOfertasEmpresas(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarOfertasEmpresa(empresa);
    }

    public List<Ofertas> listarOfertaPostulaciones(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarOfertasEmpresaPos(empresa);
    }
    private CompletarInformacionEmployer readJson(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompletarInformacionEmployer completarInformacionEmployer = objectMapper.readValue(req.getInputStream(), CompletarInformacionEmployer.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return completarInformacionEmployer;
    }

    private CompletarInformacionEmployerTarjeta readJsonTarjeta(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompletarInformacionEmployerTarjeta completarInformacionEmployerTarjeta = objectMapper.readValue(req.getInputStream(), CompletarInformacionEmployerTarjeta.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return completarInformacionEmployerTarjeta;
    }

    private Ofertas readJsonOferta(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Ofertas ofertas = objectMapper.readValue(req.getInputStream(), Ofertas.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return ofertas;
    }
    public List<Categoria> listarCategorias(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarCategorias();
    }

    private Ofertas listarOfetaPostulacion(Connection conexion, int codigo){
        employerService = new EmployerService(conexion);
        return employerService.listarOfetaCodigo(codigo);
    }

    public List<Modalidades> listarModalidad(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarModalidades();
    }
}
