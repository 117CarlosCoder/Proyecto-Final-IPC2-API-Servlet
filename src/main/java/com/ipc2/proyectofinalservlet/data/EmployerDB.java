package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.CargarDatos.Entrevista;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Postulante;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Solicitudes;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.sql.Types.NULL;

public class EmployerDB {
    private final Connection conexion;

    public EmployerDB(Connection conexion){
        this.conexion = conexion;
    }

    public void completarInformacion(String mision, String vision, int codigo){
        String query = "UPDATE usuarios SET mision = ?, vision = ? WHERE codigo = ?";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1,mision);
            preparedStatement.setString(2,vision);
            preparedStatement.setInt(3,codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void completarInformacionTarjeta(int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad){
        String query = "INSERT INTO tarjeta VALUES(?,?,?,?,?)";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,codigo);
            preparedStatement.setInt(2,codigoUsuario);
            preparedStatement.setString(3,Titular);
            preparedStatement.setInt(4,numero);
            preparedStatement.setInt(5,codigoSeguridad);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido){
        String query = "INSERT INTO ofertas VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, NULL);
            preparedStatement.setString(2,nombre);
            preparedStatement.setString(3,descripcion);
            preparedStatement.setInt(4,empresa);
            preparedStatement.setInt(5,categoria);
            preparedStatement.setString(6,estado);
            preparedStatement.setDate(7, (java.sql.Date) fechaPublicacion);
            preparedStatement.setDate(8, (java.sql.Date) fechaLimite);
            preparedStatement.setBigDecimal(9,salario);
            preparedStatement.setString(10,modalidad);
            preparedStatement.setString(11,ubicacion);
            preparedStatement.setString(12,detalles);
            preparedStatement.setInt(13,usuarioElegido);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido, int codigo){
        String query = "UPDATE ofertas SET nombre = ?, descripcion = ? , empresa = ?, categoria = ?, estado = ?, fechaPublicacion = ?, fechaLimite = ?, salario = ?, modalidad = ?, ubicacion = ?, detalles = ? , usuarioElegido = ? WHERE codigo = ?";

        try(var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1,nombre);
            preparedStatement.setString(2,descripcion);
            preparedStatement.setInt(3,empresa);
            preparedStatement.setInt(4,categoria);
            preparedStatement.setString(5,estado);
            preparedStatement.setDate(6, (java.sql.Date) fechaPublicacion);
            preparedStatement.setDate(7, (java.sql.Date) fechaLimite);
            preparedStatement.setBigDecimal(8,salario);
            preparedStatement.setString(9,modalidad);
            preparedStatement.setString(10,ubicacion);
            preparedStatement.setString(11,detalles);
            preparedStatement.setInt(12,usuarioElegido);
            preparedStatement.setInt(13, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarOfertaEstado(int codigo){
        String query = "UPDATE ofertas SET estado = 'ENTREVISTA' WHERE codigo = ?";

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ofertas> listarOfertas() {
        String query = "SELECT * FROM ofertas";
        List<Ofertas> ofertas = new ArrayList<>();
        Ofertas oferta = null;
        try (var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var nombre = resultset.getString("nombre");
                var descripcion = resultset.getString("descripcion");
                var empresa = resultset.getInt("empresa");
                var categoria = resultset.getInt("categoria");
                var estado = resultset.getString("estado");
                var fechaPublicacion = resultset.getDate("fechaPublicacion");
                var fechaLimite = resultset.getDate("fechaLimite");
                var salario = resultset.getBigDecimal("salario");
                var modalidad = resultset.getString("salario");
                var ubicacion = resultset.getString("ubicacion");
                var detalles = resultset.getString("detalles");
                var usuarioElegido = resultset.getInt("usuarioElegido");
                oferta = new Ofertas(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                ofertas.add(oferta);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ofertas;
    }

    public List<Ofertas> listarOfertasEmpresa(int numEmpresa) {
        String query = "SELECT * FROM ofertas WHERE empresa = ? AND estado = 'ACTIVA' OR empresa = ? AND estado = 'SELECCION' ";
        List<Ofertas> ofertas = new ArrayList<>();
        Ofertas oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, numEmpresa);
            preparedStatement.setInt(2, numEmpresa);


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var descripcion = resultSet.getString("descripcion");
                    var empresa = resultSet.getInt("empresa");
                    var categoria = resultSet.getInt("categoria");
                    var estado = resultSet.getString("estado");
                    var fechaPublicacion = resultSet.getDate("fechaPublicacion");
                    var fechaLimite = resultSet.getDate("fechaLimite");
                    var salario = resultSet.getBigDecimal("salario");
                    var modalidad = resultSet.getString("salario");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new Ofertas(codigo,nombre,descripcion,empresa,categoria,estado,fechaPublicacion,fechaLimite,salario,modalidad,ubicacion,detalles,usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return ofertas;
    }

    public List<Ofertas> listarOfertasEmpresaPostulantes(int numEmpresa) {
        String query = "SELECT * FROM ofertas WHERE estado = 'SELECCION' AND  empresa = ?";
        List<Ofertas> ofertas = new ArrayList<>();
        Ofertas oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1,numEmpresa);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var descripcion = resultSet.getString("descripcion");
                    var empresa = resultSet.getInt("empresa");
                    var categoria = resultSet.getInt("categoria");
                    var estado = resultSet.getString("estado");
                    var fechaPublicacion = resultSet.getDate("fechaPublicacion");
                    var fechaLimite = resultSet.getDate("fechaLimite");
                    var salario = resultSet.getBigDecimal("salario");
                    var modalidad = resultSet.getString("salario");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new Ofertas(codigo,nombre,descripcion,empresa,categoria,estado,fechaPublicacion,fechaLimite,salario,modalidad,ubicacion,detalles,usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar ofertasPostulantes: " + e);
        }

        return ofertas;
    }

    public void eliminarOfertas(int codigo){
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM ofertas WHERE codigo = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public List<Solicitudes> listarPostulantes(int numOferta) {
        String query = "SELECT * FROM solicitudes WHERE codigoOferta = ?";
        List<Solicitudes> solicitudes = new ArrayList<>();
        Solicitudes solicitud = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1,numOferta);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var usuario = resultSet.getInt("usuario");
                    var mensaje = resultSet.getString("mensaje");
                    solicitud = new Solicitudes(codigo,codigoOferta,usuario,mensaje);
                    solicitudes.add(solicitud);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar solicitudes: " + e);
        }

        return solicitudes;
    }

    public Postulante obtenerPostulante(int usuario) {
        System.out.println("Obteniendo usuario");
        String query = "SELECT usuarios.*, solicitudes.mensaje FROM solicitudes INNER JOIN usuarios ON solicitudes.usuario = usuarios.codigo WHERE solicitudes.usuario = ?";
        Postulante postulante = null;

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuario);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var email = resultSet.getString("email");
                    var curriculum = resultSet.getString("curriculum");
                    var mensaje = resultSet.getString("mensaje");
                    postulante = new Postulante(codigo,nombre,email,curriculum,mensaje);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }

        return postulante;
    }

    public void generarEntrevista(int codigo, int usuario, Date fecha, String hora, String ubicacion){
        String query = "INSERT INTO entrevistas VALUES(?,?,?,?,?,?,?)";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(hora));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Time time = new Time(date.getTime());

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.setInt(2, usuario);
            preparedStatement.setDate(3, (java.sql.Date) fecha);
            preparedStatement.setTime(4, time);
            preparedStatement.setString(5,ubicacion);
            preparedStatement.setString(6,"PENDIENTE");
            preparedStatement.setString(7,"");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Entrevista> listarEntrevista() {
        String query = "SELECT * FROM entrevistas";
        List<Entrevista> entrevistas = new ArrayList<>();
        Entrevista entrevista = null;
        try (var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var usuario = resultset.getInt("usuario");
                var fecha = resultset.getDate("fecha");
                var hora = resultset.getString("hora");
                var ubicacion = resultset.getString("ubicacion");
                var estado = resultset.getString("estado");
                var notas = resultset.getString("notas");
                entrevista = new Entrevista(codigo,usuario,fecha,hora,ubicacion,estado,notas);
                entrevistas.add(entrevista);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entrevistas;
    }

    public void finalizarEntrevista(String notas, int usuario, int codigo){
        String query = "UPDATE entrevistas SET notas = ?, estado = ? WHERE usuario = ? AND codigo = ?";

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, notas);
            preparedStatement.setString(2, "FINALIZADO");
            preparedStatement.setInt(3, usuario);
            preparedStatement.setInt(4, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
