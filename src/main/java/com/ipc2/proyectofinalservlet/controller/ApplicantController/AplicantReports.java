package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.User;
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
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ApplicantReportsServlet", urlPatterns = {"/v1/applicant-reports-servlet/*"})
public class AplicantReports extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resources = "/home/carlos117/Documentos/Ipc2/Proyecto Final IPC2 Angular y Sevlets/Proyecto-Final-Servlet/src/main/webapp/reportes/Aplicant/";
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        User user = (User) session.getAttribute("user");

        String uri = req.getRequestURI();
        String reporte = "";
        Map<String, Object> params = new HashMap<>();

        if (uri.endsWith("/oferta-sin-obtener-empleo")) {
            reporte = "OfetasSinObtenerEmpleo";
            resp.addHeader("Content-disposition", "attachment; filename=OfetasSinObtenerEmpleo.pdf");
        }

        if (uri.endsWith("/oferta-fase-seleccion")) {
            reporte = "OfertasFaseSeleccionUsuario";
            ofertasFaseSeleccion(resp,user,req);
        }

        if (uri.endsWith("/oferta-fase-entrevista")) {
            reporte = "OfertasEstadoEntrevista";
            ofertasFaseEntrevista(resp,user,req);
        }

        if (uri.endsWith("/oferta-postulacion-retirada")) {
            reporte = "fechasOfertaRetirada";
            ofertasPostulacionRetirada(resp,user,req);
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

        resp.addHeader("Content-disposition", "attachment; filename=OfertasFaseSeleccionUsuario.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("estado", estado);
        params.put("user", user.getCodigo());
        params.put("fechaA", fechaA);
        params.put("fechaB", fechaB);

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

        resp.addHeader("Content-disposition", "attachment; filename=OfertasEstadoEntrevista.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("estado", estado);
        params.put("user", user.getCodigo());
        params.put("fechaA", fechaA);
        params.put("fechaB", fechaB);

        return params;
    }

    public Map<String, Object> ofertasPostulacionRetirada(HttpServletResponse resp, User user, HttpServletRequest req){
        String fechaA = req.getParameter("fechaA");
        String fechaB = req.getParameter("fechaB");
        System.out.println(fechaA);
        System.out.println(fechaB);
        System.out.println(user.getCodigo());

        resp.addHeader("Content-disposition", "attachment; filename=fechasOfertaRetirada.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("user", user.getCodigo());
        params.put("fechaA", fechaA);
        params.put("fechaB", fechaB);

        return params;
    }

}
