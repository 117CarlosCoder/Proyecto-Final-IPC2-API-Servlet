package com.ipc2.proyectofinalservlet.controller.UserController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.data.Conexion;
import com.ipc2.proyectofinalservlet.model.CargarDatos.CargarDatosFinal;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Scanner;

@WebServlet(name = "CargaManagerControler", urlPatterns = {"/v1/carga-servlet/*"})
@MultipartConfig

public class CargarDatosController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Conexion conexion = new Conexion();
        session.setMaxInactiveInterval(3600);
        session.setAttribute("conexion", conexion.obtenerConexion());
        session.setAttribute("valorconexion", conexion);
        Connection connection = (Connection) session.getAttribute("conexion");

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
        ;

        System.out.println("Calculando error2");

        try {
            System.out.println("carga1");
            System.out.println("carga2");
            System.out.println("carga3");
            datos = gson.fromJson( jsonElement, CargarDatosFinal.class);
            System.out.println("Datos : " + datos);
        } catch (Exception e) {
            System.out.println("Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }

        CargaDB carga = new CargaDB(connection);
            System.out.println("Cargando Servicios");

            try {

                assert datos != null;
                carga.crearCategorias(datos.getCategorias());
                carga.crearAdmin(datos.getAdmin());
                carga.crearUsuariOEmpleador(datos.getEmpleadores());
                carga.crearUsuarioSolicitante(datos.getUsuarios());
                carga.crearOferta(datos.getOfertas());

                /*if (carga.errorEncontrado()){
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
                    dispatcher.forward(req, resp);
                    return;
                }*/
            } catch (Exception e) {
                System.out.println("Error al cargar datos: " + e.getMessage());
                System.out.println("error");
                RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
                dispatcher.forward(req, resp);


            }
    }
}
