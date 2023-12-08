package com.ipc2.proyectofinalservlet.controller.AdminController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipc2.proyectofinalservlet.data.AdminDB;
import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.model.Admin.Dashboard;
import com.ipc2.proyectofinalservlet.model.Admin.RegistroComision;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.Employer.NumTelefono;
import com.ipc2.proyectofinalservlet.model.Employer.Telefonos;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.model.User.login;
import com.ipc2.proyectofinalservlet.service.AdminService;
import com.ipc2.proyectofinalservlet.service.CargarDatosService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "AdminManagerServlet", urlPatterns = {"/v1/admin-servlet/*"})
public class AdminController extends HttpServlet {

    private CargarDatosService cargarDatosService;
    private AdminService adminService;

    private UserService userService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



        String angularSessionId = req.getHeader("X-Angular-Session-Id");
        System.out.println("Sesion desde angular cookie:" + angularSessionId);
        /*Cookie[] cookies = req.getCookies();
        HttpSession httpSession = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userData".equals(cookie.getName())) {
                    // Recuperar y procesar la información de la cookie
                    String userDataJson = cookie.getValue();
                    System.out.println("data : " + userDataJson);
                    ObjectMapper objectMapper = new ObjectMapper();
                    httpSession = objectMapper.readValue(userDataJson, HttpSession.class);
                    // Hacer algo con la información recuperada
                }
            }
        }*/
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        //HttpSession session =  req.getSession(false);
        System.out.println("sesion get : " + session);
        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();

