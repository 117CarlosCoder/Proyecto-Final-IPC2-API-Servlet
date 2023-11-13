package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Applicant.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.User.User;

import javax.lang.model.type.NullType;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    public void completarInformacionTarjeta(int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad, Date fechaExpiracion){
        String query = "INSERT INTO tarjeta VALUES(?,?,?,?,?,?,?)";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, Types.NULL  );
            preparedStatement.setInt(2,codigoUsuario);
            preparedStatement.setString(3,Titular);
            preparedStatement.setInt(4,numero);
            preparedStatement.setInt(5,codigoSeguridad);
            preparedStatement.setDate(6,fechaExpiracion);
            preparedStatement.setInt(7,0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Categoria> listarCategorias(){
        String query = "SELECT * FROM categoria";
        List<Categoria> categorias = new ArrayList<>();
        Categoria categoria = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var nombre = resultset.getString("nombre");
                var descripcion = resultset.getString("descripcion");
                categoria = new Categoria(codigo,nombre,descripcion);
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categorias;
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

    public List<OfertasEmpresa> listarOfertas() {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo ";
        List<OfertasEmpresa> ofertas = new ArrayList<>();
        OfertasEmpresa oferta = null;
        try (var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var nombre = resultset.getString("nombre");
                var descripcion = resultset.getString("descripcion");
                var empresa = resultset.getString("empresa");
                var categoria = resultset.getInt("categoria");
                var estado = resultset.getString("estado");
                var fechaPublicacion = resultset.getDate("fechaPublicacion");
                var fechaLimite = resultset.getDate("fechaLimite");
                var salario = resultset.getBigDecimal("salario");
                var modalidad = resultset.getString("salario");
                var ubicacion = resultset.getString("ubicacion");
                var detalles = resultset.getString("detalles");
                var usuarioElegido = resultset.getInt("usuarioElegido");
                oferta = new OfertasEmpresa(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
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


    public List<EstadoSolicitud> listarPostulaciones(int usuarioN) {
        String query = " SELECT s.codigo, s.codigoOferta, s.usuario, o.estado, o.nombre, u.nombre AS 'empresa'FROM solicitudes s JOIN ofertas o ON s.codigoOferta = o.codigo JOIN usuarios u ON o.empresa = u.codigo WHERE s.usuario = ?";
        List<EstadoSolicitud> solicitudes = new ArrayList<>();
        EstadoSolicitud solicitud = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarioN);


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var usuario = resultSet.getInt("usuario");
                    var estado = resultSet.getString("estado");
                    var nombre = resultSet.getString("nombre");
                    var empresa = resultSet.getString("empresa");
                    solicitud = new EstadoSolicitud(codigo,codigoOferta,usuario,estado,nombre,empresa);
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

    public List<EntrevitaN> listarEntrevistas(int usuarioN) {
        String query = "SELECT s.* ,o.nombre AS 'empresa' FROM entrevistas s JOIN ofertas o ON s.codigoOferta = o.codigo WHERE s.usuario = ? AND s.estado = 'PENDIENTE' ";
        List<EntrevitaN> entrevistas = new ArrayList<>();
        EntrevitaN entrevista = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1,usuarioN);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var usuario = resultSet.getInt("usuario");
                    var fechaSql = resultSet.getDate("fecha");
                    var formatter = new SimpleDateFormat("yyyy-MM-dd"); // Define el formato de fecha
                    var fecha = formatter.format(fechaSql);
                    var hora = resultSet.getString("hora");
                    var ubicacion = resultSet.getString("ubicacion");
                    var estado = resultSet.getString("estado");
                    var notas = resultSet.getString("notas");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var empresa = resultSet.getString("empresa");
                    entrevista = new EntrevitaN(codigo,usuario,fecha,hora,ubicacion,estado,notas,codigoOferta,empresa);
                    entrevistas.add(entrevista);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar solicitudes: " + e);
        }

        return entrevistas;
    }

    public List<OfertaCostos> listarOfertasCostos(int usuarioN) {
        String query = "SELECT e.codigo,o.nombre AS 'Oferta' ,u.nombre AS 'Empresa',e.estado,c.cantidad FROM comision c ,entrevistas e INNER JOIN ofertas o ON o.codigo =  e.codigoOferta INNER JOIN usuarios u ON u.codigo = o.empresa WHERE e.usuario = ?";
        List<OfertaCostos> ofertasCostos = new ArrayList<>();
        OfertaCostos ofertaCostos = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1,usuarioN);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var oferta = resultSet.getString("Oferta");
                    var empresa = resultSet.getString("Empresa");
                    var estado = resultSet.getString("estado");
                    var cantidad = resultSet.getInt("cantidad");
                    ofertaCostos = new OfertaCostos(codigo,oferta,empresa,estado,cantidad);
                    ofertasCostos.add(ofertaCostos);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar Ofertas Costos: " + e);
        }

        return ofertasCostos;
    }
}
