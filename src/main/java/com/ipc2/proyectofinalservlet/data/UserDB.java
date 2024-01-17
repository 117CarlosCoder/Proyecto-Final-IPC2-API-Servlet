package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.controller.UserController.Encriptador;
import com.ipc2.proyectofinalservlet.model.Applicant.ActualizarContrasena;
import com.ipc2.proyectofinalservlet.model.Applicant.RegistroPostulacion;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.User.Notificaciones;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jdt.internal.compiler.apt.model.NameImpl;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDB {
    private final Connection conexion;

    public UserDB(Connection conexion) {
        this.conexion = conexion;
    }

    public void crearUsuarioSolicitante(User user, String contrasena, HttpServletResponse resp) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,NULL,?,NULL,?,NULL,NULL,false)";
        Encriptador encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, encriptador.encriptarContrasena(contrasena, sal));
            preparedStatement.setString(5, sal);
            preparedStatement.setString(6, user.getEmail());
            preparedStatement.setString(7, user.getCUI());
            preparedStatement.setDate(8, user.getFechaNacimiento());
            preparedStatement.setString(9, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearUsuarioSolicitanteAdmin(User user ) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,NULL,?,NULL,?,NULL,NULL,false)";
        Encriptador encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, encriptador.encriptarContrasena(user.getPassword(),sal));
            preparedStatement.setString(5,sal);
            preparedStatement.setString(6, user.getEmail());
            preparedStatement.setString(7, user.getCUI());
            preparedStatement.setDate(8, user.getFechaNacimiento());
            preparedStatement.setString(9, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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

    public void crearTelefonos(int telefono, String user) {
        System.out.println("creando telefonos");
        String query = "INSERT INTO telefonos VALUES(NULL,?,?)";

            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, listarCodigo(user));
                preparedStatement.setInt(2, telefono);
                preparedStatement.executeUpdate();
            }catch (SQLException e) {
                System.out.println("Error al crear telefono : " + e);
            }
    }

    public void crearUsuariOEmpleador(User user, String contrasena) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,NULL,NULL,?,NULL,NULL)";
        Encriptador encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, encriptador.encriptarContrasena(user.getPassword(), sal));
            preparedStatement.setString(5, sal);
            preparedStatement.setString(6, user.getEmail());
            preparedStatement.setString(7, user.getCUI());
            preparedStatement.setDate(8, user.getFechaFundacion());
            preparedStatement.setString(9, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearUsuariOEmpleadorAdmin(User user) {
        System.out.println("creando usuario");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,?,NULL,NULL,?,NULL,NULL,false)";
        Encriptador encriptador = new Encriptador();
        String sal =encriptador.generarSecuencia();
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNombre());
            preparedStatement.setString(2, user.getDireccion());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, encriptador.encriptarContrasena(user.getPassword(), sal));
            preparedStatement.setString(5, sal);
            preparedStatement.setString(6, user.getEmail());
            preparedStatement.setString(7, user.getCUI());
            preparedStatement.setDate(8, user.getFechaFundacion());
            preparedStatement.setString(9, user.getRol());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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

    public void restablecerContrasena(String email, String password) {
        System.out.println("creando usuario");
        String query = "UPDATE usuarios SET password = ? , sal = ? WHERE email = ?";
        Encriptador encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, encriptador.encriptarContrasena(password,sal));
            preparedStatement.setString(2, sal);
            preparedStatement.setString(3, email);

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void cambiarContrasena(ActualizarContrasena actualizarContrasena) {
        System.out.println("cambiar contrasena");
        String query = "UPDATE usuarios SET password = ?, sal = ? WHERE codigo = ?";
        Encriptador encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, encriptador.encriptarContrasena(actualizarContrasena.getContrasena(), sal));
            preparedStatement.setString(2,  sal);
            preparedStatement.setInt(3, actualizarContrasena.getCodigo());

            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
                    var sal = resultSet.getString("sal");
                    var direccion = resultSet.getString("direccion");
                    var CUI = resultSet.getString("CUI");
                    var fechaFundacion = resultSet.getDate("fechaFundacion");
                    var fechaNacimiento = resultSet.getDate("fechaNacimiento");
                    var curriculum = resultSet.getString("curriculum");
                    var rol = resultSet.getString("rol");
                    var mision = resultSet.getString("mision");
                    var vision = resultSet.getString("vision");
                    var suspension = resultSet.getBoolean("suspension");
                    user = new User(codigo,nombre,direccion,username,password,sal,email,CUI,fechaFundacion,fechaNacimiento,curriculum,rol,mision,vision,suspension);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }
        return user;
    }

    public List<Notificaciones> listarNotificaciones(int usuarios) {
        String query = "SELECT * FROM notificaciones WHERE codigoUsuario = ?";
        List<Notificaciones> notificaciones = new ArrayList<>();
        Notificaciones notificacion = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarios);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoEmpresa = resultSet.getInt("codigoEmpresa");
                    var codigoUsuario = resultSet.getInt("codigoUsuario");
                    var mensaje = resultSet.getString("mensaje");
                    notificacion = new Notificaciones(codigo,codigoEmpresa, codigoUsuario,mensaje);
                    notificaciones.add(notificacion);
                    System.out.println(notificacion);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar notificaciones: " + e);
        }

        return  notificaciones;
    }

    public void eliminarOfertas(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM ofertas WHERE empresa = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarSolicitu(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM solicitudes WHERE codigoOferta = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarSolicituUsu(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM solicitudes WHERE usuario = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarEntrevistas(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM entrevistas WHERE usuario = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarNotificaciones(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM notificaciones WHERE codigoUsuario = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarTarjeta(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM tarjeta WHERE codigoUsuario = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarCategoria(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM categoriaUsuario WHERE codigoUsuario = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }
}
