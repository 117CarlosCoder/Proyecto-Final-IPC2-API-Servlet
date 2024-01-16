package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.model.Applicant.Filtros;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.OfertasEmpresa;
import com.ipc2.proyectofinalservlet.model.Invitado.Invitado;
import com.ipc2.proyectofinalservlet.model.Invitado.OfertaEmpresaInvitado;
import com.ipc2.proyectofinalservlet.model.Invitado.OfertaInformacion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvitadoDB {

    private final Connection conexion;

    public InvitadoDB(Connection conexion) {
        this.conexion = conexion;
    }

    public List<OfertasEmpresa> listarOfertas() {
        String query = " SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo WHERE s.fechaLimite >= CURDATE() AND s.estado = 'ACTIVA' AND s.usuarioElegido = 0";
        List<OfertasEmpresa> ofertas = new ArrayList<>();
        OfertasEmpresa oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

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

    public List<OfertasEmpresa> listarOfertasEmpresa(int empresan) {
        String query = " SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo WHERE s.fechaLimite > CURDATE() AND s.estado = 'ACTIVA' AND s.usuarioElegido = 0 AND s.empresa = ? AND o.rol='Empleador'";
        List<OfertasEmpresa> ofertas = new ArrayList<>();
        OfertasEmpresa oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, empresan);
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

    public OfertaInformacion listarEmpresa(int empresan) {
        String query = " SELECT o.nombre, o.mision, o.vision FROM usuarios o WHERE o.codigo = ? AND o.rol='Empleador' ";

        OfertaInformacion oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, empresan);
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var nombre = resultSet.getString("nombre");
                    var mision = resultSet.getString("mision");
                    var vision = resultSet.getString("vision");
                    oferta = new OfertaInformacion(nombre,mision,vision);

                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }
        return oferta;
    }

    public List<OfertasEmpresa> listarOfertasFiltros(Filtros filtros) {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo WHERE s.nombre LIKE ? AND s.fechaLimite >= CURDATE() AND s.estado = 'ACTIVA' AND s.usuarioElegido = 0";
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

            int parametroIndex = 0;
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

    public OfertaEmpresaInvitado listarOfertasCodigoSinEntrevista(int codigoN) {
        String query = "SELECT s.codigo, s.nombre, s.descripcion, o.nombre AS 'empresa',s.empresa AS 'codigoEmpresa', s.categoria, s.estado, s.fechaPublicacion, s.fechaLimite, s.salario, s.modalidad, s.ubicacion, s.detalles, s.usuarioElegido FROM ofertas s JOIN usuarios o ON s.empresa = o.codigo  WHERE  s.codigo = ? AND s.fechaLimite > CURDATE() AND s.usuarioElegido = 0 AND s.estado = 'ACTIVA'";
        OfertaEmpresaInvitado oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, codigoN);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var descripcion = resultSet.getString("descripcion");
                    var empresa = resultSet.getString("empresa");
                    var codigoEmpresa = resultSet.getInt("codigoEmpresa");
                    var categoria = resultSet.getInt("categoria");
                    var estado = resultSet.getString("estado");
                    var fechaPublicacion = resultSet.getDate("fechaPublicacion");
                    var fechaLimite = resultSet.getDate("fechaLimite");
                    var salario = resultSet.getBigDecimal("salario");
                    var modalidad = resultSet.getString("modalidad");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new OfertaEmpresaInvitado(codigo,nombre,descripcion,empresa,codigoEmpresa,categoria,estado,fechaPublicacion,fechaLimite,salario,modalidad,ubicacion,detalles,usuarioElegido);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar oferta de empresa: " + e);
        }

        return oferta;
    }

    public Invitado vista() {
        String query = "SELECT * FROM invitado";
        Invitado vistau = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var vista = resultSet.getInt("vista");
                    vistau = new Invitado(codigo,vista);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al visita: " + e);
        }
        return vistau;
    }

    public void crearVistaInvitado(){
        String query = "INSERT INTO invitado VALUES(1,?)";
        if (vista() == null ) {
            try (var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, 0);
                preparedStatement.executeUpdate();
                System.out.println("Vista creada");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void actualizarVistaInvitado(){
        String query = "UPDATE invitado set vista = vista + ? WHERE codigo = 1";

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,1);
            preparedStatement.executeUpdate();
            System.out.println("Vista actualzada");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
