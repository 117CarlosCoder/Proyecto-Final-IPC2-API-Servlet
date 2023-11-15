package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Admin.Admin;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.Employer.Employer;
import com.ipc2.proyectofinalservlet.model.Employer.OfertasCarga;
import com.ipc2.proyectofinalservlet.model.Employer.Tarjeta;
import com.ipc2.proyectofinalservlet.model.Employer.Telefonos;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Types.DATE;
import static java.sql.Types.NULL;

public class CargaDB {
    private final Connection conexion;

    public CargaDB(Connection conexion){  this.conexion = conexion;}


    public void crearCategorias(List<Categoria> categorias){
        String query = "INSERT INTO categoria VALUES(null,?,?) ";
        if (categorias != null){
            for (Categoria cat:categorias) {
                try(var preparedStatement = conexion.prepareStatement(query)) {
                    preparedStatement.setString(1,cat.getNombre());
                    preparedStatement.setString(2, cat.getDescripcion());
                    preparedStatement.executeUpdate();
                    System.out.println("Categoria creada");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }


    }
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

    public void crearUsuarioSolicitante(List<Usuarios> usu) {
        System.out.println("creando usuario solicitante");
        String query = "INSERT INTO usuarios VALUES(?,?,?,?,?,?,?,NULL,?,NULL,?,NULL,NULL)";

        for (Usuarios user : usu) {


            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, user.getCodigo());
                preparedStatement.setString(2, user.getNombre());
                preparedStatement.setString(3, user.getDireccion());
                preparedStatement.setString(4, user.getUsername());
                preparedStatement.setString(5, user.getPassword());
                preparedStatement.setString(6, user.getEmail());
                preparedStatement.setString(7, user.getCUI());
                preparedStatement.setDate(8, user.getFechaNacimiento());
                preparedStatement.setString(9, "Solicitante");

                preparedStatement.executeUpdate();
                System.out.println("Solicitante creado");
            } catch (SQLException e) {
                System.out.println("Error al consultar: " + e);
            }
        }
    }

    public void crearUsuariOEmpleador(List<Employer> usu) {
        System.out.println("creando usuario empleador");
        String query = "INSERT INTO usuarios VALUES(?,?,?,?,?,?,?,?,NULL,NULL,?,NULL,NULL)";
        //String querytelefonos = "INSERT INTO telefonos VALUES(NULL,?,?)";
        //String querytarjeta = "INSERT INTO tarjeta VALUES(NULL,?,?,?,?,?,?)";
        for (Employer user : usu) {
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, user.getCodigo());
                preparedStatement.setString(2, user.getNombre());
                preparedStatement.setString(3, user.getDireccion());
                preparedStatement.setString(4, user.getUsername());
                preparedStatement.setString(5, user.getPassword());
                preparedStatement.setString(6, user.getEmail());
                preparedStatement.setString(7, user.getCUI());
                preparedStatement.setDate(8, user.getFechaFundacion());
                preparedStatement.setString(9, "Empleador");

                preparedStatement.executeUpdate();
                System.out.println("Empleador creado");
            } catch (SQLException e) {
                System.out.println("Error al consultar: " + e);
            }/*
            for (String telefono:user.getTelefonos()) {
                    try (var preparedStatement = conexion.prepareStatement(querytelefonos)) {

                        preparedStatement.setInt(1, user.getCodigo());
                        preparedStatement.setString(2, telefono);

                        preparedStatement.executeUpdate();
                        System.out.println("Empleador creado");
                    } catch (SQLException e) {
                        System.out.println("Error al consultar: " + e);
                    }


            }
            Tarjeta tarjeta = user.getTarjeta();
            try (var preparedStatement = conexion.prepareStatement(querytarjeta)) {

                preparedStatement.setInt(1, user.getCodigo());
                preparedStatement.setString(2, tarjeta.getTitular());
                preparedStatement.setBigDecimal(3, tarjeta.getNumero());
                preparedStatement.setInt(4, tarjeta.getCodigoSeguridad());
                preparedStatement.setDate(5, Date.valueOf("2026-02-01"));
                preparedStatement.setInt(6,1000 );
                preparedStatement.executeUpdate();
                System.out.println("Empleador creado");
            } catch (SQLException e) {
                System.out.println("Error al consultar: " + e);
            }
            System.out.println("Empleador creado");*/
        }
    }

    public void crearOferta(List<OfertasCarga> ofertasCargas) {
        System.out.println("creando ofertas");
        String query = "INSERT INTO ofertas VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

        for (OfertasCarga oferta:ofertasCargas) {
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, NULL);
                preparedStatement.setString(2, oferta.getNombre());
                preparedStatement.setString(3, oferta.getDescripcion());
                preparedStatement.setInt(4, oferta.getEmpresa());
                preparedStatement.setInt(5, oferta.getCategoria());
                preparedStatement.setString(6, oferta.getEstado());
                preparedStatement.setDate(7, (java.sql.Date) oferta.getFechaPublicacion());
                preparedStatement.setDate(8, (java.sql.Date) oferta.getFechaLimite());
                preparedStatement.setBigDecimal(9, oferta.getSalario());
                preparedStatement.setString(10, oferta.getModalidad());
                preparedStatement.setString(11, oferta.getUbicacion());
                preparedStatement.setString(12, oferta.getDetalles());
                preparedStatement.setInt(13, oferta.getUsuarioElegido());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Ofertas Creadas");
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
