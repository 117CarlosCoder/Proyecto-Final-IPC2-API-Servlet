package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ApplicantReportsServlet", urlPatterns = {"/v1/applicant-reports-servlet/*"})
public class AplicantReports extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resources = "/home/carlos/Documentos/ipc2/Proyecto-Final-IPC2-API-Servlet/src/main/webapp/reportes/Aplicant/";
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

        String reporte = "";
        Map<String, Object> params = new HashMap<>();

        if (uri.endsWith("/oferta-sin-obtener-empleo")) {
            reporte = "OfetasSinObtenerEmpleo";
            params.put("user",user.getCodigo());
            resp.addHeader("Content-disposition", "attachment; filename=OfetasSinObtenerEmpleo.pdf");
        }

        if (uri.endsWith("/oferta-fase-seleccion")) {
            reporte = "OfertasFaseSeleccionUsuario";
            params = ofertasFaseSeleccion(resp, user,req);
            if (params.size() == 2){
                reporte = "OfertasSeleccionUsuarioSinFechajrxml";
            }
        }

        if (uri.endsWith("/oferta-fase-entrevista")) {
            reporte = "OfertasEstadoEntrevista";

            params = ofertasFaseEntrevista(resp, user,req);
            if (params.size() == 2 ){
                reporte = "OfertasEstadoEntrevistaSinFecha";
            }
        }

        if (uri.endsWith("/oferta-postulacion-retirada")) {
            reporte = "fechasOfertaRetirada";

            params = ofertasPostulacionRetirada(resp, user,req);
            if (params.size() == 1){
                reporte = "OfertasPostulacionRetiradaSinFecha";
            }
        }
        try (InputStream inputStream = new FileInputStream(resources + reporte +".jasper");){
            resp.setContentType("application/pdf");
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new Conexion().obtenerConexion());
            OutputStream out = resp.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            out.flush();
            out.close();
        } catch (IOException | JRException e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }


    public Map<String, Object> ofertasFaseSeleccion(HttpServletResponse resp, User user, HttpServletRequest req){
        String fechaA = req.getParameter("fechaA");
        String fechaB = req.getParameter("fechaB");
        String estado = req.getParameter("estado");
        System.out.println(fechaA);
        System.out.println(fechaB);
        System.out.println(estado);
        System.out.println(user.getCodigo());

        Map<String, Object> params = new HashMap<>();

        if (fechaA.isEmpty() || fechaB.isEmpty() || estado.isEmpty()){
            resp.addHeader("Content-disposition", "attachment; filename=OfertasFaseSeleccionUsuario.pdf");
            params.put("estado", estado);
            params.put("user", user.getCodigo());
        }else{
            resp.addHeader("Content-disposition", "attachment; filename=OfertasFaseSeleccionUsuario.pdf");
            params.put("estado", estado);
            params.put("user", user.getCodigo());
            params.put("fechaA", fechaA);
            params.put("fechaB", fechaB);
        }


        return params;
    }

    public Map<String, Object> ofertasFaseEntrevista(HttpServletResponse resp, User user, HttpServletRequest req){
        String fechaA = req.getParameter("fechaA");
        String fechaB = req.getParameter("fechaB");
        String estado = req.getParameter("estado");
        System.out.println(fechaA);
        System.out.println(fechaB);
        System.out.println(estado);
        System.out.println(user.getCodigo());
        Map<String, Object> params = new HashMap<>();
        if (fechaA.isEmpty() || fechaB.isEmpty() ){
            resp.addHeader("Content-disposition", "attachment; filename=OfertasEstadoEntrevista.pdf");
            params.put("estado", estado);
            params.put("user", user.getCodigo());
        }else {

            resp.addHeader("Content-disposition", "attachment; filename=OfertasEstadoEntrevista.pdf");
            params.put("estado", estado);
            params.put("user", user.getCodigo());
            params.put("fechaA", fechaA);
            params.put("fechaB", fechaB);
        }

        return params;
    }

    public Map<String, Object> ofertasPostulacionRetirada(HttpServletResponse resp, User user, HttpServletRequest req){
        String fechaA = req.getParameter("fechaA");
        String fechaB = req.getParameter("fechaB");
        System.out.println(fechaA);
        System.out.println(fechaB);
        System.out.println(user.getCodigo());
        Map<String, Object> params = new HashMap<>();
        if (fechaA.isEmpty() || fechaB.isEmpty()){
            resp.addHeader("Content-disposition", "attachment; filename=fechasOfertaRetirada.pdf");

            params.put("user", user.getCodigo());
        }else {

            resp.addHeader("Content-disposition", "attachment; filename=fechasOfertaRetirada.pdf");

            params.put("user", user.getCodigo());
            params.put("fechaA", fechaA);
            params.put("fechaB", fechaB);
        }

        return params;
    }

}
