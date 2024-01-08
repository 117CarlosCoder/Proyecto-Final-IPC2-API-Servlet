package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.EntrevitaN;
import com.ipc2.proyectofinalservlet.model.CargarDatos.OfertasEmpresa;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "ApplicantInterviewManagerServlet", urlPatterns = {"/v1/applicant-interview-servlet/*"})
public class ApplicantInterviewController extends HttpServlet {

    private ApplicantService applicantService;

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
        if (!user.getRol().equals("Solicitante")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if(uri.endsWith("/listar-entrevistas")) {
            List<EntrevitaN> entrevistas = listarEntrevitas(conexion, user.getCodigo());
            System.out.println("usuario :" + user.getCodigo());
            userService.enviarJson(resp, entrevistas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }



        if(uri.endsWith("/listar-oferta-postulacion")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            OfertasEmpresa oferta = listarOfetaPostulacion(conexion, codigo, user.getCodigo());
            userService.enviarJson(resp, oferta);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private List<EntrevitaN> listarEntrevitas(Connection conexion, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarEntrevistas(usuario);
    }

    private OfertasEmpresa listarOfetaPostulacion(Connection conexion, int codigo,int usuarion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfetaCodigo(codigo, usuarion);
    }
}