        if (uri.endsWith("/cargar-categorias")) {
            List<Categoria> categorias = listarCategorias(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), categorias);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/cargar-categoria")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            Categoria categorias = listarCategoriaCodigo(conexion,codigo);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), categorias);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-dashboard")) {
            Dashboard dashboard = listarDashboard(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), dashboard);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-comision")) {
            Comision comision = listarComision(conexion);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), comision);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-usuarios")) {
            String rol = req.getParameter("rol");
            List<Usuarios> usuarios = listarUsuarios(conexion,rol);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), usuarios);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-usuario")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            Usuarios usuario = listarUsuario(conexion,codigo);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), usuario);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-telefonos")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            List<NumTelefono> telefonos = listarTelefonos(conexion,codigo);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            objectMapper.writeValue(resp.getWriter(), telefonos);
            resp.setStatus(HttpServletResponse.SC_OK);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        //HttpSession session = req.getSession(false);
        Connection conexion = (Connection) session.getAttribute("conexion");

        String uri = req.getRequestURI();

        if (uri.endsWith("/gestionar-categorias-crear")) {
            Categoria categoria = readJsonCategoria(resp,req,conexion);
            crearCategoria(conexion,categoria.getCodigo(), categoria.getNombre(), categoria.getDescripcion());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
        if (uri.endsWith("/crear-registro-comision")) {

            RegistroComision registroComision = readJsonRegistroComision(resp,req);
            registrarComision(conexion,registroComision.getComision(),registroComision.getFecha());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        }

        if (uri.endsWith("/crear-usuarios")) {

            User usuario = readJsonUsuario(resp,req);
            session.setAttribute("usuario",usuario);
            crearUsuario(conexion,usuario);
            resp.setStatus(HttpServletResponse.SC_CREATED);

        }

        if (uri.endsWith("/crear-telefonos")) {

            List<NumTelefono> telefono = readJsonTelefonos(resp,req);
            crearTelefonos(conexion,telefono);

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        //HttpSession session = (HttpSession) req.getAttribute("userSession");
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

        if (uri.endsWith("/actualizar-usuario")) {

            User usuario = readJsonUsuario(resp,req);
            session.setAttribute("usuario",usuario);
            System.out.println("usuario act: "+ usuario);
            actualizarUsuario(conexion,usuario);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        }

        if (uri.endsWith("/actualizar-telefonos")) {

            List<NumTelefono> telefonos = readJsonTelefonos(resp,req);
            actualizarTelefono(conexion,telefonos);
            System.out.println("usuario act: "+ telefonos);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        }


    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //HttpSession session = (HttpSession) req.getAttribute("userSession");
        HttpSession session = (HttpSession) getServletContext().getAttribute("userSession");
        System.out.println("sesion delete : " + session);
        Connection conexion = (Connection) session.getAttribute("conexion");
        String uri = req.getRequestURI();

        if (uri.endsWith("/eliminar-usuario")) {
            String username = req.getParameter("username");
            eliminarUsuario(conexion, username);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        }

        if (uri.endsWith("/eliminar-categoria")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            eliminarCategoria(conexion, codigo);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);

        }

    }

    public void actualizarComision(Connection conexion, int cantidad){
        cargarDatosService = new CargarDatosService(conexion);
        cargarDatosService.actualizarComision(cantidad);
    }

    public void crearCategoria(Connection conexion,int codigo, String nombre, String descripcion){
        adminService = new AdminService(conexion);
        adminService.crearCategoria(codigo,nombre,descripcion);
    }

    public void crearTelefonos(Connection conexion,List<NumTelefono> numTelefonos){
        adminService = new AdminService(conexion);
        System.out.println(numTelefonos);
        if (numTelefonos.size() > 0) {
            adminService.crearTelefonos(numTelefonos.get(0));

            if (numTelefonos.size() > 1) {
                if ((numTelefonos.get(1)) != null) {
                    adminService.crearTelefonos(numTelefonos.get(1));
                }
                if (numTelefonos.size() > 2) {
                    if ((numTelefonos.get(2)) != null) {
                        adminService.crearTelefonos(numTelefonos.get(2));
                    }
                }
            }
        }

    }

    private void crearUsuario(Connection conexion, User user){
        userService = new UserService(conexion);
        if (user.getRol().equals("Empleador")){
            userService.crearUsuarioEmpleador(user,true);
        }
        if (user.getRol().equals("Solicitante")){
            userService.crearUsuarioSolicitante(user,true);
        }

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
        adminService.actualizarTelefono(telefono.get(0));
        if (telefono.size() > 1) {
            if ((telefono.get(1)) != null) {
                adminService.actualizarTelefono(telefono.get(1));
            }
            if (telefono.size() > 2) {
                if ((telefono.get(2)) != null) {
                    adminService.actualizarTelefono(telefono.get(2));
                }
            }
        }
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

    public Comision readJson(HttpServletResponse resp, HttpServletRequest req , Connection conexion) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Comision comision = objectMapper.readValue(req.getInputStream(), Comision.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return comision;
    }

    public RegistroComision readJsonRegistroComision(HttpServletResponse resp, HttpServletRequest req ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        RegistroComision registroComision = objectMapper.readValue(req.getInputStream(), RegistroComision.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return registroComision;
    }


    private List<NumTelefono> readJsonTelefonos(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        Gson gson = new Gson();

        try (Reader reader = req.getReader()) {
            // Lee la entrada del flujo de datos de la solicitud HTTP
            List<NumTelefono> numTelefonos = gson.fromJson(reader, new TypeToken<List<NumTelefono>>() {}.getType());

            // Configura el Content-Type de la respuesta (revisar si esto es necesario aquí)
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());

            return numTelefonos;
        } catch (IOException e) {
            // Maneja la excepción o relanza una nueva excepción más específica si es necesario
            throw new IOException("Error al procesar la solicitud JSON", e);
        }
    }
    private User readJsonUsuario(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        User user = objectMapper.readValue(req.getInputStream(), User.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return user;
    }

    public Categoria readJsonCategoria(HttpServletResponse resp, HttpServletRequest req , Connection conexion) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Categoria categoria = objectMapper.readValue(req.getInputStream(), Categoria.class);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return categoria;
    }
}
