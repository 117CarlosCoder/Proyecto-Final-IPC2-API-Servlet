package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.JasperExportManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "EmployerManagerReportsServlet", urlPatterns = {"/v1/employer-reports-servlet/*"})
public class EmployerReports extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resources = "/home/carlos/Documentos/ipc2/Proyecto-Final-IPC2-API-Servlet/src/main/webapp/reportes/Employer/";
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        User user = (User) session.getAttribute("user");



        String uri = req.getRequestURI();
        String reporte = "";
        Map<String, Object> params = new HashMap<>();

        if (uri.endsWith("/ofertas-costos")) {
            reporte = "OfertasFinalizadasCostos";
            params = ofertasCostos(resp,user);
        }

        if (uri.endsWith("/entrevista-fecha-especifica")) {
            reporte = "EntrevistaFechaEspecifica";
            params = entrevistaFechaEspecifica(resp,user,req);
        }

        if (uri.endsWith("/entrevista-fecha-estado")) {
            reporte = "EntrevistasFechaEstado";
            params = entrevistaFechaEstado(resp,user,req);
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

    public Map<String, Object> ofertasCostos(HttpServletResponse resp, User user){
        resp.addHeader("Content-disposition", "attachment; filename=ofertasFinalizadaCostos.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("user", user.getCodigo());
        return params;
    }

    public Map<String, Object> entrevistaFechaEspecifica(HttpServletResponse resp, User user, HttpServletRequest req){
        String fecha = req.getParameter("fecha");
        String estado = req.getParameter("estado");
        resp.addHeader("Content-disposition", "attachment; filename=EntrevistaFechaEspecifica.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("empresa", user.getCodigo());
        params.put("fecha", fecha);
        params.put("estado",estado);
        return params;
    }

    public Map<String, Object> entrevistaFechaEstado(HttpServletResponse resp, User user, HttpServletRequest req){
        String fechaA = req.getParameter("fechaA");
        String fechaB = req.getParameter("fechaB");
        String estado = req.getParameter("estado");
        resp.addHeader("Content-disposition", "attachment; filename=EntrevistasFechaEstado.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("empresa", user.getCodigo());
        params.put("estado",estado);
        params.put("fechaA", fechaA);
        params.put("fechaB", fechaB);

        return params;
    }
}
