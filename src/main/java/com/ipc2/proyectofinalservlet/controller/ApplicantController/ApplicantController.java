package com.ipc2.proyectofinalservlet.controller.ApplicantController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Admin.TelefonosUsuario;
import com.ipc2.proyectofinalservlet.model.Applicant.*;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.Employer.Modalidades;
import com.ipc2.proyectofinalservlet.model.Employer.NumTelefono;
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
import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ApplicantManagerServlet", urlPatterns = {"/v1/applicant-servlet/*"})
public class ApplicantController extends HttpServlet{
    private ApplicantService applicantService;
    private UserService userService ;
    private String username;
    private String password;
    private User user;
    private AdminService adminService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];

        System.out.println(Arrays.toString(parts));
        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Solicitante")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if(uri.endsWith("/listar-ofertas")) {
            List<OfertasEmpresa> ofertas = listarOfertasEmpleo(conexion, user.getCodigo());
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-ofertas-sugerencias")) {
            System.out.println("codigo : " + user.getCodigo());
            List<OfertasEmpresa> ofertas = listarOfertasSugerencia(conexion, user.getCodigo());
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-categorias")) {
            List<Categoria> categorias = listarCategorias(conexion);
            userService.enviarJson(resp,categorias);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-ubicaciones")) {
            List<Ubicacion> ubicaciones = listarUbicaciones(conexion);
            userService.enviarJson(resp,ubicaciones);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/listar-salarios")) {
            List<Salario> salarios = listarSalarios(conexion);
            userService.enviarJson(resp,salarios);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        if (uri.endsWith("/listar-modalidades")) {
            List<Modalidades> modalidad = listarModalidad(conexion);
            userService.enviarJson(resp,modalidad);
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
        if (!user.getRol().equals("Solicitante")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/completar-informacion")) {
                CompletarInformacionApplicant completarInformacionApplicant = (CompletarInformacionApplicant) userService.leerJson(resp, req, CompletarInformacionApplicant.class);
                if (completarInformacionApplicant == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                assert completarInformacionApplicant != null;
                    if (!completarInformacion(conexion, completarInformacionApplicant.getCurriculum(), user.getCodigo()))resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                for (Integer categoria : completarInformacionApplicant.getCategorias()) {
                    crearCategorias(conexion, user.getCodigo(), categoria);
                }
                resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/completar-informacion-tarjeta")) {
            CompletarInformacionEmployerTarjeta completarInformacionEmployerTarjeta = (CompletarInformacionEmployerTarjeta) userService.leerJson(resp, req, CompletarInformacionEmployerTarjeta.class);
            if (completarInformacionEmployerTarjeta == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (!completarInformacionTarjeta(conexion, user.getCodigo(), user.getCodigo(), completarInformacionEmployerTarjeta.getTitular(),completarInformacionEmployerTarjeta.getNumero(),completarInformacionEmployerTarjeta.getCodigoSeguridad(),completarInformacionEmployerTarjeta.getFechaExpiracion())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/aplicar-oferta")) {
            Solicitudes solicitudes = (Solicitudes) userService.leerJson(resp, req, Solicitudes.class);
            if (solicitudes == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            assert solicitudes != null;
            if (!aplicarOferta(conexion, solicitudes.getCodigoOferta(), user.getCodigo(), solicitudes.getMensaje())) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

        if (uri.endsWith("/buscar-empresa")) {
            Filtros filtros = (Filtros) userService.leerJson(resp, req, Filtros.class);
            if (filtros == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Filtros  : "+filtros);
            List<OfertasEmpresa> ofertas = listarOfertasFiltros(conexion, filtros,user.getCodigo());
            userService.enviarJson(resp,ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if(uri.endsWith("/listar-ofertas-filtros")) {
            Filtros filtros = (Filtros) userService.leerJson(resp, req, Filtros.class);
            if (filtros == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Filtros  : "+filtros);
            List<OfertasEmpresa> ofertas = listarOfertasFiltros(conexion, filtros, user.getCodigo());
            userService.enviarJson(resp, ofertas);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/crear-telefonos-usuario")) {
            List<TelefonosUsuario> telefono = readJsonTelefonosUsuario(resp,req);
            adminService =new AdminService(conexion);
            adminService.crearTelefonosUsarioP2(telefono);

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Conexion conectar = new Conexion();
        Connection conexion = conectar.obtenerConexion();

        String authorizationHeader = req.getHeader("Authorization");

        userService = new UserService(conexion);
        String[] parts = userService.autorizacion(authorizationHeader,resp);
        username = parts[0];
        password = parts[1];


        user = userService.validarUsuario(conexion,username,password,username);
        if (!user.getRol().equals("Solicitante")) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        String uri = req.getRequestURI();

        if (uri.endsWith("/actualizar-usuario")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            User usuario = (User) userService.leerJson(resp,req,User.class);
            if (usuario == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            adminService = new AdminService(conexion);
            assert usuario != null;
            if (!adminService.actualizarUsuario(usuario)) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (uri.endsWith("/actualizar-telefonos")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            List<NumTelefono> telefonos = readJsonTelefonos(resp,req);
            if (telefonos == null) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            adminService = new AdminService(conexion);
            assert telefonos != null;
            if (!adminService.actualizarTelefono(telefonos))resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public List<Categoria> listarCategorias(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarCategorias();
    }

    public List<OfertasEmpresa> listarOfertasSugerencia(Connection conexion, int codigo){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfertasSugerencia(codigo);
    }
    public List<OfertasEmpresa> listarOfertasFiltros(Connection conexion, Filtros filtros, int codigo){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfertasFiltrados(filtros,codigo);
    }

    public List<Salario> listarSalarios(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarSalarios();
    }

    public List<Modalidades> listarModalidad(Connection conexion){
        EmployerService employerService = new EmployerService(conexion);
        return employerService.listarModalidades();
    }

    public List<Ubicacion> listarUbicaciones(Connection conexion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarUbicaciones();
    }

    private boolean completarInformacion(Connection conexion, String curriculum, int usuario){
        applicantService = new ApplicantService(conexion);
        return applicantService.completarInformacion(curriculum,usuario);
    }

    private void crearCategorias(Connection conexion, int usuario, int categoria){
        applicantService = new ApplicantService(conexion);
        applicantService.crearCategoria(usuario,categoria);
    }



    private boolean aplicarOferta(Connection conexion, int oferta, int usuario, String mensaje){
        applicantService = new ApplicantService(conexion);
        return applicantService.aplicarOferta(oferta,usuario,mensaje);
    }

    private List<OfertasEmpresa> listarOfertasEmpleo(Connection conexion, int usuarion){
        applicantService = new ApplicantService(conexion);
        return applicantService.listarOfeta(usuarion);
    }
    private boolean completarInformacionTarjeta(Connection conexion,int codigo, int codigoUsuario, String Titular,int numero,int codigoSeguridad, Date fechaExpiracion){
        applicantService = new ApplicantService(conexion);
        return applicantService.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad, fechaExpiracion);
    }

    public Usuarios listarUsuario(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarUsuarioE(codigo);
    }

    public List<NumTelefono> listarTelefonos(Connection conexion, int codigo){
        adminService = new AdminService(conexion);
        return adminService.listarTelefonos(codigo);
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


}
