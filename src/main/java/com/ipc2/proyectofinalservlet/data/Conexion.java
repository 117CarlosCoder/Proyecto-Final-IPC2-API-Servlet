package com.ipc2.proyectofinalservlet.data;

import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public Connection obtenerConexion() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/empleo_database";
            String user = "newuser";
            String password = "password";
            Connection conexion = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa");
            return conexion;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error al registrar el driver de MySQL: " + e);
        }
        return null;
    }

    public void desconectar(Connection conexion) {

            try {
                conexion.close();
                System.out.println("Conexión cerrada");
            } catch (Exception e) {
                System.out.println("No se pudo cerrar la conexión");
                System.out.println(e);
            }

    }
}
