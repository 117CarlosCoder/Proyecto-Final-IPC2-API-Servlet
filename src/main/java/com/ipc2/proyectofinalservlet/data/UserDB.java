package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Applicant.RegistroPostulacion;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDB {
    private final Connection conexion;

    public UserDB(Connection conexion) {
        this.conexion = conexion;
    }

    public void crearUsuarioSolicitante(User user, String contrasena) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,NULL,?,NULL,?,NULL,NULL)";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, contrasena);
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setString(6, user.getCUI());
            preparedStatement.setDate(7, user.getFechaNacimiento());
            preparedStatement.setString(8, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
    }

    public void crearUsuarioSolicitanteAdmin(User user ) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,NULL,?,NULL,?,NULL,NULL)";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setString(6, user.getCUI());
            preparedStatement.setDate(7, user.getFechaNacimiento());
            preparedStatement.setString(8, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
    }

    public int listarCodigo(String username){
        String query = "SELECT u.codigo FROM usuarios u WHERE username = ?";
        int codigo = 0;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);


            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                     codigo = resultSet.getInt("codigo");
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar usuario: " + e);
        }
        return codigo;
    }

    public void eliminarUsuario(String username){
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM usuarios WHERE username = ?")) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarTelefonos(String username){

        try (var preparedStatement = conexion.prepareStatement("DELETE FROM telefonos WHERE codigoUsuario = ?")) {
            preparedStatement.setInt(1, listarCodigo(username));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void crearTelefonos(String telefono, User user) {
        System.out.println("creando telefonos");
        String query = "INSERT INTO telefonos VALUES(NULL,?,?)";


            try (var preparedStatement = conexion.prepareStatement(query)) {

                preparedStatement.setInt(1, listarCodigo(user.getUsername()));
                preparedStatement.setString(2, telefono);
                preparedStatement.executeUpdate();
            }catch (SQLException e) {
                System.out.println("Error al crear telefono : " + e);
            }





    }

    public void crearUsuariOEmpleador(User user, String contrasena) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,NULL,NULL,?,NULL,NULL)";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, contrasena);
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setString(6, user.getCUI());
            preparedStatement.setDate(7, user.getFechaFundacion());
            preparedStatement.setString(8, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
    }

    public void crearUsuariOEmpleadorAdmin(User user) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,NULL,NULL,?,NULL,NULL)";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setString(6, user.getCUI());
            preparedStatement.setDate(7, user.getFechaFundacion());
            preparedStatement.setString(8, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
    }

    public void crearUsuariOSolicitanteAdmin(User user) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,NULL,NULL,?,NULL,NULL)";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setString(6, user.getCUI());
            preparedStatement.setDate(7, user.getFechaNacimiento());
            preparedStatement.setString(8, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
    }

    public void restablecerContraseña(String email, String password) {
        System.out.println("creando usuario");
        String query = "UPDATE usuarios SET password = ? WHERE email = ?";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, password);
            preparedStatement.setString(2, email);

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
    }
    public User comprobarCorreo(String email) {
        String query = "SELECT * FROM usuarios WHERE email=?";
        User user = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, email);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var username = resultSet.getString("username");
                    var password = resultSet.getString("password");
                    var direccion = resultSet.getString("direccion");
                    var CUI = resultSet.getString("CUI");
                    var fechaFundacion = resultSet.getDate("fechaFundacion");
                    var fechaNacimiento = resultSet.getDate("fechaNacimiento");
                    var curriculum = resultSet.getString("curriculum");
                    var rol = resultSet.getString("rol");
                    var mision = resultSet.getString("mision");
                    var vision = resultSet.getString("vision");
                    user = new User(codigo,nombre,direccion,username,password,email,CUI,fechaFundacion,fechaNacimiento,curriculum,rol,mision,vision);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
        return user;
    }

}
