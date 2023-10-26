package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CargaDB {
    private final Connection conexion;

    public CargaDB(Connection conexion){  this.conexion = conexion;}

    public Comision crearComision(int cantidadE){
        String query = "INSERT INTO comision VALUES(1,?)";
        Comision comision = null;
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,cantidadE);
            comision = new Comision(1,cantidadE);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comision;
    }

    public Comision cambiarComision(int cantidadE){
        String query = "UPDATE comision SET cantidad = ? WHERE codigo = 1";
        Comision comision = null;
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,cantidadE);
            comision = new Comision(1,cantidadE);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comision;
    }

    public Comision listarComision(){
        String query = "SELECT * FROM comision";
        Comision comision = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            if (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var cantidad = resultset.getInt("cantidad");
                comision = new Comision(codigo,cantidad);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comision;
    }

}
