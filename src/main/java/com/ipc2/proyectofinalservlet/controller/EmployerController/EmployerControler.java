package com.ipc2.proyectofinalservlet.controller.EmployerController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Admin.TelefonosUsuario;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdf;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.CompletarInformacionEmployer;
import com.ipc2.proyectofinalservlet.model.CargarDatos.CompletarInformacionEmployerTarjeta;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.Employer.Modalidades;
import com.ipc2.proyectofinalservlet.model.Employer.NumTelefono;
import com.ipc2.proyectofinalservlet.model.Employer.TarjetaDatos;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.AdminService;
import com.ipc2.proyectofinalservlet.service.ApplicantService;
import com.ipc2.proyectofinalservlet.service.EmployerService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

@WebServlet(name = "EmployerManagerServlet", urlPatterns = {"/v1/employer-servlet/*"})
public class EmployerControler extends HttpServlet {

    private EmployerService employerService;
    private UserService userService;
    private AdminService adminService;
    private User user;
    private String username;
    private String password;

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
        if (!user.getRol().equals("Empleador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/cargar-ofertas")) {
            List<Ofertas> ofertas = listarOfertasEmpresas(conexion, user.getCodigo());
            userService.enviarJson(resp, ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/cargar-ofertas-fecha")) {
            java.sql.Date fechaA = java.sql.Date.valueOf(req.getParameter("fechaA"));
            java.sql.Date fechaB = java.sql.Date.valueOf(req.getParameter("fechaB"));
            String fechaS = req.getParameter("fechaS");

            List<Ofertas> ofertas = listarOfertasEmpresaFecha(conexion,fechaA, fechaB, fechaS, user.getCodigo()) ;
            userService.enviarJson(resp, ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/cargar-ofertas-postulantes")) {
            System.out.println(user.getCodigo());
            List<Ofertas> ofertas = listarOfertaPostulaciones(conexion, user.getCodigo());
            System.out.println(ofertas);
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-categorias")) {
            List<Categoria> categorias = listarCategorias(conexion);
            userService.enviarJson(resp, categorias);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-modalidades")) {
            List<Modalidades> modalidad = listarModalidad(conexion);
            userService.enviarJson(resp, modalidad);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-tarjeta")) {
            TarjetaDatos tarjetas = listarTarjetaEmpresa(conexion, user.getCodigo());
            userService.enviarJson(resp, tarjetas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if(uri.endsWith("/listar-oferta")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            Ofertas oferta = listarOfetaPostulacion(conexion, codigo);
            userService.enviarJson(resp, oferta);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-usuario-especifico")) {
            Usuarios usuario = listarUsuario(conexion, user.getCodigo());
            userService.enviarJson(resp,usuario);
        }

        if (uri.endsWith("/listar-telefonos-usuario-especifico")) {
            List<NumTelefono> telefonos = listarTelefonos(conexion, user.getCodigo());
            userService.enviarJson(resp,telefonos);
        }

        if(uri.endsWith("/listar-curriculum")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            ApplicantService applicantService = new ApplicantService(conexion);
            UsuarioPdf usuarioPdf = applicantService.listarCurriculum(codigo);
            System.out.println("usuario :" + codigo);
            try (OutputStream out = resp.getOutputStream()) {
                // Convierte los bytes del Blob a un InputStream
                ByteArrayInputStream inputStream = new ByteArrayInputStream(usuarioPdf.getPdfBytes());

                // Copia el contenido del InputStream al OutputStream de la respuesta
                IOUtils.copy(inputStream, out);
                System.out.println("Salida : " + out);
            } catch (IOException e) {
                throw new ServletException("Error al enviar el PDF al cliente", e);
            }
            System.out.println("Pdf : "+ usuarioPdf);
            resp.setContentType("application/pdf");
            resp.setStatus(HttpServletResponse.SC_OK);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Empleador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/completar-informacion")) {
            CompletarInformacionEmployer completarInformacionEmployer = (CompletarInformacionEmployer) userService.leerJson(resp, req, CompletarInformacionEmployer.class);
            if (completarInformacionEmployer == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            assert completarInformacionEmployer != null;
            if (!completarInformacion(conexion, completarInformacionEmployer.getMision(), completarInformacionEmployer.getVision(), user.getCodigo())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/completar-informacion-tarjeta")) {
            CompletarInformacionEmployerTarjeta completarInformacionEmployerTarjeta = (CompletarInformacionEmployerTarjeta) userService.leerJson(resp, req, CompletarInformacionEmployerTarjeta.class);
            if (completarInformacionEmployerTarjeta == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (!completarInformacionTarjeta(conexion, user.getCodigo(), user.getCodigo(), completarInformacionEmployerTarjeta.getTitular(),completarInformacionEmployerTarjeta.getNumero(),completarInformacionEmployerTarjeta.getCodigoSeguridad(),completarInformacionEmployerTarjeta.getFechaExpiracion(), completarInformacionEmployerTarjeta.getCantidad())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/crear-oferta")) {
            Ofertas ofertas = (Ofertas) userService.leerJson(resp, req, Ofertas.class);
            if (ofertas == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            assert ofertas != null;
            if (!crearOferta(conexion, ofertas.getNombre(),ofertas.getDescripcion(),user.getCodigo(),ofertas.getCategoria(),ofertas.getEstado(),ofertas.getFechaPublicacion(),ofertas.getFechaLimite(),ofertas.getSalario(),ofertas.getModalidad(),ofertas.getUbicacion(),ofertas.getDetalles(),ofertas.getUsuarioElegido())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/crear-telefonos-usuario")) {
            List<TelefonosUsuario> telefono = readJsonTelefonosUsuario(resp,req);
            if (telefono == null)  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            adminService =new AdminService(conexion);
            assert telefono != null;
            adminService.crearTelefonosUsarioP2(telefono);

        }



    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Empleador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();



        if (uri.endsWith("/actualizar-oferta")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            Ofertas ofertas = (Ofertas) userService.leerJson(resp, req, Ofertas.class);
            if (ofertas == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Oferta : "+ ofertas);
            assert ofertas != null;
            if(!actualizarOfeta(conexion, ofertas.getNombre(),ofertas.getDescripcion(),ofertas.getEmpresa(),ofertas.getCategoria(),ofertas.getEstado(),ofertas.getFechaPublicacion(),ofertas.getFechaLimite(),ofertas.getSalario(),ofertas.getModalidad(),ofertas.getUbicacion(),ofertas.getDetalles(),ofertas.getUsuarioElegido(),ofertas.getCodigo())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (uri.endsWith("/actualizar-usuario")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            User usuario = (User) userService.leerJson(resp,req,User.class);
            if (usuario == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            adminService = new AdminService(conexion);
            assert usuario != null;
            if (!adminService.actualizarUsuario(usuario)) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (uri.endsWith("/actualizar-tarjeta")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            CompletarInformacionEmployerTarjeta tarjeta = (CompletarInformacionEmployerTarjeta) userService.leerJson(resp,req,CompletarInformacionEmployerTarjeta.class);
            if (tarjeta == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (!actualizarInformacionTarjeta(conexion,tarjeta, user.getCodigo())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (uri.endsWith("/actualizar-telefonos")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            List<NumTelefono> telefonos = readJsonTelefonos(resp,req);
            if (telefonos == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            adminService = new AdminService(conexion);
            assert telefonos != null;
            if (!adminService.actualizarTelefono(telefonos)) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
        if (!user.getRol().equals("Empleador")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/eliminar-oferta")) {
            int codigo = Integer.parseInt(req.getParameter("codigo"));
            employerService =new EmployerService(conexion);
            employerService.eliminarOfetas(codigo);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
    }

    private boolean crearOferta(Connection conexion, String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido){
        employerService = new EmployerService(conexion);
        return employerService.crearOferta(nombre,descripcion,empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
    }

    private boolean actualizarOfeta(Connection conexion, String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido, int codigo){
        employerService = new EmployerService(conexion);
        return employerService.actualizarOferta(nombre,descripcion,empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido, codigo);
    }

    public List<NumTelefono> listarTelefonos(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarTelefonos(codigo);
    }

    public Usuarios listarUsuario(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarUsuarioE(codigo);
    }
    private boolean completarInformacion(Connection conexion, String mision, String vision, int id){
        employerService = new EmployerService(conexion);
        System.out.println("mision : " + mision + " vision : " + vision + " id : " +id );
        return employerService.completarInformacion(mision,vision,id);
    }

    private boolean completarInformacionTarjeta(Connection conexion,int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad, java.sql.Date fechaExpiracion, BigDecimal catidad){
        employerService = new EmployerService(conexion);
        return employerService.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad, fechaExpiracion, catidad);
    }

    private boolean actualizarInformacionTarjeta(Connection conexion, CompletarInformacionEmployerTarjeta tarjeta, int usuario){
        employerService = new EmployerService(conexion);
        return employerService.actualizarInformacionTarjeta(tarjeta, usuario);
    }

    public List<Ofertas> listarOfertasEmpresaFecha(Connection conexion, java.sql.Date FechaA, java.sql.Date FechaB, String FechaS, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarOfertasEmpresaFecha(FechaA, FechaB, FechaS, empresa);
    }

    public List<Ofertas> listarOfertasEmpresas(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarOfertasEmpresa(empresa);
    }

    public List<Ofertas> listarOfertaPostulaciones(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarOfertasEmpresaPos(empresa);
    }

    public TarjetaDatos listarTarjetaEmpresa(Connection conexion, int empresa){
        employerService = new EmployerService(conexion);
        return employerService.listarTarjetaEmpresa(empresa);
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

    public List<Categoria> listarCategorias(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarCategorias();
    }

    private Ofertas listarOfetaPostulacion(Connection conexion, int codigo){
        employerService = new EmployerService(conexion);
        return employerService.listarOfetaCodigo(codigo);
    }

    public List<Modalidades> listarModalidad(Connection conexion){
        employerService = new EmployerService(conexion);
        return employerService.listarModalidades();
    }

}
