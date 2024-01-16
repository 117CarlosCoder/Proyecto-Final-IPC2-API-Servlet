package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.controller.UserController.Encriptador;
import com.ipc2.proyectofinalservlet.model.Admin.Admin;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdfJson;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Entrevista;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Solicitudes;
import com.ipc2.proyectofinalservlet.model.Employer.Employer;
import com.ipc2.proyectofinalservlet.model.Employer.OfertasCarga;
import com.ipc2.proyectofinalservlet.model.Employer.Tarjeta;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class CargaDB {
    private final Connection conexion;

    private Encriptador encriptador;

    public CargaDB(Connection conexion){  this.conexion = conexion;}

    public boolean validandoBase(){
        String query = "SELECT * FROM usuarios ";
        User user = null;
        List<User> users = new ArrayList<>();
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var nombre = resultset.getString("nombre");
                var direccion = "";
                var username = resultset.getString("username");
                var password = "";
                var email ="";
                var cui = "";
                var curriculum = "";
                var rol = "";
                var fechaNacimiento = resultset.getDate("fechaNacimiento");
                var fechaFundacion = resultset.getDate("fechaFundacion");
                var mision = "";
                var vision ="";
                user = new User(codigo, nombre,direccion,username,password,"",email,cui,fechaFundacion,fechaNacimiento,curriculum,rol,mision,vision,false);
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return !users.isEmpty();
    }

    public void reiniciarBase(){
        DatabaseMetaData metaData = null;
        try {
            metaData = conexion.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);

            while (tables.next()) {
                String nombreDeTabla = tables.getString("TABLE_NAME");
                vaciarTabla(conexion, nombreDeTabla);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void vaciarTabla(Connection conexion, String nombreDeTabla) throws SQLException {
        String consulta = "DELETE FROM " + nombreDeTabla;

        try (Statement statement = conexion.createStatement()) {
            statement.executeUpdate(consulta);
        }
    }

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

    public void crearTarjeta(Tarjeta tarjeta, int usuario){
        String query = "INSERT INTO tarjeta VALUES(null,?,?,?,?,?,?) ";

                try(var preparedStatement = conexion.prepareStatement(query)) {
                    preparedStatement.setInt(1,usuario);
                    preparedStatement.setString(2, tarjeta.getTitular());
                    preparedStatement.setBigDecimal(3, tarjeta.getNumero());
                    preparedStatement.setInt(4,tarjeta.getCodigoSeguridad());
                    preparedStatement.setDate(5,Date.valueOf("2024-08-12"));
                    preparedStatement.setBigDecimal(6,BigDecimal.valueOf(1600));

                    preparedStatement.executeUpdate();
                    System.out.println("Tarjeta empleador creada");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
    }

    public void crearUsuarioSuspension(String username){
        String query = "INSERT INTO suspension VALUES(null,?,false) ";

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1,username);
            preparedStatement.executeUpdate();
            System.out.println("Apartado suspension");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearCategoriaUsuario(int[] categorias, int usuario){
        String query = "INSERT INTO categoriaUsuario VALUES(null,?,?) ";
        for (int categoria : categorias) {
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, usuario);
                preparedStatement.setInt(2, categoria);

                preparedStatement.executeUpdate();
                System.out.println("Usuario Categoria creada");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void crearSolicitudes(List<Solicitudes> solicitudes, int oferta){
        String query = "INSERT INTO solicitudes VALUES(null,?,?,?) ";
        for (Solicitudes solicitud : solicitudes) {
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, oferta);
                preparedStatement.setInt(2, solicitud.getUsuario());
                preparedStatement.setString(3, solicitud.getMensaje());

                preparedStatement.executeUpdate();
                System.out.println("Solicitud creada");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void crearEntrevistas(List<Entrevista> entrevistas, int oferta){
        String query = "INSERT INTO entrevistas VALUES(null,?,?,?,?,?,?,?) ";
        for (Entrevista entrevista : entrevistas) {
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, oferta);
                preparedStatement.setInt(2, entrevista.getUsuario());
                preparedStatement.setDate(3, entrevista.getFecha());
                preparedStatement.setString(4, entrevista.getHora());
                preparedStatement.setString(5, entrevista.getUbicacion());
                preparedStatement.setString(6, entrevista.getEstado());
                preparedStatement.setString(7, entrevista.getNotas());

                preparedStatement.executeUpdate();
                System.out.println("Solicitud creada");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void crearTelefonos(String[] telefonos, int usuario){
        String query = "INSERT INTO telefonos VALUES(null,?,?) ";

        for (String telefono : telefonos) {
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, usuario);
                preparedStatement.setInt(2, Integer.parseInt(telefono));

                preparedStatement.executeUpdate();
                System.out.println("Crendo Telefonos ");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void crearAdmin(Admin admin) {
        System.out.println("creando usuario administrador");
        String query = "INSERT INTO usuarios VALUES(?,?,?,?,?,?,?,?,NULL,?,NULL,'Administrador',NULL,NULL,false)";
        encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();


            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, admin.getCodigo());
                preparedStatement.setString(2, admin.getNombre());
                preparedStatement.setString(3, admin.getDireccion());
                preparedStatement.setString(4, admin.getUsername());
                preparedStatement.setString(5,encriptador.encriptarContrasena(admin.getPassword(), sal));
                preparedStatement.setString(6,sal);
                preparedStatement.setString(7, admin.getEmail());
                preparedStatement.setString(8,admin.getCUI());
                preparedStatement.setDate(9,admin.getFechaNacimiento());
                preparedStatement.executeUpdate();
                crearUsuarioSuspension(admin.getUsername());
                System.out.println("Admin creado");
            } catch (SQLException e) {
                System.out.println("Error al crear Admin: " + e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

    }

    public void cargarUsuarioSolicitante(List<Usuarios> usu) {
        System.out.println("creando usuario solicitante");
        String query = "INSERT INTO usuarios VALUES(?,?,?,?,?,?,?,?,NULL,?,?,?,NULL,NULL,false)";
        encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();
        for (Usuarios user : usu) {

            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, user.getCodigo());
                preparedStatement.setString(2, user.getNombre());
                preparedStatement.setString(3, user.getDireccion());
                preparedStatement.setString(4, user.getUsername());
                preparedStatement.setString(5,encriptador.encriptarContrasena(user.getPassword(), sal));
                preparedStatement.setString(6, sal);
                preparedStatement.setString(7, user.getEmail());
                preparedStatement.setString(8, user.getCUI());
                preparedStatement.setDate(9, user.getFechaNacimiento());
                preparedStatement.setString(10, user.getCurriculum());
                preparedStatement.setString(11, "Solicitante");

                preparedStatement.executeUpdate();
                System.out.println("Solicitante creado");
                crearUsuarioSuspension(user.getUsername());
                crearTelefonos(user.getTelefonos(), user.getCodigo());
                crearCategoriaUsuario(user.getCategorias(), user.getCodigo());
            } catch (SQLException e) {
                System.out.println("Error al consultar: " + e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void cargarUsuariOEmpleador(List<Employer> usu) {
        System.out.println("creando usuario empleador");

        String query = "INSERT INTO usuarios VALUES(?,?,?,?,?,?,?,?,?,NULL,NULL,?,?,?,false)";
        encriptador = new Encriptador();
        String sal = encriptador.generarSecuencia();

        for (Employer user : usu) {

            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, user.getCodigo());
                preparedStatement.setString(2, user.getNombre());
                preparedStatement.setString(3, user.getDireccion());
                preparedStatement.setString(4, user.getUsername());
                preparedStatement.setString(5,encriptador.encriptarContrasena( user.getPassword(), sal));
                preparedStatement.setString(6, sal);
                preparedStatement.setString(7, user.getEmail());
                preparedStatement.setString(8, user.getCUI());
                preparedStatement.setDate(9, user.getFechaFundacion());
                preparedStatement.setString(10, "Empleador");
                preparedStatement.setString(11, user.getMision());
                preparedStatement.setString(12, user.getVision());

                preparedStatement.executeUpdate();
                System.out.println("Empleador creado");
                crearUsuarioSuspension(user.getUsername());
                crearTarjeta(user.getTarjeta(), user.getCodigo());
                crearTelefonos(user.getTelefonos(), user.getCodigo());

            } catch (SQLException e) {
                System.out.println("Error al consultar: " + e);
            } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
            }
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
                preparedStatement.setDate(7,  oferta.getFechaPublicacion());
                preparedStatement.setDate(8, oferta.getFechaLimite());
                preparedStatement.setBigDecimal(9, oferta.getSalario());
                preparedStatement.setString(10, oferta.getModalidad());
                preparedStatement.setString(11, oferta.getUbicacion());
                preparedStatement.setString(12, oferta.getDetalles());
                preparedStatement.setInt(13, oferta.getUsuarioElegido());
                preparedStatement.executeUpdate();
                crearSolicitudes(oferta.getSolicitudes(), oferta.getCodigo());
                crearEntrevistas(oferta.getEntrevistas(), oferta.getCodigo());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Ofertas Creadas");
        }

    }

    public Comision crearComision(BigDecimal cantidadE){
        String query = "INSERT INTO comision VALUES(1,?,NOW())";
        Comision comision = null;
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setBigDecimal(1,cantidadE);
            comision = new Comision(1,cantidadE);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comision;
    }

    public Comision cambiarComision(BigDecimal cantidadE){
        String query = "UPDATE comision SET cantidad = ? WHERE codigo = 1";
        Comision comision = null;
        System.out.println(cantidadE);
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setBigDecimal(1,cantidadE);
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
                var cantidad = resultset.getBigDecimal("cantidad");
                comision = new Comision(codigo,cantidad);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comision;
    }

    public List<User> listarUsuariosPdf(){
        String query = "SELECT u.codigo, u.nombre, u.username, u.curriculum, u.fechaNacimiento, u.fechaFundacion FROM usuarios u WHERE u.rol = 'Solicitante' AND NOT EXISTS ( SELECT 1 FROM curriculum c WHERE c.codigoUsuario = u.codigo)";
        User user = null;
        List<User> users = new ArrayList<>();
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var nombre = resultset.getString("nombre");
                var direccion = "";
                var username = resultset.getString("username");
                var password = "";
                var email ="";
                var cui = "";
                var curriculum = resultset.getString("curriculum");
                var rol = "";
                var fechaNacimiento = resultset.getDate("fechaNacimiento");
                var fechaFundacion = resultset.getDate("fechaFundacion");
                var mision = "";
                var vision ="";
                user = new User(codigo, nombre,direccion,username,password,"",email,cui,fechaFundacion,fechaNacimiento,curriculum,rol,mision,vision,false);
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  users;
    }

    public void guardarPdf(List<UsuarioPdfJson> usuarioPdf){
        String query = "INSERT INTO curriculum VALUES(null,?,?) ";
        System.out.println(usuarioPdf);
        for (UsuarioPdfJson usuario : usuarioPdf){
            try(var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1,usuario.getCodigoUsuario());
                        preparedStatement.setBlob(2, usuario.getPdfBlob().getBinaryStream());
                        preparedStatement.executeUpdate();
                        System.out.println("Pdf guardado");


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }



}
