package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminDB {

    private Connection conexion;
    public AdminDB(Connection conexion){ this.conexion = conexion;}


    public void crearCartegoria(int codigo,String nombre, String descripcion){
        String query = "INSERT INTO categoria VALUES(?,?,?)";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,codigo);
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

}
