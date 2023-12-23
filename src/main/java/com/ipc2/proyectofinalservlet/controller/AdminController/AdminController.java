package com.ipc2.proyectofinalservlet.controller.AdminController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Admin.Dashboard;
import com.ipc2.proyectofinalservlet.model.Admin.RegistroComision;
import com.ipc2.proyectofinalservlet.model.Admin.TelefonosUsuario;
import com.ipc2.proyectofinalservlet.model.Admin.UsuarioCreacion;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.Employer.NumTelefono;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.AdminService;
import com.ipc2.proyectofinalservlet.service.CargarDatosService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "AdminManagerServlet", urlPatterns = {"/v1/admin-servlet/*"})
public class AdminController extends HttpServlet {

    private CargarDatosService cargarDatosService;
    private AdminService adminService;
    private UserService userService ;
    private String username;
    private String password;
    private User user;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Administrador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/cargar-categorias")) {
            List<Categoria> categorias = listarCategorias(conexion);
            enviarJson(resp,categorias);
        }
        if (uri.endsWith("/cargar-categoria")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            Categoria categorias = listarCategoriaCodigo(conexion,codigo);
            enviarJson(resp,categorias);
        }
        if (uri.endsWith("/listar-dashboard")) {
            Dashboard dashboard = listarDashboard(conexion);
            enviarJson(resp,dashboard);
        }

        if (uri.endsWith("/listar-comision")) {
            Comision comision = listarComision(conexion);
            enviarJson(resp,comision);
        }

        if (uri.endsWith("/listar-usuarios")) {
            String rol = req.getParameter("rol");
            List<Usuarios> usuarios = listarUsuarios(conexion,rol);
            enviarJson(resp,usuarios);
        }

        if (uri.endsWith("/listar-usuario")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            Usuarios usuario = listarUsuario(conexion,codigo);
            enviarJson(resp,usuario);
        }

        if (uri.endsWith("/listar-usuario-especifico")) {
            Usuarios usuario = listarUsuario(conexion, user.getCodigo());
            enviarJson(resp,usuario);
        }

        if (uri.endsWith("/listar-telefonos")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            List<NumTelefono> telefonos = listarTelefonos(conexion,codigo);
            enviarJson(resp,telefonos);
        }

        if (uri.endsWith("/listar-telefonos-usuario-especifico")) {
            List<NumTelefono> telefonos = listarTelefonos(conexion, user.getCodigo());
            enviarJson(resp,telefonos);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");
        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];

        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Administrador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/gestionar-categorias-crear")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            Categoria categoria = (Categoria) leerJson(resp,req,Categoria.class);
            crearCategoria(conexion,categoria.getCodigo(), categoria.getNombre(), categoria.getDescripcion());
        }
        if (uri.endsWith("/crear-registro-comision")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            RegistroComision registroComision = (RegistroComision) leerJson(resp,req,RegistroComision.class);
            registrarComision(conexion,registroComision.getComision(),registroComision.getFecha());
        }

        if (uri.endsWith("/crear-usuarios")) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            UsuarioCreacion usuario = (UsuarioCreacion) leerJson(resp,req, UsuarioCreacion.class);
            crearUsuario(conexion,usuario.getUsuario());
            crearTelefonosUser(conexion,usuario.getUsuario(),usuario.getTelefonos());

        }

        if (uri.endsWith("/crear-telefonos")) {
            List<NumTelefono> telefono = readJsonTelefonos(resp,req);
            crearTelefonos(conexion,telefono);

        }

        if (uri.endsWith("/crear-telefonos-usuario")) {
            List<TelefonosUsuario> telefono = readJsonTelefonosUsuario(resp,req);
            crearTelefonosUsarioP2(conexion,telefono);

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");
        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Administrador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }


        String uri = req.getRequestURI();

        if (uri.endsWith("/gestionar-categorias-actualizar")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            Categoria categoria = (Categoria) leerJson(resp,req,Categoria.class);
            actualizarCategoria(conexion,categoria.getCodigo(), categoria.getNombre(), categoria.getDescripcion());
        }

        if (uri.endsWith("/actualizar-comision")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            Comision comision = (Comision) leerJson(resp,req, Comision.class);
            actualizarComision(conexion,comision.getCantidad());
        }

        if (uri.endsWith("/actualizar-usuario")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            User usuario = (User) leerJson(resp,req,User.class);
            actualizarUsuario(conexion,usuario);
        }

        if (uri.endsWith("/actualizar-telefonos")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            List<NumTelefono> telefonos = readJsonTelefonos(resp,req);
            actualizarTelefono(conexion,telefonos);
        }


    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");
        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Administrador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/eliminar-usuario")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            String username = req.getParameter("username");
            eliminarUsuario(conexion, username);
        }

        if (uri.endsWith("/eliminar-categoria")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            eliminarCategoria(conexion, codigo);
        }

    }

    public void actualizarComision(Connection conexion, BigDecimal cantidad){
        cargarDatosService = new CargarDatosService(conexion);
        cargarDatosService.actualizarComision(cantidad);
    }

    public void crearCategoria(Connection conexion,int codigo, String nombre, String descripcion){
        adminService = new AdminService(conexion);
        adminService.crearCategoria(codigo,nombre,descripcion);
    }

    public void crearTelefonos(Connection conexion,List<NumTelefono> numTelefonos){
        adminService = new AdminService(conexion);
        adminService.crearTelefonos(numTelefonos);

    }

    public void crearTelefonosUsarioP2(Connection conexion,List<TelefonosUsuario> numTelefonos){
        adminService = new AdminService(conexion);
        adminService.crearTelefonosUsarioP2(numTelefonos);
    }

    private void crearTelefonosUser(Connection conexion, User user, Telefono telefono){
        System.out.println("CrearTelefono");
        userService = new UserService(conexion);
        if ((telefono.getTelefono1() != null)){
            userService.crearTelefono(telefono.getTelefono1(), user);
        }
        if ((telefono.getTelefono2() != null)){
            userService.crearTelefono(telefono.getTelefono2(),user);
        }
        if ((telefono.getTelefono3() != null)){
            userService.crearTelefono(telefono.getTelefono3(),user);
        }


    }

    private void crearUsuario(Connection conexion, User user){
        userService = new UserService(conexion);
        System.out.println("Usuario : " + user);
        userService.crearUsuarioEmpleador(user);

    }

    public void registrarComision(Connection conexion, BigDecimal comision, String fecha){
        adminService = new AdminService(conexion);
        adminService.crearComision(comision, fecha);
    }

    public Categoria listarCategoriaCodigo(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarCategoriaCodigo(codigo);
    }

    public List<Categoria> listarCategorias(Connection conexion){
        adminService = new AdminService(conexion);
        return adminService.listarCategorias();
    }

    public Dashboard listarDashboard(Connection conexion){
        adminService = new AdminService(conexion);
        return adminService.listarDashboard();
    }

    public List<Usuarios> listarUsuarios(Connection conexion, String rol){
        adminService = new AdminService(conexion);
        return adminService.listarUsuario(rol);
    }

    public Usuarios listarUsuario(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarUsuarioE(codigo);
    }

    public List<NumTelefono> listarTelefonos(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarTelefonos(codigo);
    }

    public Comision listarComision(Connection conexion){
        adminService = new AdminService(conexion);
        return adminService.listarComision();
    }

    public void actualizarCategoria(Connection conexion,int codigo, String nombre, String descripcion){
        adminService = new AdminService(conexion);
        adminService.actualizarCategoria(codigo,nombre,descripcion);
    }

    public  void actualizarTelefono(Connection conexion, List<NumTelefono> telefono  ){
        adminService = new AdminService(conexion);
        adminService.actualizarTelefono(telefono);

    }
    private void actualizarUsuario(Connection conexion, User usuario) {
        adminService = new AdminService(conexion);
        adminService.actualizarUsuario(usuario);
    }


    public void eliminarUsuario(Connection conexion,String username){
        userService = new UserService(conexion);
        userService.eliminarUsuario(username);
    }

    public void eliminarCategoria(Connection conexion,int codigo){
        adminService = new AdminService(conexion);
        adminService.eliminarCategoria(codigo);
    }

    private List<NumTelefono> readJsonTelefonos(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = req.getReader()) {
            List<NumTelefono> numTelefonos = gson.fromJson(reader, new TypeToken<List<NumTelefono>>() {}.getType());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            return numTelefonos;
        } catch (IOException e) {
            throw new IOException("Error al procesar la solicitud JSON", e);
        }
    }

    private List<TelefonosUsuario> readJsonTelefonosUsuario(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = req.getReader()) {
            List<TelefonosUsuario> numTelefonos = gson.fromJson(reader, new TypeToken<List<TelefonosUsuario>>() {}.getType());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            return numTelefonos;
        } catch (IOException e) {
            throw new IOException("Error al procesar la solicitud JSON", e);
        }
    }

    public Object leerJson(HttpServletResponse resp, HttpServletRequest req , Class clase) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Object valor = objectMapper.readValue(req.getInputStream(), clase);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return valor;
    }

    public void enviarJson(HttpServletResponse resp, Object valor) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.writeValue(resp.getWriter(), valor);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());

    }

}
