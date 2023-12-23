package com.ipc2.proyectofinalservlet.controller.AdminController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.SesionService;
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
import java.sql.Connection;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AdminReportsServlet", urlPatterns = {"/v1/admin-reports-servlet/*"})
public class AdminReports extends HttpServlet {
    private SesionService sesionService;
    private String username;
    private String password;
    private User user;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();
        String authorizationHeader = req.getHeader("Authorization");
        autorizacion(authorizationHeader,resp);

        user = validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Administrador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String resources = "/home/carlos/Documentos/ipc2/Proyecto-Final-IPC2-API-Servlet/src/main/webapp/reportes/Admin/";

        String uri = req.getRequestURI();
        String reporte = "";
        Map<String, Object> params = new HashMap<>();

        if (uri.endsWith("/historial-comision")) {
            reporte = "HistorialComision";
            resp.addHeader("Content-disposition", "attachment; filename=HistorialComision.pdf");
        }

        if (uri.endsWith("/empleadores-mas-ofertas")) {
            reporte = "Top5Empleadores";
            resp.addHeader("Content-disposition", "attachment; filename=Top5Empleadores.pdf");
        }

        if (uri.endsWith("/empleadores-mas-ingresos")) {
            reporte = "EmpleadoresMasIngresos";
            params = empleadoFechaMasIngresos(resp,req);

        }

        if (uri.endsWith("/total-ingresos")) {
            reporte = "TotalIngresos";
            params = totalIngresos(resp,req);

        }

        try (InputStream inputStream = new FileInputStream(resources + reporte + ".jasper");) {
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

    public Map<String, Object> empleadoFechaMasIngresos(HttpServletResponse resp, HttpServletRequest req){
        String fechaA = req.getParameter("fechaA");
        String fechaB = req.getParameter("fechaB");
        resp.addHeader("Content-disposition", "attachment; filename=EmpleadoresMasIngresos.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("fechaA", fechaA);
        params.put("fechaB",fechaB);
        return params;
    }

    public Map<String, Object> totalIngresos(HttpServletResponse resp, HttpServletRequest req){
        String fechaA = req.getParameter("fechaA");
        String fechaB = req.getParameter("fechaB");
        resp.addHeader("Content-disposition", "attachment; filename=TotalIngresos.pdf");
        Map<String, Object> params = new HashMap<>();
        params.put("fechaA", fechaA);
        params.put("fechaB",fechaB);
        return params;
    }

    public User validarUsuario(Connection conexion,String username, String password, String email) {
        System.out.println("validar : ");
        sesionService = new SesionService(conexion);
        return sesionService.obtenerUsuario(username, password, email);
    }

    public void autorizacion(String authorizationHeader, HttpServletResponse resp) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);
            username = parts[0];
            password = parts[1];
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        }
        else {
            System.out.println("Usuario no aceptado");
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }

    }
}

