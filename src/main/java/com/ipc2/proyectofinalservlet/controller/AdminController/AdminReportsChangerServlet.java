package com.ipc2.proyectofinalservlet.controller.AdminController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.model.Admin.CantidadTotal;
import com.ipc2.proyectofinalservlet.model.Admin.IngresoTotal;
import com.ipc2.proyectofinalservlet.model.Admin.RegistroComision;
import com.ipc2.proyectofinalservlet.model.Admin.TopEmpleadores;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.http.entity.ContentType;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "AdminManagerReportsServlet", urlPatterns = {"/v1/admin-report-changer-servlet/*"})
public class AdminReportsChangerServlet extends HttpServlet {
    private AdminService adminService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();
        User user = (User) session.getAttribute("user");

        if (uri.endsWith("/listar-top-empleadores")) {
            List<TopEmpleadores> topEmpleadores = topEmpleadores(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), topEmpleadores);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-cantidad-total")) {
            String fechaA = req.getParameter("fechaA");
            String fechaB = req.getParameter("fechaB");
            int categoria = Integer.parseInt(req.getParameter("categoria"));
            boolean valor = Boolean.parseBoolean(req.getParameter("valor"));
            System.out.println("Valor : " + valor);
            CantidadTotal cantidadTotal = null;
            if (valor){
                 cantidadTotal = litarTotal(conexion,categoria, fechaA,fechaB);
            }
            else {
                cantidadTotal = listarTotalSinFecha(conexion);
            }
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), cantidadTotal);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-registro-comision")) {
            List<RegistroComision> registroComisions = listarRegistroComision(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), registroComisions);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/ingresos-fecha")) {
            String fechaA = req.getParameter("fechaA");
            String fechaB = req.getParameter("fechaB");
            List<IngresoTotal> ingresoTotals= ingresosTotales(conexion,fechaA,fechaB);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), ingresoTotals);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    public List<TopEmpleadores> topEmpleadores(Connection conexion){
        adminService = new AdminService(conexion);
        return adminService.topEmpleadores();
    }

    public List<IngresoTotal> ingresosTotales(Connection conexion, String fechaA, String fechaB){
        adminService = new AdminService(conexion);
        return adminService.ingresoTotalFecha(fechaA,fechaB);
    }

    public CantidadTotal litarTotal(Connection conexion, int categoria, String fechaA , String fechaB){
        adminService = new AdminService(conexion);
        return adminService.ofertaTotal(categoria, fechaA, fechaB);
    }
    public CantidadTotal listarTotalSinFecha(Connection conexion){
        adminService = new AdminService(conexion);
        return adminService.ofertaTotalSinFecha();
    }

    public List<RegistroComision> listarRegistroComision(Connection conexion){
        adminService = new AdminService(conexion);
        return adminService.listarRegistroComision();
    }
}
