package com.ipc2.proyectofinalservlet.controller.UserController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdf;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdfJson;
import com.ipc2.proyectofinalservlet.model.CargarDatos.CargarDatosFinal;
import com.ipc2.proyectofinalservlet.model.User.User;
import com.ipc2.proyectofinalservlet.service.CargarDatosService;
import com.ipc2.proyectofinalservlet.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.http.entity.ContentType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

@WebServlet(name = "CargaManagerControler", urlPatterns = {"/v1/carga-servlet/*"})
@MultipartConfig

public class CargarDatosController extends HttpServlet {

    private UserService userService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conexion = new Conexion();
        Connection connection = conexion.obtenerConexion();
        List<User> users = listarUsuariosSinPDF(connection);

        try {
            userService = new UserService(connection);
            userService.enviarJson(resp, users);
        }catch (Exception e){
            System.out.println("Error al listar PDF "+ e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Conexion conexion = new Conexion();
        Connection connection = conexion.obtenerConexion();
        cargarDatosInicio(connection,new BigDecimal(150));

        String uri = req.getRequestURI();
        userService = new UserService(connection);

        if (uri.endsWith("/cargar-json")) {

           if (validarBase(connection)) {
               resp.setStatus(HttpServletResponse.SC_CONFLICT);
               return;
           }

        Part filePart = req.getPart("file");
        InputStream fileInputStream = filePart.getInputStream();
        Scanner scanner = new Scanner(fileInputStream, "UTF-8").useDelimiter("\\A");
        String fileContent = scanner.hasNext() ? scanner.next() : "";

        Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
                .create();
        CargarDatosFinal datos = null;
        JsonElement jsonElement = null;
        try {
            jsonElement = gson.fromJson(fileContent, JsonElement.class);
            System.out.println("Json Element : " + jsonElement);
        } catch (JsonSyntaxException e) {

        }

        try {
            System.out.println("carga");
            datos = gson.fromJson( jsonElement, CargarDatosFinal.class);
            System.out.println("Datos : " + datos);
            if (datos == null){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error al cargar datos: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        CargaDB carga = new CargaDB(connection);
            System.out.println("Cargando Servicios");

            try {

                assert datos != null;
                carga.crearCategorias(datos.getCategorias());
                carga.crearAdmin(datos.getAdmin());
                carga.cargarUsuariOEmpleador(datos.getEmpleadores());
                carga.cargarUsuarioSolicitante(datos.getUsuarios());
                carga.crearOferta(datos.getOfertas());

            } catch (Exception e) {
                System.out.println("Error al cargar datos: " + e.getMessage());
                System.out.println("error");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                varciarBase(connection);
                RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
                dispatcher.forward(req, resp);


            }
        }

        if (uri.endsWith("/cargar-pdfs")){
            List<UsuarioPdfJson> usuarioPdf  = readJsonUsuarioPdfs(resp,req);
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            guardarPfs(usuarioPdf, connection);
        }
    }

    public void cargarDatosInicio(Connection conexion, BigDecimal cantidad){
        CargarDatosService cargarDatosService = new CargarDatosService(conexion);
        if (!cargarDatosService.listarComision()){
            cargarDatosService.crearComision(cantidad);
        }
    }
    public List<User> listarUsuariosSinPDF(Connection conexion){
        CargarDatosService cargarDatosService = new CargarDatosService(conexion);
        return cargarDatosService.listarUsuariosPdf();
    }

    public void guardarPfs(List<UsuarioPdfJson> usuarioPdf, Connection connection){
        System.out.println("guardar pdfs");
        CargarDatosService cargarDatosService = new CargarDatosService(connection);
        cargarDatosService.guardarPdf( usuarioPdf);

    }

    public void varciarBase( Connection connection){
        System.out.println("Vaciar Base");
        CargarDatosService cargarDatosService = new CargarDatosService(connection);
        cargarDatosService.vaciarBSe( );

    }

    public boolean validarBase( Connection connection){
        System.out.println("Validar Base");
        CargarDatosService cargarDatosService = new CargarDatosService(connection);
        return cargarDatosService.validarBase( );

    }

    private List<UsuarioPdfJson> readJsonUsuarioPdfs(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        try {
            return objectMapper.readValue(req.getInputStream(), new TypeReference<>() {});
        } catch (JsonParseException | JsonMappingException e) {
            System.out.println(e); // Maneja estas excepciones según tus requisitos
            return null;  // O lanza una excepción adecuada
        }
    }



}
