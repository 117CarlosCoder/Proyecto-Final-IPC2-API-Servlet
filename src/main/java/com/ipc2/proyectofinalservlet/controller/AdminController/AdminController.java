package com.ipc2.proyectofinalservlet.controller.AdminController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.AdminDB;
import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.AdminService;
import com.ipc2.proyectofinalservlet.service.CargarDatosService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "AdminManagerServlet", urlPatterns = {"/v1/admin-servlet/*"})
public class AdminController extends HttpServlet {

    private CargarDatosService cargarDatosService;
    private AdminService adminService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();
        User user = (User) session.getAttribute("user");

        if (uri.endsWith("/cargar-categorias")) {
            List<Categoria> categorias = listarCategoriaCodigo(conexion,user.getCodigo());
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.writeValue(resp.getWriter(), categorias);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();

        if (uri.endsWith("/gestionar-categorias-crear")) {
            Categoria categoria = readJsonCategoria(resp,req,conexion);
            crearCategoria(conexion,categoria.getCodigo(), categoria.getNombre(), categoria.getDescripcion());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
        if (uri.endsWith("/reportes")) {


        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();

        if (uri.endsWith("/gestionar-categorias-actualizar")) {
            Categoria categoria = readJsonCategoria(resp,req,conexion);
            actualizarCategoria(conexion,categoria.getCodigo(), categoria.getNombre(), categoria.getDescripcion());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/actualizar-comision")) {

            Comision comision = readJson(resp,req,conexion);
            actualizarComision(conexion,comision.getCantidad());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        }


    }

    public void actualizarComision(Connection conexion, int cantidad){
        CargaDB cargaDB = new CargaDB(conexion);
        cargarDatosService = new CargarDatosService(conexion);
        cargarDatosService.actualizarComision(cantidad);
    }

    public void crearCategoria(Connection conexion,int codigo, String nombre, String descripcion){
        AdminDB adminDB = new AdminDB(conexion);
        adminService = new AdminService(conexion);
        adminService.crearCategoria(codigo,nombre,descripcion);
    }

    public List<Categoria> listarCategoriaCodigo(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarCategoriaCodigo(codigo);
    }

    public void actualizarCategoria(Connection conexion,int codigo, String nombre, String descripcion){
        AdminDB adminDB = new AdminDB(conexion);
        adminService = new AdminService(conexion);
        adminService.actualizarCategoria(codigo,nombre,descripcion);
    }

    public Comision readJson(HttpServletResponse resp, HttpServletRequest req , Connection conexion) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Comision comision = objectMapper.readValue(req.getInputStream(), Comision.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return comision;
    }

    public Categoria readJsonCategoria(HttpServletResponse resp, HttpServletRequest req , Connection conexion) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Categoria categoria = objectMapper.readValue(req.getInputStream(), Categoria.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return categoria;
    }
}
