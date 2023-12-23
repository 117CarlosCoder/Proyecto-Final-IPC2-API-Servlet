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
    private ApplicantService applicantService;
    private UserService userService ;
    private String username;
    private String password;
    private User user;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = request.getHeader("Authorization");

        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,response);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Solicitante")) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        InputStream inputStream = request.getInputStream();
        System.out.println("Pdf : " + inputStream);
        applicantService = new ApplicantService(conexion);
        applicantService.guardarPfs(user.getCodigo(), inputStream);

    }

}
