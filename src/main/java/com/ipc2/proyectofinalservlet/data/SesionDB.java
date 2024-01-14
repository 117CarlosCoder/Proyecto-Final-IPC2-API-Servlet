package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.controller.UserController.Encriptador;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class SesionDB {

    private final Connection conexion;

    public SesionDB(Connection conexion){  this.conexion = conexion;}

    public String obtenerSal(String username){
        String query = "SELECT sal FROM usuarios WHERE username = ?";
        String sal = "";
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    sal = resultSet.getString("sal");
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
        System.out.println("sal : " + sal);
        return sal;
    }

    public Optional<User> obtenerUsuario(String username, String password, String email) {
        System.out.println("Obteniendo usuario");
        String query = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND suspension = false OR email = ? AND password = ? AND suspension = false";
        User user = null;
        Encriptador encriptador = new Encriptador();
        String sal = obtenerSal(username);
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, encriptador.encriptarContrasena( password, sal));
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, encriptador.encriptarContrasena( password, sal));


            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var direccion = resultSet.getString("direccion");
                    var CUI = resultSet.getString("CUI");
                    var fechaFundacion = resultSet.getDate("fechaFundacion");
                    var fechaNacimiento = resultSet.getDate("fechaNacimiento");
                    var curriculum = resultSet.getString("curriculum");
                    var rol = resultSet.getString("rol");
                    var mision = resultSet.getString("mision");
                    var vision = resultSet.getString("vision");
                    var suspension = resultSet.getBoolean("suspension");
                    user = new User(codigo, nombre, direccion, username,password,sal,email,CUI,fechaFundacion,fechaNacimiento,curriculum,rol,mision,vision,suspension);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(user);
    }

}
