package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Admin.Dashboard;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminDB {

    private Connection conexion;
    public AdminDB(Connection conexion){ this.conexion = conexion;}


    public void crearCartegoria(int codigo,String nombre, String descripcion){
        String query = "INSERT INTO categoria VALUES(?,?,?)";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, Types.NULL);
            preparedStatement.setString(2,nombre);
            preparedStatement.setString(3,descripcion);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Categoria cambiarCategoria(int codigoN, String nombreN, String descripcionN){
        String query = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE codigo = ?";
        Categoria categoria = null;
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1,nombreN);
            preparedStatement.setString(2,descripcionN);
            preparedStatement.setInt(3,codigoN);
            categoria = new Categoria(codigoN,nombreN,descripcionN);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoria;
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

    public Comision listarComision(){
        String query = "SELECT * FROM comision";
        Comision comision = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var cantidad = resultset.getInt("cantidad");
                comision = new Comision(codigo,cantidad);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comision;
    }

    public void actualizarComision(int cantidadN){
        String query = "UPDATE comision SET cantidad = ?";
        Comision comision = null;
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,cantidadN);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Categoria listarCategoriasCodigo(int codigoN) {
        String query = "SELECT * FROM categoria WHERE codigo = ?";
        Categoria categoria = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, codigoN);


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var descripcion = resultSet.getString("descripcion");
                    categoria = new Categoria(codigo,nombre,descripcion);;
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar especialidades: " + e);
        }

        return categoria;
    }
    public Dashboard dashboardVista (){
        String query = "SELECT (SELECT COUNT(*) FROM usuarios WHERE rol = 'Empleador') AS cantidad_empleadores, (SELECT COUNT(*) FROM usuarios WHERE rol = 'Solicitante') AS cantidad_solicitantes, COALESCE(SUM(c.cantidad), 0) AS cantidad_cobros, COALESCE((SELECT SUM(vista) FROM invitado), 0) AS total_vistas FROM cobros c";
        Dashboard dashboard = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var cantidad_empleadores = resultset.getInt("cantidad_empleadores");
                var cantidad_solicitantes = resultset.getInt("cantidad_solicitantes");
                var cantidad_cobros= resultset.getInt("cantidad_cobros");
                var total_vistas= resultset.getInt("total_vistas");
                dashboard = new Dashboard(cantidad_empleadores,cantidad_solicitantes,cantidad_cobros, total_vistas);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dashboard;
    }

}
