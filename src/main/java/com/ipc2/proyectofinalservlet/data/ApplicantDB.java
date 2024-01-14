package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Applicant.*;
import com.ipc2.proyectofinalservlet.model.Employer.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.mysql.cj.jdbc.Blob;
import org.apache.commons.lang3.ObjectUtils;

import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
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

    public List<CategoriaUsuario> listarCategoriasusuario(int usuarion){
        String query = "SELECT * FROM categoriaUsuario WHERE codigoUsuario = ?";
        List<CategoriaUsuario> categorias = new ArrayList<>();
        CategoriaUsuario categoria = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarion);

            try (var resultset = preparedStatement.executeQuery()) {
                while (resultset.next()) {
                    var codigo = resultset.getInt("codigo");
                    var codigoUsuario = resultset.getInt("codigoUsuario");
                    var codigoCategoria = resultset.getInt("codigoCategoria");
                    categoria = new CategoriaUsuario(codigo, codigoUsuario, codigoCategoria);
                    categorias.add(categoria);
                }

            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(categorias);
        return categorias;

    }

    public List<Salario> listarSalarios(){
        String query = "SELECT DISTINCT salario FROM ofertas;";
        List<Salario> salarios = new ArrayList<>();
        Salario salario = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var salarioC = resultset.getBigDecimal("salario");
                salario = new Salario(salarioC);
                salarios.add(salario);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return salarios;
    }

    public List<Ubicacion> listarUbicaciones(){
        String query = "SELECT DISTINCT ubicacion FROM ofertas;";
        List<Ubicacion> ubicaciones = new ArrayList<>();
        Ubicacion ubicacion = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var ubicacionC = resultset.getString("ubicacion");
                ubicacion = new Ubicacion(ubicacionC);
                ubicaciones.add(ubicacion);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ubicaciones;
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

    public void guardarPdf(int usuario, InputStream inputStream){
        String query = "INSERT INTO curriculum VALUES(null,?,?) ";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,usuario);
            preparedStatement.setBlob(2,inputStream);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarPdf(int usuario, InputStream inputStream){
        String query = "UPDATE curriculum SET  pdf = ? WHERE codigoUsuario = ?";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setBlob(1,inputStream);
            preparedStatement.setInt(2,usuario);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aplicarOferta( int oferta,int usuario,String mensaje ){
        if(listarPostulacionesComprobar(usuario,oferta) == null || listarPostulacionesComprobar(usuario,oferta).isEmpty()) {
            String query = "INSERT INTO solicitudes VALUES(null,?,?,?) ";
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, oferta);
                preparedStatement.setInt(2, usuario);
                preparedStatement.setString(3, mensaje);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<OfertasEmpresa> listarOfertas(int usuarion) {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo LEFT JOIN solicitudes u ON s.codigo = u.codigoOferta AND u.usuario = ? WHERE s.fechaLimite >= CURDATE() AND u.codigoOferta IS NULL AND s.estado IN ('ENTREVISTA','ACTIVA') AND s.usuarioElegido = 0";
        List<OfertasEmpresa> ofertas = new ArrayList<>();
        OfertasEmpresa oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarion);

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
                    var modalidad = resultSet.getString("modalidad");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new OfertasEmpresa(codigo,nombre,descripcion,empresa,categoria,estado,fechaPublicacion,fechaLimite,salario,modalidad,ubicacion,detalles,usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }
        return ofertas;
    }

    public List<OfertasEmpresa> listarOfertasSugerencia(List<CategoriaUsuario> categoriaUsuarios, int codigon) {
        String query = " SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo LEFT JOIN solicitudes u ON s.codigo = u.codigoOferta AND u.usuario = ? WHERE s.fechaLimite >= CURDATE() AND u.codigoOferta IS NULL AND s.estado IN ('ENTREVISTA','ACTIVA') AND s.usuarioElegido = 0 ";
        List<OfertasEmpresa> ofertas = new ArrayList<>();
        OfertasEmpresa oferta = null;
        for (int i =0 ; i < categoriaUsuarios.size(); i++ ) {
            if(i==0){
                query += "AND ( s.categoria = ? ";
            }
            if(i==categoriaUsuarios.size()-1){
                query += "OR s.categoria = ? ) ";
            }
            if(i!=0 && i!=categoriaUsuarios.size()-1){
                query += "OR s.categoria = ? ";
            }
            System.out.println(query);
        }

        try (var preparedStatement = conexion.prepareStatement(query)) {

            int parametroIndex = 1;
            preparedStatement.setInt(parametroIndex,codigon);
            for (CategoriaUsuario catU: categoriaUsuarios) {
                System.out.println(parametroIndex);
                preparedStatement.setInt(++parametroIndex, catU.getCodigoCategoria());
                System.out.println(parametroIndex);
            }

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
                    var modalidad = resultSet.getString("modalidad");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new OfertasEmpresa(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println(ofertas);

        return ofertas;
    }

    public List<OfertasEmpresa> listarOfertasFiltros(Filtros filtros, int codigon) {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo LEFT JOIN solicitudes u ON s.codigo = u.codigoOferta AND u.usuario = ? WHERE s.nombre LIKE ? AND s.fechaLimite >= CURDATE() AND u.codigoOferta IS NULL AND s.estado IN ('ENTREVISTA','ACTIVA') AND s.usuarioElegido = 0";
        List<OfertasEmpresa> ofertas = new ArrayList<>();
        OfertasEmpresa oferta = null;
        if (filtros.getCategoria() != null && !filtros.getCategoria().isEmpty()) {
            query += " AND categoria = ?";
        }
        if (filtros.getModalidad() != null && !filtros.getModalidad() .isEmpty()) {
            query += " AND modalidad = ?";
        }
        if (filtros.getUbicacion() != null && !filtros.getUbicacion() .isEmpty()) {
            query += " AND ubicacion = ?";
        }
        if (filtros.getSalario() != null) {
            query += " AND salario = ?";
        }
        try (var preparedStatement = conexion.prepareStatement(query)) {

            int parametroIndex = 1;
            preparedStatement.setInt(parametroIndex, codigon);
            preparedStatement.setString(++parametroIndex, filtros.getNombre()+"%");
            if (filtros.getCategoria() != null && !filtros.getCategoria().isEmpty()) {
                preparedStatement.setInt(++parametroIndex, Integer.parseInt(filtros.getCategoria()));
            }
            if (filtros.getModalidad() != null && !filtros.getModalidad().isEmpty()) {
                preparedStatement.setString(++parametroIndex, filtros.getModalidad());
            }
            if (filtros.getUbicacion() != null && !filtros.getUbicacion() .isEmpty()) {
                preparedStatement.setString(++parametroIndex, filtros.getUbicacion());
            }
            if (filtros.getSalario() != null) {
                preparedStatement.setBigDecimal(++parametroIndex, filtros.getSalario());
            }


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
                    var modalidad = resultSet.getString("modalidad");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new OfertasEmpresa(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ofertas;
    }

    public OfertasEmpresa listarOfertasCodigo(int codigoN, int usuarion) {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo LEFT JOIN solicitudes u ON s.codigo = u.codigoOferta AND u.usuario = ? WHERE  s.codigo = ? AND s.fechaLimite >= CURDATE() AND u.codigoOferta IS NULL AND ( s.estado = 'ENTREVISTA' OR s.estado = 'ACTIVA') AND s.usuarioElegido = 0";
        OfertasEmpresa oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarion);
            preparedStatement.setInt(2, codigoN);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
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
                    oferta = new OfertasEmpresa(codigo,nombre,descripcion,empresa,categoria,estado,fechaPublicacion,fechaLimite,salario,modalidad,ubicacion,detalles,usuarioElegido);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return oferta;
    }

    public OfertasEmpresa listarOfertasCodigoSinEntrevista(int codigoN, int usuarion) {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo LEFT JOIN solicitudes u ON s.codigo = u.codigoOferta AND u.usuario = ? WHERE  s.codigo = ? AND s.fechaLimite >= CURDATE() AND s.usuarioElegido = 0 ";
        OfertasEmpresa oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarion);
            preparedStatement.setInt(2, codigoN);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
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
                    oferta = new OfertasEmpresa(codigo,nombre,descripcion,empresa,categoria,estado,fechaPublicacion,fechaLimite,salario,modalidad,ubicacion,detalles,usuarioElegido);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return oferta;
    }


    public List<EstadoSolicitud> listarPostulaciones(int usuarioN) {
        String query = " SELECT s.codigo, s.codigoOferta, s.usuario, o.estado, o.nombre, u.nombre AS 'empresa'FROM solicitudes s JOIN ofertas o ON s.codigoOferta = o.codigo JOIN usuarios u ON o.empresa = u.codigo WHERE s.usuario = ? AND fechaLimite >= CURDATE() AND NOT EXISTS (SELECT * FROM entrevistas e WHERE e.codigoOferta = s.codigoOferta AND e.usuario = s.usuario);";
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

    public List<EstadoSolicitud> listarPostulacionesComprobar(int usuarioN, int ofertan) {
        String query = " SELECT s.codigo, s.codigoOferta, s.usuario, o.estado, o.nombre, u.nombre AS 'empresa'FROM solicitudes s JOIN ofertas o ON s.codigoOferta = o.codigo JOIN usuarios u ON o.empresa = u.codigo WHERE s.usuario = ? AND s.codigoOferta = ? AND fechaLimite >= CURDATE() ";
        List<EstadoSolicitud> solicitudes = new ArrayList<>();
        EstadoSolicitud solicitud = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, usuarioN);
            preparedStatement.setInt(2, ofertan);


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

    public void registroPostulaciones(int usuarioN,String oferta, String fecha) {
        String query = "INSERT INTO postulacionRetirada VALUES(?,?,?,?) ";

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, Types.NULL);
            preparedStatement.setInt(2, usuarioN);
            preparedStatement.setString(3, oferta);
            preparedStatement.setDate(4, Date.valueOf(fecha));
            preparedStatement.executeUpdate();

        }catch (SQLException e) {
            System.out.println("Error al registrar postulaciones retiradas: " + e);
        }
    }

    public List<RegistroPostulacion> listarRegistros(int usuarioN, String fechaA, String fechaB) {
        String query = " SELECT * FROM postulacionRetirada WHERE usuario=? AND fecha BETWEEN ? AND ?";
        if (fechaA.isEmpty() || fechaB.isEmpty()){
            query = " SELECT * FROM postulacionRetirada WHERE usuario=? ";
        }
        List<RegistroPostulacion> registroPostulacions = new ArrayList<>();
        RegistroPostulacion registroPostulacion = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, usuarioN);
            if (!fechaA.isEmpty() && !fechaB.isEmpty()) {

                preparedStatement.setString(2, fechaA);
                preparedStatement.setString(3, fechaB);
            }


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var usuario = resultSet.getInt("usuario");
                    var oferta = resultSet.getString("oferta");
                    var fecha = resultSet.getString("fecha");
                    registroPostulacion = new RegistroPostulacion(codigo,usuario,oferta,fecha);
                    registroPostulacions.add(registroPostulacion);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al registrar postulaciones retiradas: " + e);
        }

        return registroPostulacions;
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
        String query = "SELECT e.codigo,o.nombre AS 'Oferta' ,u.nombre AS 'Empresa',e.estado,c.cantidad FROM comision c ,entrevistas e INNER JOIN ofertas o ON o.codigo =  e.codigoOferta INNER JOIN usuarios u ON u.codigo = o.empresa WHERE e.usuario = ? AND o.fechaLimite >= CURDATE() AND o.estado IN ('ENTREVISTA','ACTIVA') AND o.usuarioElegido = 0";
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

    public List<OfertasEmpresa> listarOfertasNombre(String nombreE, int usuarion)  {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo LEFT JOIN solicitudes u ON s.codigo = u.codigoOferta AND u.usuario = ? WHERE s.nombre LIKE ? AND s.fechaLimite >= CURDATE() AND s.estado IN ('ENTREVISTA','ACTIVA') AND s.usuarioElegido = 0";
        List<OfertasEmpresa> ofertas = new ArrayList<>();
        OfertasEmpresa oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1,usuarion);
            preparedStatement.setString(2,nombreE+"%");

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
                    var modalidad = resultSet.getString("modalidad");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new OfertasEmpresa(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                }
            }

        }catch (SQLException e) {
            System.out.println("Error al listar Ofertas Nombre P1: " + e);
        }
        return ofertas;
    }

    public List<EntrevistaOferta> listarEntrevistasInfo(int usuarion) {
        String query = "SELECT e.*, u.nombre, o.nombre AS 'nombreOferta' FROM entrevistas e INNER JOIN usuarios u ON e.usuario = u.codigo INNER JOIN ofertas o ON o.codigo = e.codigoOferta WHERE e.estado = 'PENDIENTE' AND u.codigo = ? AND o.fechaLimite >= CURDATE()";
        List<EntrevistaOferta> entrevistas = new ArrayList<>();
        EntrevistaOferta entrevista = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1,usuarion);

            try (var resultset = preparedStatement.executeQuery()) {
                while (resultset.next()) {
                    var codigo = resultset.getInt("codigo");
                    var usuario = resultset.getInt("usuario");
                    var fecha = resultset.getDate("fecha");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaFormateada = dateFormat.format(fecha);
                    var hora = resultset.getTime("hora");
                    var ubicacion = resultset.getString("ubicacion");
                    var estado = resultset.getString("estado");
                    var notas = resultset.getString("notas");
                    var codigoOferta = resultset.getInt("codigoOferta");
                    var nombreOferta = resultset.getString("nombreOferta");
                    entrevista = new EntrevistaOferta(codigo, usuario, fechaFormateada, hora, ubicacion, estado, notas, codigoOferta, "", nombreOferta);
                    entrevistas.add(entrevista);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar Ofertas Costos: " + e);
        }

        return entrevistas;
    }

    public List<OfertasEmpresaFecha> listarOfertasFecha(int usuario,String estadon, String fechaA, String fechaB) {
        String query = "SELECT o.* FROM ofertas o INNER JOIN solicitudes s ON s.codigoOferta = o.codigo WHERE o.estado=? AND s.usuario=? AND fechaPublicacion BETWEEN ? AND ? AND o.usuarioElegido = 0 ";
        if (fechaA.isEmpty() || fechaB.isEmpty()){
            query = "SELECT o.* FROM ofertas o INNER JOIN solicitudes s ON s.codigoOferta = o.codigo WHERE o.estado=? AND s.usuario=?  AND o.usuarioElegido = 0 ";
        }
        List<OfertasEmpresaFecha> ofertas = new ArrayList<>();
        OfertasEmpresaFecha oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, estadon);
            preparedStatement.setInt(2, usuario);
            if (!fechaA.isEmpty() && !fechaB.isEmpty()){
                preparedStatement.setDate(3, Date.valueOf(fechaA));
                preparedStatement.setDate(4, Date.valueOf(fechaB));
            }



            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var descripcion = resultSet.getString("descripcion");
                    var empresa = resultSet.getString("empresa");
                    var categoria = resultSet.getInt("categoria");
                    var estado = resultSet.getString("estado");
                    var fechaPublicacion = resultSet.getDate("fechaPublicacion");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaFormateada = dateFormat.format(fechaPublicacion);
                    var fechaLimite = resultSet.getDate("fechaLimite");
                    var salario = resultSet.getBigDecimal("salario");
                    var modalidad = resultSet.getString("salario");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new OfertasEmpresaFecha(codigo,nombre,descripcion,empresa,categoria,estado,fechaFormateada,fechaLimite,salario,modalidad,ubicacion,detalles,usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return ofertas;
    }

    public UsuarioPdf visualizarPdf(int usuario){
        String query = "SELECT * FROM curriculum WHERE codigoUsuario=?";
        UsuarioPdf pdfn = null;
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,usuario);
            try (var resultset = preparedStatement.executeQuery()) {
                if (resultset.next()) {
                    var codigo = resultset.getInt("codigo");
                    var codigoUsuario = resultset.getInt("codigoUsuario");
                    var pdf = resultset.getBlob("pdf");
                    pdfn = new UsuarioPdf(codigo, codigoUsuario, (Blob) pdf);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  pdfn;
    }
}
