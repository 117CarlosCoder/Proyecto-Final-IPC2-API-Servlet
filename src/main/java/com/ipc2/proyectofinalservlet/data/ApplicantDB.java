package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Entrevista;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Solicitudes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicantDB    {
    private Connection conexion;
    public ApplicantDB(Connection conexion){
        this.conexion = conexion;
    }

    public void completarInformacion(String curriculum, int usuario){
        String query = "UPDATE  usuarios SET curriculum = ? WHERE codigo = ?";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1,curriculum);
            preparedStatement.setInt(2,usuario);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearCategorias( int usuario,int categoria){
        String query = "INSERT INTO categoriaUsuario VALUES(null,?,?) ";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,usuario);
            preparedStatement.setInt(2,categoria);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aplicarOferta( int oferta,int usuario,String mensaje ){
        String query = "INSERT INTO solicitudes VALUES(null,?,?,?) ";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,oferta);
            preparedStatement.setInt(2,usuario);
            preparedStatement.setString(3,mensaje);
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

    public Ofertas listarOfertasCodigo(int codigoN) {
        String query = "SELECT * FROM ofertas WHERE codigo = ?  ";
        Ofertas oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, codigoN);


            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
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
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return oferta;
    }


    public List<Solicitudes> listarPostulaciones(int usuarioN) {
        String query = "SELECT * FROM solicitudes WHERE usuario = ?";
        List<Solicitudes> solicitudes = new ArrayList<>();
        Solicitudes solicitud = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarioN);


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var usuario = resultSet.getInt("usuario");
                    var mensaje = resultSet.getString("mensaje");
                    solicitud = new Solicitudes(codigo,codigoOferta,usuario,mensaje
                    );
                    solicitudes.add(solicitud);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar especialidades: " + e);
        }

        return solicitudes;
    }

    public void eliminarPostulacion(int codigo){
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM solicitudes WHERE codigo = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public List<Entrevista> listarEntrevistas(int usuarioN) {
        String query = "SELECT * FROM entrevistas WHERE usuario = ? AND estado = 'PENDIENTE' ";
        List<Entrevista> entrevistas = new ArrayList<>();
        Entrevista entrevista = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1,usuarioN);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var usuario = resultSet.getInt("usuario");
                    var fecha = resultSet.getDate("fecha");
                    var hora = resultSet.getString("hora");
                    var ubicacion = resultSet.getString("ubicacion");
                    var estado = resultSet.getString("estado");
                    var notas = resultSet.getString("notas");
                    entrevista = new Entrevista(codigo,usuario,fecha,hora,ubicacion,estado,notas);
                    entrevistas.add(entrevista);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar solicitudes: " + e);
        }

        return entrevistas;
    }
}
