package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.AdminService;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

@WebServlet(name = "ApplicantPDF", urlPatterns = {"/v1/applicant-curriculum/*"})
@MultipartConfig()
public class AplicantPDF extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws  IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = request.getHeader("Authorization");

        UserService userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,response);
        String username = parts[0];
        String password = parts[1];


        User user = userService.validarUsuario(conexion, username, password, username);
        if (!user.getRol().equals("Solicitante") && !user.getRol().equals("Administrador") ) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = request.getRequestURI();
        InputStream inputStream = request.getInputStream();
        System.out.println("Pdf : " + inputStream);
        ApplicantService applicantService = new ApplicantService(conexion);

        if (uri.endsWith("/guardar-pdf")) {
            applicantService.guardarPfs(user.getCodigo(), inputStream);
        }

        if (uri.endsWith("/actualizar-pdf")) {
            applicantService.actualizarPdfs(user.getCodigo(), inputStream);
        }

        if (uri.endsWith("/actualizar-pdf-admin")) {
            int codigo = Integer.parseInt(request.getParameter("codigo"));
            applicantService.actualizarPdfs(codigo, inputStream);
        }


    }

}
