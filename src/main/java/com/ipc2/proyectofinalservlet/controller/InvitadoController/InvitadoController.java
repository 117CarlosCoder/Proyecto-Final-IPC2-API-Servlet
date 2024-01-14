package com.ipc2.proyectofinalservlet.controller.InvitadoController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.data.InvitadoDB;
import com.ipc2.proyectofinalservlet.model.Applicant.Filtros;
import com.ipc2.proyectofinalservlet.model.Applicant.Salario;
import com.ipc2.proyectofinalservlet.model.Applicant.Ubicacion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.OfertasEmpresa;
import com.ipc2.proyectofinalservlet.model.Employer.Modalidades;
import com.ipc2.proyectofinalservlet.model.Invitado.OfertaEmpresaInvitado;
import com.ipc2.proyectofinalservlet.model.Invitado.OfertaInformacion;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.EmployerService;
import com.ipc2.proyectofinalservlet.service.InvitadoService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;




@WebServlet(name = "GuestServlet", urlPatterns = {"/v1/guest-servlet/*"})
public class InvitadoController extends HttpServlet {

    private ApplicantService applicantService;
    private InvitadoService invitadoService;

    private int codigo;

    private boolean valor;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String uri = req.getRequestURI();
        UserService userService = new UserService(conexion);
        invitadoService = new InvitadoService(conexion);
        invitadoService.crearVista();


        if (uri.endsWith("/listar-categorias")) {
            List<Categoria> categorias = listarCategorias(conexion);
            userService.enviarJson(resp,categorias);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-salarios")) {
            List<Salario> salarios = listarSalarios(conexion);
            userService.enviarJson(resp,salarios);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-ubicaciones")) {
            List<Ubicacion> ubicaciones = listarUbicaciones(conexion);
            userService.enviarJson(resp,ubicaciones);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-modalidades")) {
            List<Modalidades> modalidad = listarModalidad(conexion);
            userService.enviarJson(resp,modalidad);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-ofertas")) {
            List<OfertasEmpresa> ofertas = listarOfertasEmpleo(conexion);
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-empresa")) {
            obtenerParamtros(req);
            OfertaInformacion ofertas = listarEmpresa(conexion, codigo);
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-ofertas-empresa")) {
            obtenerParamtros(req);
            List<OfertasEmpresa> ofertas = listarOfertasEmpleoEmpresa(conexion,codigo);
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-oferta")) {
            obtenerParamtros(req);
            OfertaEmpresaInvitado oferta ;
            oferta = listarOfetaPostulacionSinE(conexion, codigo);
            userService.enviarJson(resp,oferta);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/actualizar-vista")) {
            invitadoService = new InvitadoService(conexion);
            invitadoService.actulizarVista();
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String uri = req.getRequestURI();
        UserService userService = new UserService(conexion);


        if (uri.endsWith("/buscar-empresa")) {
            Filtros filtros = (Filtros) userService.leerJson(resp, req, Filtros.class);
            if (filtros == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Filtros  : "+filtros);
            List<OfertasEmpresa> ofertas = listarOfertasFiltros(conexion, filtros);
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-ofertas-filtros")) {
            Filtros filtros = (Filtros) userService.leerJson(resp, req, Filtros.class);
            if (filtros == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Filtros  : "+filtros);
            List<OfertasEmpresa> ofertas = listarOfertasFiltros(conexion, filtros);
            userService.enviarJson(resp, ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

    }

    public List<Categoria> listarCategorias(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarCategorias();
    }
    public List<OfertasEmpresa> listarOfertasFiltros(Connection conexion, Filtros filtros){
        invitadoService = new InvitadoService(conexion);
        return invitadoService.listarOfertasFiltrados(filtros);
    }

    public List<Salario> listarSalarios(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarSalarios();
    }

    public OfertaInformacion listarEmpresa(Connection conexion, int empresa){
        invitadoService = new InvitadoService(conexion);
        return invitadoService.listarEmpresa(empresa);
    }

    public List<Modalidades> listarModalidad(Connection conexion){
        EmployerService employerService = new EmployerService(conexion);
        return employerService.listarModalidades();
    }

    public List<Ubicacion> listarUbicaciones(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarUbicaciones();
    }

    private List<OfertasEmpresa> listarOfertasEmpleo(Connection conexion){
        invitadoService = new InvitadoService(conexion);
        return invitadoService.listarOfeta();
    }

    private List<OfertasEmpresa> listarOfertasEmpleoEmpresa(Connection conexion, int empresa){
        invitadoService = new InvitadoService(conexion);
        return invitadoService.listarOfertaEmpresa(empresa);
    }


    private OfertaEmpresaInvitado listarOfetaPostulacionSinE(Connection conexion, int codigo){
        invitadoService = new InvitadoService(conexion);
        return invitadoService.listarOfertasCodigoSinEntrevista(codigo);
    }

    private void  obtenerParamtros(HttpServletRequest req){

        try {
            codigo = Integer.parseInt(req.getParameter("codigo"));
        }catch (Exception e){
            System.out.println(e);
            codigo = 0;
        }
        System.out.println(codigo);
        valor = Boolean.parseBoolean(req.getParameter("valor"));
    }

}
