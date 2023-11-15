package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.AdminDB;
import com.ipc2.proyectofinalservlet.model.Admin.*;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.CargarDatos.EntrevitaN;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class AdminService {
    private final AdminDB adminDB;

    public AdminService(Connection conexion){
        this.adminDB = new AdminDB(conexion);
    }

    public void crearCategoria(int codigo,String nombre, String descripcion){
        System.out.println("Crear Categoria");
        adminDB.crearCartegoria(codigo,nombre, descripcion);
    }

    public void crearComision(BigDecimal comision, String fecha){
        System.out.println("Crear Comsion");
        adminDB.crearComision(comision, fecha);
    }
    public List<RegistroComision> listarRegistroComision(){
        System.out.println("listar registro comision");
        return adminDB.listarRegistroComision();
    }

    public Categoria actualizarCategoria(int codigo,String nombre, String descripcion){
        System.out.println("Actualizar Categoria");
        return adminDB.cambiarCategoria(codigo,nombre,descripcion);
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
        System.out.println("Top Empleadores");
        return adminDB.ofertaTotalFecha(categoria, fechaA, fechaB);
    }

    public List<IngresoTotal> ingresoTotalFecha( String fechaA, String fechaB){
        System.out.println("Top Ingresos");
        return adminDB.ingresoTotalFecha(fechaA, fechaB);
    }

    public void actualizarComision(int cantidad){
        System.out.println("Actualizar Comision");
        adminDB.actualizarComision(cantidad);
    }
}
