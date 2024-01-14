package com.ipc2.proyectofinalservlet.controller.AdminController;

import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Admin.CantidadTotal;
import com.ipc2.proyectofinalservlet.model.Admin.IngresoTotal;
import com.ipc2.proyectofinalservlet.model.Admin.RegistroComision;
import com.ipc2.proyectofinalservlet.model.Admin.TopEmpleadores;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.AdminService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "AdminManagerReportsServlet", urlPatterns = {"/v1/admin-report-changer-servlet/*"})
public class AdminReportsChangerServlet extends HttpServlet {
    private AdminService adminService;
    private String fechaA;
    private String fechaB;
    private int categoria;

    private boolean valor;

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
        if (!user.getRol().equals("Administrador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();
        obtenerParametros(req);

        if (uri.endsWith("/listar-top-empleadores")) {
            List<TopEmpleadores> topEmpleadores = topEmpleadores(conexion);
            userService.enviarJson(resp, topEmpleadores);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-cantidad-total")) {
            obtenerParametros(req);
            System.out.println("Valor : " + valor);
            CantidadTotal cantidadTotal = null;
            if (valor){
                 cantidadTotal = litarTotal(conexion,categoria, fechaA,fechaB);
            }
            else {
                cantidadTotal = listarTotalSinFecha(conexion);
            }

            userService.enviarJson(resp, cantidadTotal);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-registro-comision")) {
            List<RegistroComision> registroComisions = listarRegistroComision(conexion);
            userService.enviarJson(resp,registroComisions);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/ingresos-fecha")) {
            obtenerParametros(req);
            List<IngresoTotal> ingresoTotals= ingresosTotales(conexion,fechaA,fechaB);
            userService.enviarJson(resp,ingresoTotals);
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

    public void obtenerParametros(HttpServletRequest req){
        try {
            fechaA = req.getParameter("fechaA");
            fechaB = req.getParameter("fechaB");
        }catch (Exception e){
            System.out.println(e);
            fechaA = "";
            fechaB = "";
        }

        try {
            categoria = Integer.parseInt(req.getParameter("categoria"));
        }catch (Exception e){
            System.out.println(e);
            categoria = 0;
        }
        valor = Boolean.parseBoolean(req.getParameter("valor"));
    }
}
