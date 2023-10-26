package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.User.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class SesionDB {

    private final Connection conexion;

    public SesionDB(Connection conexion){  this.conexion = conexion;}

    public Optional<User> obtenerUsuario(String username, String password, String email) {
        System.out.println("Obteniendo usuario");
        String query = "SELECT * FROM usuarios WHERE username = ? AND password = ? OR email = ? AND password = ? ";
        User user = null;

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);


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
                    user = new User(codigo, nombre, direccion, username,password,email,CUI,fechaFundacion,fechaNacimiento,curriculum,rol,mision,vision);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }

        return Optional.ofNullable(user);
    }

}
