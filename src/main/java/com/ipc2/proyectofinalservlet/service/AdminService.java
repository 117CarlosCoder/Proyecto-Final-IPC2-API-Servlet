package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.AdminDB;
import com.ipc2.proyectofinalservlet.model.Admin.*;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.Employer.NumTelefono;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class AdminService {
    private final AdminDB adminDB;

    public AdminService(Connection conexion){
        this.adminDB = new AdminDB(conexion);
    }

    public boolean crearCategoria(int codigo,String nombre, String descripcion){
        System.out.println("Crear Categoria");
        if ( nombre.isEmpty() || descripcion.isEmpty()) return false;
        List<Categoria> categorias = adminDB.listarCategorias();
        for(Categoria categoria : categorias){
            if (Objects.equals(categoria.getNombre(), nombre)){
                return false;
            }
        }
        adminDB.crearCartegoria(codigo,nombre, descripcion);
        return true;
    }

    public boolean crearComision(BigDecimal comision, String fecha){
        System.out.println("Crear Comsion");
        if (comision.compareTo(BigDecimal.ZERO) <= 0 || fecha.isEmpty()) return false;
        adminDB.crearComision(comision, fecha);
        return true;
    }
    public List<RegistroComision> listarRegistroComision(){
        System.out.println("listar registro comision");
        return adminDB.listarRegistroComision();
    }

    public boolean actualizarCategoria(int codigo, String nombre, String descripcion){
        System.out.println("Actualizar Categoria");
        if (nombre.isEmpty() || descripcion.isEmpty()) return false;
        adminDB.cambiarCategoria(codigo,nombre,descripcion);
        return true;
    }

    public boolean actualizarTelefono(List<NumTelefono> telefono){
        System.out.println("Actualizar Telefonos");
        System.out.println(telefono);
        if (telefono.get(0).getNumero() <= 0) return false;
        adminDB.actualizarTelefonos(telefono.get(0));
        if (telefono.size() > 1) {
            if ((telefono.get(1)) != null) {
                if (telefono.get(1).getNumero() <= 0) return false;
                adminDB.actualizarTelefonos(telefono.get(1));
            }
            if (telefono.size() > 2) {
                if ((telefono.get(2)) != null) {
                    if (telefono.get(2).getNumero() <= 0) return false;
                    adminDB.actualizarTelefonos(telefono.get(2));
                }
            }
            return true;
        }
        return  true;
    }

    public Categoria listarCategoriaCodigo(int codigo){
        System.out.println("listar categoria Codigo Empresa");
        return adminDB.listarCategoriasCodigo(codigo);
    }
    public List<Categoria> listarCategorias(){
        System.out.println("listar categorias");
        return adminDB.listarCategorias();
    }

    public Dashboard listarDashboard(){
        System.out.println("listar dashboard");
        return adminDB.dashboardVista();
    }

    public Comision listarComision( ){
        System.out.println("Listar Comision");
        return adminDB.listarComision();
    }

    public List<TopEmpleadores> topEmpleadores(){
        System.out.println("Top Empleadores");
        return adminDB.top5EmpleadoresMasOfertas();
    }

    public CantidadTotal ofertaTotal(int categoria, String fechaA, String fechaB){
        System.out.println("Oferta Total");
        return adminDB.ofertaTotalFecha(categoria, fechaA, fechaB);
    }

    public CantidadTotal ofertaTotalSinFecha(){
        System.out.println("Oferta Total");
        return adminDB.ofertaTotal();
    }
    public List<IngresoTotal> ingresoTotalFecha( String fechaA, String fechaB){
        System.out.println("Top Ingresos");
        return adminDB.ingresoTotalFecha(fechaA, fechaB);
    }

    public List<Usuarios> listarUsuario(String rol){
        System.out.println("listar usuarios");
        return adminDB.listarUsuarios(rol);
    }

    public List<Usuarios> listarUsuariosCodigo(int codigo){
        System.out.println("listar usuarios");
        return adminDB.listarUsuariosCodigo(codigo);
    }

    public Usuarios listarUsuarioE(int codigo){
        System.out.println("listar usuario E");
        return adminDB.listarUsuario(codigo);
    }

    public List<NumTelefono> listarTelefonos(int codigo){
        System.out.println("listar telefonos");
        return adminDB.listarTelefonos(codigo);
    }
    public boolean actualizarUsuario(User usuario){
        System.out.println("Actualizar Usuario");
        if (usuario.getDireccion().isEmpty() || usuario.getCodigo() <= 0 || usuario.getUsername().isEmpty() || usuario.getEmail().isEmpty() || usuario.getNombre().isEmpty() || usuario.getCUI().isEmpty() || new BigInteger(usuario.getCUI()).compareTo(BigInteger.ZERO) <= 0  ) return  false;
        System.out.println("Actualizando");
        List<Usuarios> usuarios = listarUsuariosCodigo(usuario.getCodigo());
        for(Usuarios usuarion : usuarios ){
            if (usuario.getEmail().equals(usuarion.getEmail())){
                return false;
            }
        }
        if (Objects.equals(usuario.getRol(), "Solicitante")){

        }
        adminDB.actualizarUsuario(usuario);
        return true;
    }


    public boolean crearTelefonos( List<NumTelefono> numTelefonos){
        System.out.println("Crear Telefonos");
        System.out.println(numTelefonos);
        if (numTelefonos == null ) return false;
        if (!numTelefonos.isEmpty()) {
            if (numTelefonos.get(0).getNumero() <= 0) return false;
            adminDB.crearTelefonos(numTelefonos.get(0));

            if (numTelefonos.size() > 1) {
                if ((numTelefonos.get(1)) != null) {
                    if (numTelefonos.get(1).getNumero() <= 0) return false;
                    adminDB.crearTelefonos(numTelefonos.get(1));
                }
                if (numTelefonos.size() > 2) {
                    if ((numTelefonos.get(2)) != null) {
                        if (numTelefonos.get(2).getNumero() <= 0) return false;
                        adminDB.crearTelefonos(numTelefonos.get(2));
                    }
                }
            }
        }
        return true;
    }

    public boolean crearTelefonosUsarioP2( List<TelefonosUsuario> numTelefonos){
        System.out.println("Crear Telefonos");
        if (numTelefonos == null ) return false;
        if (!numTelefonos.isEmpty()) {
            adminDB.crearTelefonosUsuario(numTelefonos.get(0));

            if (numTelefonos.size() > 1) {
                if ((numTelefonos.get(1)) != null) {
                    if (numTelefonos.get(1).getNumero() <= 0) return false;
                    adminDB.crearTelefonosUsuario(numTelefonos.get(1));
                }
                if (numTelefonos.size() > 2) {
                    if ((numTelefonos.get(2)) != null) {
                        if (numTelefonos.get(2).getNumero() <= 0) return false;
                        adminDB.crearTelefonosUsuario(numTelefonos.get(2));
                    }
                }
            }
        }
        return true;
    }



    public void eliminarCategoria(int codigo){
        System.out.println("Eliminar categoria");
        adminDB.eliminarCategoria(codigo);
    }

    public boolean eliminarOfetas(int codigo){
        System.out.println("eliminar ofertas");
        adminDB.eliminarSolicitu(codigo);
        adminDB.eliminarOfertas(codigo);
        return true;
    }

    public void suspenderUsuario(String username, boolean estado){
        System.out.println("suspender usuario");
        adminDB.suspenderUsuario(username, estado);
    }
}
