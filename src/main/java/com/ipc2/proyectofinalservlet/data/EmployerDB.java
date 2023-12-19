package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Employer.EntrevistaFecha;
import com.ipc2.proyectofinalservlet.model.Employer.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

import static java.sql.Types.NULL;

public class EmployerDB {
    private final Connection conexion;

    public EmployerDB(Connection conexion) {
        this.conexion = conexion;
    }


    public void completarInformacion(String mision, String vision, int codigo) {
        String query = "UPDATE usuarios SET mision = ?, vision = ? WHERE codigo = ?";
        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, mision);
            preparedStatement.setString(2, vision);
            preparedStatement.setInt(3, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void completarInformacionTarjeta(int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad, java.sql.Date fechaExpiracion, BigDecimal cantidad){
        String query = "INSERT INTO tarjeta VALUES(?,?,?,?,?,?,?)";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, Types.NULL  );
            preparedStatement.setInt(2,codigoUsuario);
            preparedStatement.setString(3,Titular);
            preparedStatement.setInt(4,numero);
            preparedStatement.setInt(5,codigoSeguridad);
            preparedStatement.setDate(6,fechaExpiracion);
            preparedStatement.setBigDecimal(7,cantidad);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido, int codigo) {
        String query = "UPDATE ofertas SET nombre = ?, descripcion = ? , categoria = ?, fechaLimite = ?, salario = ?, modalidad = ?, ubicacion = ?, detalles = ? WHERE codigo = ?";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setInt(3, categoria);
            preparedStatement.setDate(4, (java.sql.Date) fechaLimite);
            preparedStatement.setBigDecimal(5, salario);
            preparedStatement.setString(6, modalidad);
            preparedStatement.setString(7, ubicacion);
            preparedStatement.setString(8, detalles);
            preparedStatement.setInt(9, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido) {
        String query = "INSERT INTO ofertas VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, NULL);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, descripcion);
            preparedStatement.setInt(4, empresa);
            preparedStatement.setInt(5, categoria);
            preparedStatement.setString(6, estado);
            preparedStatement.setDate(7, (java.sql.Date) fechaPublicacion);
            preparedStatement.setDate(8, (java.sql.Date) fechaLimite);
            preparedStatement.setBigDecimal(9, salario);
            preparedStatement.setString(10, modalidad);
            preparedStatement.setString(11, ubicacion);
            preparedStatement.setString(12, detalles);
            preparedStatement.setInt(13, usuarioElegido);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                    var modalidad = resultSet.getString("modalidad");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new Ofertas(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }
        return oferta;
    }

    public void actualizarOfertaEstado(int codigo) {
        String query = "UPDATE ofertas SET estado = 'ENTREVISTA' WHERE codigo = ?";

        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, codigo);
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

    public List<Categoria> listarCategorias() {
        String query = "SELECT * FROM categoria";
        List<Categoria> categorias = new ArrayList<>();
        Categoria categoria = null;
        try (var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var nombre = resultset.getString("nombre");
                var descripcion = resultset.getString("descripcion");
                categoria = new Categoria(codigo, nombre, descripcion);
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categorias;
    }

    public List<Ofertas> listarOfertasEmpresa(int numEmpresa) {
        String query = "SELECT * FROM ofertas WHERE empresa = ? ";
        List<Ofertas> ofertas = new ArrayList<>();
        Ofertas oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, numEmpresa);


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
                    oferta = new Ofertas(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return ofertas;
    }

    public List<Ofertas> listarOfertasEmpresaPostulaciones(int numEmpresa) {
        String query = "SELECT * FROM ofertas WHERE empresa = ? AND estado = 'ACTIVA' OR empresa = ? AND estado = 'SELECCION' OR empresa = ? AND estado = 'ENTREVISTA'";
        List<Ofertas> ofertas = new ArrayList<>();
        Ofertas oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, numEmpresa);
            preparedStatement.setInt(2, numEmpresa);
            preparedStatement.setInt(3, numEmpresa);


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
                    oferta = new Ofertas(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return ofertas;
    }

    public List<Ofertas> listarOfertasEmpresaPostulantes(int numEmpresa) {
        String query = "SELECT * FROM ofertas WHERE estado = 'SELECCION' AND  empresa = ?";
        List<Ofertas> ofertas = new ArrayList<>();
        Ofertas oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, numEmpresa);

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
                    oferta = new Ofertas(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ofertasPostulantes: " + e);
        }

        return ofertas;
    }

    public Entrevista listarEntrevistaPostulante(int usuarion, int codigoOfertan) {
        String query = "SELECT * FROM solicitudes WHERE usuario = ? AND codigoOferta = ?";
        Entrevista entrevista = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarion);
            preparedStatement.setInt(2, codigoOfertan);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var usuario = resultSet.getInt("usuario");
                    var fecha = resultSet.getDate("fecha");
                    var hora = resultSet.getString("hora");
                    var ubicacion = resultSet.getString("ubicacion");
                    var estado = resultSet.getString("estado");
                    var notas = resultSet.getString("notas");
                    entrevista = new Entrevista(codigo,usuario,fecha,hora,ubicacion,estado,notas,codigoOferta);

                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ofertasPostulantes: " + e);
        }

