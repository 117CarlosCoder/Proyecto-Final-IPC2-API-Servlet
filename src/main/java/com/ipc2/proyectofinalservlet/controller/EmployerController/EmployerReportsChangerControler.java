package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.OfertasEmpresaFecha;
import com.ipc2.proyectofinalservlet.model.Employer.EntrevistaFecha;
import com.ipc2.proyectofinalservlet.model.Employer.Estado;
import com.ipc2.proyectofinalservlet.model.Employer.EstadoOferta;
import com.ipc2.proyectofinalservlet.model.Employer.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.EmployerService;
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

@WebServlet(name = "EmployerManagerChangerReportsServlet", urlPatterns = {"/v1/employer-reports-changer-servlet/*"})
public class EmployerReportsChangerControler extends HttpServlet {

    private EmployerService employerService;
    private Date fecha;
    private String estado;

    private String fechaA;
    private  String fechaB;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        UserService userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        String username = parts[0];
        String password = parts[1];


        User user = userService.validarUsuario(conexion, username, password, username);
        if (!user.getRol().equals("Empleador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();


        if (uri.endsWith("/ofertas-costos")) {
            List<OfertaCostos> ofertaCostos = ofertaCostos(conexion);
            userService.enviarJson(resp, ofertaCostos);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-estados")) {
            List<Estado> estados = listarEstados(conexion);
            userService.enviarJson(resp, estados);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-estados-oferta")) {
            List<EstadoOferta> estadoOfertas = listarEstadosOfertas(conexion);
            userService.enviarJson(resp, estadoOfertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-fechaEntrevista")) {
            obtenerParametros(req);
            List<EntrevistaFecha> estados =entrevistaFechas(conexion,fecha, user.getCodigo(),estado);
            userService.enviarJson(resp, estados);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-fecha-Oferta")) {
            obtenerParametros(req);
            List<OfertasEmpresaFecha> ofertas =listarOfertasFecha(conexion,fechaA, fechaB,estado, user.getCodigo());
            userService.enviarJson(resp,ofertas);
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

    public void obtenerParametros(HttpServletRequest req){
        try {
            fecha = Date.valueOf(req.getParameter("fecha"));
        }catch (Exception e){
            System.out.println(e);
            fecha = null;
        }

         estado = req.getParameter("estado");
         fechaA = req.getParameter("fechaA");
         fechaB = req.getParameter("fechaB");
    }
}
