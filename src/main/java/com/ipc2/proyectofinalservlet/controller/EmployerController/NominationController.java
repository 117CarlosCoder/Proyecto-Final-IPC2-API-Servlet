package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.ipc2.proyectofinalservlet.HelloServlet;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.EmployerService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

@WebServlet(name = "EmployerNominationServlet", urlPatterns = {"/v1/employer-nomination-servlet/*"})

public class NominationController extends HelloServlet {
    private EmployerService employerService;
    private UserService userService;

    private User user;
    private String username;
    private String password;
    private int codigo;
    private int oferta;

    private String mensaje;
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
        employerService = new EmployerService(conexion);
        employerService.actualizarEstadoOferta();

        if (uri.endsWith("/cargar-ofertas")) {
            resp.setStatus(HttpServletResponse.SC_OK);
            List<Ofertas> ofertas = listarOfertasEmpresasPostulaciones(conexion, user.getCodigo());
            userService.enviarJson(resp,ofertas);

        }

        if (uri.endsWith("/cargar-entrevistas")) {
            obtenerParanetros(req);
            resp.setStatus(HttpServletResponse.SC_OK);
            List<EntrevistaInfo> entrevistas = listarEntrevistas(conexion,codigo, user.getCodigo());
            userService.enviarJson(resp,entrevistas);

        }

        if (uri.endsWith("/cargar-entrevistas-contratacion")) {
            obtenerParanetros(req);

            System.out.println(codigo);
            List<EntrevistaInfo> entrevistas = listarEntrevistasContratacion(conexion,codigo, user.getCodigo());
            if (entrevistas.isEmpty()){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            userService.enviarJson(resp,entrevistas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/crear-notificaciones")) {
            obtenerParanetros(req);
            crearNotificacion(conexion, mensaje, user.getCodigo(),codigo);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/fase-entrevista")) {
            obtenerParanetros(req);
            System.out.println(codigo);
            if (!employerService.faseEntrevista(codigo, user.getCodigo())){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/fase-contratacion")) {
            obtenerParanetros(req);
            if (!employerService.faseContratacion(codigo, user.getCodigo())){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
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
        employerService = new EmployerService(conexion);
        employerService.actualizarEstadoOferta();


        if (uri.endsWith("/cargar-postulantes")) {
            obtenerParanetros(req);
            List<EstadoSolicitudPostulante> solicitudes = listarPostulantes(conexion,codigo, user.getCodigo());
            userService.enviarJson(resp, solicitudes);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/obtener-postulante")) {
            obtenerParanetros(req);
            Postulante postulante = obtenerPostulante(conexion,codigo, oferta);
            userService.enviarJson(resp, postulante);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/generar-entrevista")) {
            obtenerParanetros(req);
            Postulante postulante =obtenerPostulante(conexion,codigo, oferta);
            Entrevista entrevista = (Entrevista) userService.leerJson(resp,req, Entrevista.class);
            if (entrevista == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            assert entrevista != null;
            generarEntrevista(conexion,postulante.getCodigo(),postulante.getCodigoOferta(),postulante.getCodigo(),entrevista.getFecha(),entrevista.getHora(),entrevista.getUbicacion());
            if (!actualizarOfertaEstado(conexion,entrevista.getCodigoOferta())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_OK);
        }




    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {

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
        employerService = new EmployerService(conexion);
        employerService.actualizarEstadoOferta();

        if (uri.endsWith("/finalizar-entrevista")) {
            Entrevista entrevista = (Entrevista) userService.leerJson(resp,req, Entrevista.class);
            if (entrevista == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println(entrevista);
            assert entrevista != null;
            if (!finalizarEntrevista(conexion,entrevista.getNotas(),entrevista.getUsuario(),entrevista.getCodigo())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/contratar")) {
            Entrevista entrevista = (Entrevista) userService.leerJson(resp,req, Entrevista.class);
            if (entrevista == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println(entrevista);
            assert entrevista != null;
            if(!contratar(conexion, entrevista.getUsuario(), entrevista.getCodigoOferta())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    public List<Ofertas> listarOfertasEmpresasPostulaciones(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarOfertasEmpresaPostulaciones(empresa);
    }

    public boolean actualizarOfertaEstado(Connection conexion, int usuario){
        employerService = new EmployerService(conexion);
        return employerService.actualizarOfertaEstado(usuario);
    }

    public List<EstadoSolicitudPostulante> listarPostulantes(Connection conexion, int codigo, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarPostulaciones(codigo,empresa);
    }

    public Postulante obtenerPostulante(Connection conexion, int usuario, int oferta){
        employerService = new EmployerService(conexion);
        return employerService.obtenerPostulante(usuario, oferta);
    }

    public void crearNotificacion(Connection conexion, String mensaje, int empresa, int usuario){
        employerService = new EmployerService(conexion);
         employerService.crearNotificacion(mensaje, empresa, usuario);
    }

    public boolean contratar(Connection conexion, int usuario, int codigo){
        employerService = new EmployerService(conexion);
       return employerService.contratar(usuario, codigo);
    }

    private void generarEntrevista(Connection conexion, int codigo,int codigoOferta, int usuario, Date fecha, String hora, String ubicacion ){
        employerService = new EmployerService(conexion);
        employerService.generarEntrevista(codigo,codigoOferta,usuario,fecha,hora,ubicacion);
    }

    public List<EntrevistaInfo> listarEntrevistas(Connection conexion, int codigo, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarEntrevistas(codigo,empresa);
    }

    public List<EntrevistaInfo> listarEntrevistasContratacion(Connection conexion,int codigo, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarEntrevistasContratacion(codigo,empresa);
    }

    public boolean finalizarEntrevista(Connection conexion,String notas,int usuario, int codigo){
        employerService = new EmployerService(conexion);
        return employerService.finalizarEntrevista(notas,usuario,codigo);
    }

    public void obtenerParanetros(HttpServletRequest req){
        try {

            oferta = Integer.parseInt(req.getParameter("oferta"));
        }catch (Exception e){
            System.out.println(e);
            oferta = 0;
        }

        try {
            codigo = Integer.parseInt(req.getParameter("codigo"));
        }catch (Exception e){
            System.out.println(e);
            codigo = 0;
        }
        try {

            mensaje = req.getParameter("mensaje");
        }catch (Exception e){
            System.out.println(e);
            mensaje = "";
        }

    }
}