        return entrevista;
    }

    public void eliminarOfertas(int codigo) {
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM ofertas WHERE codigo = ?")) {
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

    public List<Solicitudes> listarPostulantes(int numOferta) {
        String query = "SELECT * FROM solicitudes WHERE codigoOferta = ?";
        List<Solicitudes> solicitudes = new ArrayList<>();
        Solicitudes solicitud = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, numOferta);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var usuario = resultSet.getInt("usuario");
                    var mensaje = resultSet.getString("mensaje");
                    solicitud = new Solicitudes(codigo, codigoOferta, usuario, mensaje);
                    solicitudes.add(solicitud);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar solicitudes: " + e);
        }

        return solicitudes;
    }

    public List<EstadoSolicitudPostulante> listarPostulaciones(int codigoN) {
        String query = " SELECT s.codigo, s.codigoOferta, s.usuario, o.estado, o.nombre, u.nombre AS 'nombreUsuario' FROM solicitudes s  JOIN ofertas o ON s.codigoOferta = o.codigo  JOIN usuarios u ON s.usuario = u.codigo WHERE s.codigoOferta = ? AND NOT EXISTS (SELECT 1 FROM entrevistas e WHERE e.codigoOferta = s.codigoOferta AND e.usuario = s.usuario )";
        List<EstadoSolicitudPostulante> solicitudes = new ArrayList<>();
        EstadoSolicitudPostulante solicitud = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, codigoN);


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var usuario = resultSet.getInt("usuario");
                    var estado = resultSet.getString("estado");
                    var nombre = resultSet.getString("nombre");
                    var nombreUsuario = resultSet.getString("nombreUsuario");
                    solicitud = new EstadoSolicitudPostulante(codigo, codigoOferta, usuario, estado, nombre, nombreUsuario);
                    solicitudes.add(solicitud);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar especialidades: " + e);
        }

        return solicitudes;
    }

    public Postulante obtenerPostulante(int usuario, int ofertan) {
        System.out.println("Obteniendo usuario");
        String query = "SELECT usuarios.*, solicitudes.mensaje, solicitudes.codigoOferta FROM solicitudes INNER JOIN usuarios ON solicitudes.usuario = usuarios.codigo WHERE solicitudes.usuario = ? AND solicitudes.codigoOferta = ?";
        Postulante postulante = null;

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuario);
            preparedStatement.setInt(2, ofertan);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoOferta = resultSet.getInt("codigoOferta");
                    var nombre = resultSet.getString("nombre");
                    var email = resultSet.getString("email");
                    var curriculum = resultSet.getString("curriculum");
                    var mensaje = resultSet.getString("mensaje");
                    postulante = new Postulante(codigo, codigoOferta, nombre, email, curriculum, mensaje);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }

        return postulante;
    }

    public void generarEntrevista(int codigo, int codigoOferta, int usuario, Date fecha, String hora, String ubicacion) {
        String query = "INSERT INTO entrevistas VALUES(?,?,?,?,?,?,?,?)";
        java.sql.Date fech = new java.sql.Date(NULL);
        Time tiempo = new Time(LocalDateTime.now().getHour());

        /*SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(hora));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Time time = new Time(date.getTime());*/

        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatoEntrada.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date fechah = null;
        String horaFormateada = "";
        try {
            // Analiza la cadena a un objeto Date
            fechah = formatoEntrada.parse(hora);
            // Establece la zona horaria del objeto Date a la zona horaria de Quetzaltenango
            //fechah.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
            horaFormateada = formatoEntrada.format(fechah);
            System.out.println("Hora formateada: " + horaFormateada);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Time time = new Time(fechah.getTime());

        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, NULL);
            preparedStatement.setInt(2, codigoOferta);
            preparedStatement.setInt(3, usuario);
            preparedStatement.setDate(4, (java.sql.Date) fecha);
            preparedStatement.setTime(5, time);
            preparedStatement.setString(6, ubicacion);
            preparedStatement.setString(7, "PENDIENTE");
            preparedStatement.setString(8, "");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EntrevistaInfo> listarEntrevista(int empresan)  {
        String query = "SELECT e.*, u.nombre, o.nombre AS 'nombreOferta' FROM entrevistas e INNER JOIN usuarios u ON e.usuario = u.codigo INNER JOIN ofertas o ON o.codigo = e.codigoOferta WHERE o.empresa = ? AND e.fecha  >= CURDATE()";
        List<EntrevistaInfo> entrevistas = new ArrayList<>();
        EntrevistaInfo entrevista = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, empresan);

            try (var resultset = preparedStatement.executeQuery()) {
                while (resultset.next()) {
                    var codigo = resultset.getInt("codigo");
                    var usuario = resultset.getInt("usuario");
                    var fecha = resultset.getDate("fecha");
                    var hora = resultset.getTime("hora");
                    var ubicacion = resultset.getString("ubicacion");
                    var estado = resultset.getString("estado");
                    var notas = resultset.getString("notas");
                    var codigoOferta = resultset.getInt("codigoOferta");
                    var nombre = resultset.getString("nombre");
                    var nombreOferta = resultset.getString("nombreOferta");
                    entrevista = new EntrevistaInfo(codigo, usuario, fecha, hora, ubicacion, estado, notas, codigoOferta, nombre, nombreOferta);
                    entrevistas.add(entrevista);
                }
        }

    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entrevistas;
    }

    public void finalizarEntrevista(String notas, int usuario, int codigo) {
        String query = "UPDATE entrevistas SET notas = ?, estado = ? WHERE usuario = ? AND codigo = ?";

        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, notas);
            preparedStatement.setString(2, "FINALIZADO");
            preparedStatement.setInt(3, usuario);
            preparedStatement.setInt(4, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void contratar( int usuario, int codigo) {
        String query = "UPDATE ofertas SET usuarioElegido = ? WHERE codigo = ? ";

        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, usuario);
            preparedStatement.setInt(2, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (var preparedStatement = conexion.prepareStatement("UPDATE tarjeta SET cantidad = ? WHERE codigoUsuario = ?")) {
            preparedStatement.setBigDecimal(1, listarCantidadTarjeta(usuarioEmpresa(codigo)).subtract(comision()));
            preparedStatement.setInt(2, usuarioEmpresa(codigo));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }

        crearCobroComision(codigo,usuario,usuarioEmpresa(codigo),comision(),listarCantidadTarjeta(usuarioEmpresa(codigo)),listarCantidadTarjeta(usuarioEmpresa(codigo)).subtract(comision()));
    }

    public void crearCobroComision(int oferta,int usuario, int empresa, BigDecimal cobro, BigDecimal cantidadTarjeta, BigDecimal Total){
        String query = "INSERT INTO cobroComision VALUES(NULL,?,?,?,?,?,?,NOW())";

        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, oferta);
            preparedStatement.setInt(2, usuario);
            preparedStatement.setInt(3, empresa);
            preparedStatement.setBigDecimal(4, cobro);
            preparedStatement.setBigDecimal(5, cantidadTarjeta);
            preparedStatement.setBigDecimal(6, Total);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int usuarioEmpresa(int ofertan) {
        String query = "SELECT * FROM ofertas WHERE codigo = ?";
        int empresa = 0;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, ofertan);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    empresa = resultSet.getInt("empresa");
                }    }
        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e);
        }

        return empresa;
    }

    public BigDecimal listarCantidadTarjeta(int usuarion) {
        String query = "SELECT * FROM tarjeta WHERE codigoUsuario = ?";
        BigDecimal cantidad = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarion);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    cantidad = resultSet.getBigDecimal("cantidad");
                }    }
        } catch (SQLException e) {
            System.out.println("Error al listar cantidad: " + e);
        }

        return cantidad;
    }

    public BigDecimal comision(){
        String query = "SELECT * FROM comision";
        BigDecimal cantidad  = null;
        try (var select = conexion.prepareStatement(query)) {
            ResultSet resultSet = select.executeQuery();
            while (resultSet.next()) {
                cantidad = resultSet.getBigDecimal("cantidad");
            }
        }  catch (SQLException e) {
            System.out.println("Error al listar comision: " + e);
        }

        return cantidad;
    }

    public List<OfertaCostos> listarOfertasCostos() {
        String query = "SELECT o.codigo,o.nombre AS 'Oferta' ,u.nombre AS 'Empresa',o.estado,c.cantidad FROM cobros c INNER JOIN ofertas o ON o.codigo = c.codigoOferta INNER JOIN usuarios u ON u.codigo = o.empresa WHERE o.estado = 'FINALIZADO'";
        List<OfertaCostos> ofertasCostos = new ArrayList<>();
        OfertaCostos ofertaCostos = null;


        try (var select = conexion.prepareStatement(query)) {
            ResultSet resultSet = select.executeQuery();
            while (resultSet.next()) {
                var codigo = resultSet.getInt("codigo");
                var oferta = resultSet.getString("Oferta");
                var empresa = resultSet.getString("Empresa");
                var estado = resultSet.getString("estado");
                var cantidad = resultSet.getInt("cantidad");
                ofertaCostos = new OfertaCostos(codigo, oferta, empresa, estado, cantidad);
                ofertasCostos.add(ofertaCostos);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ofertasCostos;
    }

    public List<EntrevistaFecha> listarEntrevistaFecha(java.sql.Date fechan, int empresaN, String estadon) {
        String query = "SELECT e.codigo,o.nombre AS 'Oferta', e.fecha,u.nombre AS 'Empresa',e.estado FROM entrevistas e INNER JOIN ofertas o ON o.codigo =  e.codigoOferta INNER JOIN usuarios u ON u.codigo = o.empresa WHERE o.empresa=? AND e.fecha = ? AND e.estado = ?";
        List<EntrevistaFecha> entrevistaFechas = new ArrayList<>();
        EntrevistaFecha entrevistaFecha = null;


        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, empresaN);
            preparedStatement.setDate(2, fechan);
            preparedStatement.setString(3, estadon);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormateada = dateFormat.format(fechan);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var oferta = resultSet.getString("Oferta");
                    var empresa = resultSet.getString("Empresa");
                    var estado = resultSet.getString("estado");
                    entrevistaFecha = new EntrevistaFecha(codigo, oferta, fechaFormateada, empresa, estado);
                    entrevistaFechas.add(entrevistaFecha);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar EntrevistaFechas: " + e);
        }

        return entrevistaFechas;
    }

    public List<OfertasEmpresaFecha> listarOfertaFecha(String fechaAn, String fechaBn, String estadon, int empresan) {
        String query = " SELECT e.codigo, e.nombre, e.descripcion,u.nombre AS 'empresa', e.categoria, e.estado, e.fechaPublicacion, e.fechaLimite, e.salario, e.modalidad, e.ubicacion, e.detalles,e.usuarioElegido FROM ofertas e INNER JOIN usuarios u ON e.empresa = u.codigo  WHERE e.empresa=? AND e.estado=? AND e.fechaPublicacion BETWEEN ? AND ?";
        List<OfertasEmpresaFecha> ofertas = new ArrayList<>();
        OfertasEmpresaFecha oferta = null;
        System.out.println(fechaAn);
        System.out.println(fechaBn);
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, empresan);
            preparedStatement.setString(2,estadon);
            preparedStatement.setDate(3, java.sql.Date.valueOf(fechaAn));
            preparedStatement.setDate(4, java.sql.Date.valueOf(fechaBn));



            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var descripcion = resultSet.getString("descripcion");
                    var empresa = resultSet.getString("empresa");
                    var categoria = resultSet.getInt("categoria");
                    var estado = resultSet.getString("estado");
                    var fechaPublicacion = resultSet.getDate("fechaPublicacion");
                    var fechaLimite = resultSet.getDate("fechaLimite");
                    var salario = resultSet.getBigDecimal("salario");
                    var modalidad = resultSet.getString("salario");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new OfertasEmpresaFecha(codigo, nombre, descripcion, empresa, categoria, estado, fechaAn, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                    System.out.println(oferta);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar EntrevistaFechas: " + e);
        }

            return ofertas;
        }

}
