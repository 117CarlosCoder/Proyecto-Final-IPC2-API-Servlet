package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.AdminService;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        Connection conexion = (Connection) session.getAttribute("conexion");
        User user = (User) session.getAttribute("user");

        InputStream inputStream = request.getInputStream();
        System.out.println("Pdf : " + inputStream);
        applicantService = new ApplicantService(conexion);
        applicantService.guardarPfs(user.getCodigo(), inputStream);

    }

}
