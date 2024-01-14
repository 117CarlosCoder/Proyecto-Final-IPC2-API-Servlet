package com.ipc2.proyectofinalservlet.data;

import com.ipc2.proyectofinalservlet.controller.UserController.Encriptador;
import com.ipc2.proyectofinalservlet.model.Admin.*;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.CargarDatos.OfertasEmpresaFecha;
import com.ipc2.proyectofinalservlet.model.Employer.NumTelefono;
import com.ipc2.proyectofinalservlet.model.Employer.Telefonos;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminDB {

    private final Connection conexion;
    private Encriptador encriptador;

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

    public void crearComision(BigDecimal comision, String fecha){
        String query = "INSERT INTO historialComision VALUES(?,?,?,NOW())";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, Types.NULL);
            preparedStatement.setBigDecimal(2,comision);
            preparedStatement.setDate(3, Date.valueOf(fecha));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearTelefonos( NumTelefono telefonos){
        System.out.println(telefonos);
        String query = "INSERT INTO telefonos VALUES(NULL,?,?)";
        try(var preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setInt(1, telefonos.getCodigoUsuario());
                preparedStatement.setInt(2, telefonos.getNumero());
                preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void crearTelefonosUsuario( TelefonosUsuario telefonos){
        System.out.println(telefonos);
        String query = "INSERT INTO telefonos VALUES(NULL,?,?)";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1, listarCodigo(telefonos.getUsername()));
            preparedStatement.setInt(2, telefonos.getNumero());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int listarCodigo(String username){
        String query = "SELECT u.codigo FROM usuarios u WHERE username = ?";
        int codigo = 0;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);


            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    codigo = resultSet.getInt("codigo");
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar usuario: " + e);
        }
        return codigo;
    }

    public List<RegistroComision> listarRegistroComision(){
        String query = " SELECT * FROM historialComision ORDER BY codigo DESC";
        List<RegistroComision> registroComisions = new ArrayList<>();
        RegistroComision registroComision = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var comision = resultset.getBigDecimal("comision");
                var fechaInicial = resultset.getString("fechaInicial");
                var fechaFinal = resultset.getString("fechaFinal");
                registroComision = new RegistroComision(codigo,comision,fechaInicial, fechaFinal);
                registroComisions.add(registroComision);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return registroComisions;
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

    public CantidadTotal ofertaTotalFecha(int categorian, String fechaA, String fechaB){
        String query = "SELECT IFNULL(SUM(c.cobro), 0) AS cantidadTotal, IFNULL(g.nombre, 'Todas') AS 'categoria' FROM cobroComision c INNER JOIN categoria g ON g.codigo = c.categoria WHERE c.categoria = ? AND c.fecha BETWEEN ? AND ? GROUP BY g.nombre";
        CantidadTotal cantidadTotal = null;

        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, categorian);
            preparedStatement.setDate(2, Date.valueOf(fechaA));
            preparedStatement.setDate(3, Date.valueOf(fechaB));



            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var cantidad = resultSet.getInt("cantidadTotal");
                    var categoria = resultSet.getString("categoria");
                    cantidadTotal = new CantidadTotal(cantidad, categoria);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar cantidad Total: " + e);
        }

        return cantidadTotal;
    }

    public CantidadTotal ofertaTotal(){
        String query = "SELECT IFNULL(SUM(c.cobro), 0) AS cantidadTotal, IFNULL(g.nombre, 'Todas') AS 'categoria' FROM cobroComision c INNER JOIN categoria g ON g.codigo = c.categoria GROUP BY g.nombre WITH ROLLUP";
        CantidadTotal cantidadTotal = null;

        try (var preparedStatement = conexion.prepareStatement(query)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var cantidad = resultSet.getInt("cantidadTotal");
                    var categoria = resultSet.getString("categoria");
                    cantidadTotal = new CantidadTotal(cantidad, categoria);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar cantidad Total: " + e);
        }

        return cantidadTotal;
    }

    public List<IngresoTotal> ingresoTotalFecha(String fechaA, String fechaB){
        String query = "SELECT u.codigo, u.nombre, COUNT(DISTINCT o.codigo) AS cantidadOfertas, SUM(s.cobro) AS total FROM usuarios u INNER JOIN ofertas o ON u.codigo = o.empresa LEFT JOIN cobroComision s ON s.codigoOferta = o.codigo WHERE o.estado = 'FINALIZADO' AND s.fecha BETWEEN ? AND ? GROUP BY u.codigo, u.nombre ORDER BY total DESC LIMIT 5";
        if (fechaA == null || fechaA.isEmpty()|| fechaB == null|| fechaB.isEmpty()){
            System.out.println("otra query");
            query = "SELECT u.codigo, u.nombre, COUNT(DISTINCT o.codigo) AS cantidadOfertas, SUM(s.cobro) AS total FROM usuarios u INNER JOIN ofertas o ON u.codigo = o.empresa LEFT JOIN cobroComision s ON s.codigoOferta = o.codigo WHERE o.estado = 'FINALIZADO' GROUP BY u.codigo, u.nombre ORDER BY total DESC LIMIT 5";
        }
        List<IngresoTotal> ingresoTotals = new ArrayList<>();
        IngresoTotal ingresoTotal = null;

        try (var preparedStatement = conexion.prepareStatement(query)) {

            if ((fechaA != null) & !fechaA.isEmpty() & (fechaB != null) & !fechaB.isEmpty()) {
                preparedStatement.setDate(1, Date.valueOf(fechaA));
                preparedStatement.setDate(2, Date.valueOf(fechaB));
            }


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var cantidadOfertas = resultSet.getInt("cantidadOfertas");
                    var total = resultSet.getBigDecimal("total");
                    ingresoTotal = new IngresoTotal(codigo,nombre,cantidadOfertas,total);
                    ingresoTotals.add(ingresoTotal);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar ingreso Total: " + e);
        }

        return ingresoTotals;
    }

    public List<IngresoTotal> ingresoTotalSinFecha(String fechaA, String fechaB){
        String query = "SELECT u.codigo, u.nombre, COUNT(DISTINCT o.codigo) AS cantidadOfertas, SUM(s.cobro) AS total FROM usuarios u INNER JOIN ofertas o ON u.codigo = o.empresa LEFT JOIN cobroComision s ON s.codigoOferta = o.codigo WHERE o.estado = 'FINALIZADO' GROUP BY u.codigo, u.nombre ORDER BY total DESC LIMIT 5;";
        List<IngresoTotal> ingresoTotals = new ArrayList<>();
        IngresoTotal ingresoTotal = null;

        try (var preparedStatement = conexion.prepareStatement(query)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var cantidadOfertas = resultSet.getInt("cantidadOfertas");
                    var total = resultSet.getBigDecimal("total");
                    ingresoTotal = new IngresoTotal(codigo,nombre,cantidadOfertas,total);
                    ingresoTotals.add(ingresoTotal);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar ingreso Total: " + e);
        }

        return ingresoTotals;
    }


    public List<TopEmpleadores> top5EmpleadoresMasOfertas(){
        String query = "SELECT u.codigo, u.nombre, COUNT(*) AS cantidad FROM usuarios u INNER JOIN ofertas o ON u.codigo = o.empresa GROUP BY u.codigo, u.nombre ORDER BY cantidad DESC LIMIT 5";
        List<TopEmpleadores> empleadores = new ArrayList<>();
        TopEmpleadores empleador = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var nombre = resultset.getString("nombre");
                var cantidad = resultset.getInt("cantidad");
                empleador = new TopEmpleadores(codigo,nombre,cantidad);
                empleadores.add(empleador);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empleadores;
    }

    public Comision listarComision(){
        String query = "SELECT * FROM comision";
        Comision comision = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var codigo = resultset.getInt("codigo");
                var cantidad = resultset.getBigDecimal("cantidad");
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

    public void actualizarUsuario(User usuario){
        String query = "UPDATE usuarios SET CUI=?,curriculum=?,direccion=?,email=?,fechaFundacion=?,fechaNacimiento=?,mision=?,nombre=?,username=?,vision=? where codigo=?";
        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1,usuario.getCUI());
            preparedStatement.setString(2,usuario.getCurriculum());
            preparedStatement.setString(3,usuario.getDireccion());
            preparedStatement.setString(4,usuario.getEmail());
            preparedStatement.setDate(5,usuario.getFechaFundacion());
            preparedStatement.setDate(6,usuario.getFechaNacimiento());
            preparedStatement.setString(7,usuario.getMision());
            preparedStatement.setString(8,usuario.getNombre());
            preparedStatement.setString(9,usuario.getUsername());
            preparedStatement.setString(10,usuario.getVision());
            preparedStatement.setInt(11,usuario.getCodigo());

            preparedStatement.executeUpdate();

        } catch (SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarTelefonos(NumTelefono telefono){
        String query = "UPDATE telefonos SET numero = ? WHERE codigo = ?";

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setInt(1,telefono.getNumero());
            preparedStatement.setInt(2, telefono.getCodigo());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al actualizar telefono : " + e);
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
        String query = "SELECT (SELECT COUNT(*) FROM usuarios WHERE rol = 'Empleador') AS cantidad_empleadores, (SELECT COUNT(*) FROM usuarios WHERE rol = 'Solicitante') AS cantidad_solicitantes, COALESCE(SUM(c.cobro), 0) AS cantidad_cobros, COALESCE((SELECT SUM(vista) FROM invitado), 0) AS total_vistas FROM cobroComision c";
        Dashboard dashboard = null;
        try(var select = conexion.prepareStatement(query)) {
            ResultSet resultset = select.executeQuery();
            while (resultset.next()) {
                var cantidad_empleadores = resultset.getInt("cantidad_empleadores");
                var cantidad_solicitantes = resultset.getInt("cantidad_solicitantes");
                var cantidad_cobros= resultset.getBigDecimal("cantidad_cobros");
                var total_vistas= resultset.getInt("total_vistas");
                dashboard = new Dashboard(cantidad_empleadores,cantidad_solicitantes,cantidad_cobros, total_vistas);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dashboard;
    }

    public List<Usuarios> listarUsuarios(String rol){
        String query = "SELECT * FROM usuarios WHERE rol = ?";
        List<Usuarios> usuarios = new ArrayList<>();
        Usuarios usuario = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, rol);


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var direccion = resultSet.getString("direccion");
                    var username = resultSet.getString("username");
                    var password = resultSet.getString("password");
                    var email = resultSet.getString("email");
                    var CUI = resultSet.getString("CUI");
                    var fechaNacimiento = resultSet.getDate("fechaNacimiento");
                    var fechaFundacion = resultSet.getDate("fechaFundacion");
                    var curriculum = resultSet.getString("curriculum");
                    var suspension = resultSet.getBoolean("suspension");
                    usuario = new Usuarios(codigo,nombre,direccion,username,password,"",email,CUI, fechaNacimiento,fechaFundacion,new String[]{}, curriculum,null,suspension);
                    usuarios.add(usuario);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e);
        }
        return usuarios;
    }

    public Usuarios listarUsuario(int codigon){
        String query = "SELECT * FROM usuarios WHERE codigo = ?";

        Usuarios usuario = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, codigon);


            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var nombre = resultSet.getString("nombre");
                    var direccion = resultSet.getString("direccion");
                    var username = resultSet.getString("username");
                    var password = resultSet.getString("password");
                    var sal = resultSet.getString("sal");
                    var email = resultSet.getString("email");
                    var CUI = resultSet.getString("CUI");
                    var fechaNacimiento = resultSet.getDate("fechaNacimiento");
                    var fechaFundacion= resultSet.getDate("fechaFundacion");
                    var curriculum = resultSet.getString("curriculum");
                    var suspension = resultSet.getBoolean("suspension");
                    usuario = new Usuarios(codigo,nombre,direccion,username,password,sal,email,CUI, fechaNacimiento,fechaFundacion,new String[]{}, curriculum,null, suspension);

                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e);
        }
        System.out.println("Listar usuario : " +usuario );
        return usuario;
    }

    public void eliminarCategoria(int codigo ){
        eliminarCategoriaUsuario(codigo);
        List<Ofertas> ofertas = listarOFerta(codigo);
        for (Ofertas oferta:ofertas) {
            eliminarCategoriaOfertaSolicitudes(oferta.getCodigo());
        }
        eliminarCategoriaOferta(codigo);
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM categoria WHERE codigo = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarCategoriaUsuario(int codigo ){
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM categoriaUsuario WHERE codigoCategoria = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarCategoriaOferta(int codigo ){
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM ofertas WHERE categoria = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }

    public void eliminarCategoriaOfertaSolicitudes(int codigo ){
        try (var preparedStatement = conexion.prepareStatement("DELETE FROM solicitudes WHERE codigoOferta = ?")) {
            preparedStatement.setInt(1, codigo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e);
        }
    }
    public List<Ofertas> listarOFerta(int codigon){
        String query = "SELECT * FROM ofertas WHERE categoria = ?";
        List<Ofertas> ofertas = new ArrayList<>();
        Ofertas oferta = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, codigon);

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
                    var modalidad = resultSet.getString("modalidad");
                    var ubicacion = resultSet.getString("ubicacion");
                    var detalles = resultSet.getString("detalles");
                    var usuarioElegido = resultSet.getInt("usuarioElegido");
                    oferta = new Ofertas(codigo, nombre, descripcion, empresa, categoria, estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
                    ofertas.add(oferta);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e);
        }
        return ofertas;
    }
    public List<NumTelefono> listarTelefonos(int codigon){
        String query = "SELECT * FROM telefonos WHERE codigoUsuario = ?";
        List<NumTelefono> telefonos = new ArrayList<>();
        NumTelefono telefono = null;
        try (var preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setInt(1, codigon);

            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var codigo = resultSet.getInt("codigo");
                    var codigoUsuario = resultSet.getInt("codigoUsuario");
                    var numero = resultSet.getInt("numero");
                    telefono = new NumTelefono(codigo,codigoUsuario,numero);
                    telefonos.add(telefono);
                }
            }
        }catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e);
        }
        return telefonos;
    }

    public void suspenderUsuario(String username, boolean estado){
        String query = "UPDATE usuarios SET suspension = ? WHERE username = ?";

        try(var preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setBoolean(1,estado);
            preparedStatement.setString(2,username);
            preparedStatement.executeUpdate();

        } catch (SQLException  e ) {
            throw new RuntimeException(e);
        }
    }

}
