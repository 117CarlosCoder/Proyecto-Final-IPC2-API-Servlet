package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Admin.Admin;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CargaDB {
    private final Connection conexion;

    public CargaDB(Connection conexion){  this.conexion = conexion;}

    public void crearAdmin(Admin admin) {
        System.out.println("creando usuario administrador");
        String query = "INSERT INTO usuarios VALUES(?,?,?,?,?,?,?,?,NULL,NULL,NULL,NULL,NULL)";


            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, admin.getCodigo());
                preparedStatement.setString(2, admin.getNombre());
                preparedStatement.setString(3, admin.getDireccion());
                preparedStatement.setString(4, admin.getUsername());
                preparedStatement.setString(5, admin.getPassword());
                preparedStatement.setString(6, admin.getEmail());
                preparedStatement.setString(7,admin.getCUI());
                preparedStatement.setDate(8,admin.getFechaNacimiento());
                preparedStatement.executeUpdate();
                System.out.println("Admin creado");
            } catch (SQLException e) {
                System.out.println("Error al crear Admin: " + e);
            }

    }

    public void crearUsuarioSolicitante(List<User> usu) {
        System.out.println("creando usuario solicitante");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,NULL,?,NULL,?,NULL,NULL)";

        for (User user : usu) {


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
            } catch (SQLException e) {
                System.out.println("Error al consultar: " + e);
            }
        }
    }

    public void crearUsuariOEmpleador(List<User> usu) {
        System.out.println("creando usuario empleador");
        String query = "INSERT INTO usuarios VALUES(NULL,?,?,?,?,?,?,?,NULL,NULL,?,NULL,NULL)";

        for (User user : usu) {
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
            } catch (SQLException e) {
                System.out.println("Error al consultar: " + e);
            }
        }
    }

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
