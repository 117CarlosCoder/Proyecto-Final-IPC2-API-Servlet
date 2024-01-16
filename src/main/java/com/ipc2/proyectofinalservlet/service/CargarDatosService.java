package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.data.SesionDB;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdf;
import com.ipc2.proyectofinalservlet.model.Applicant.UsuarioPdfJson;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;
import com.ipc2.proyectofinalservlet.model.User.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class CargarDatosService {

    private final CargaDB cargaDB;

    public CargarDatosService( Connection conexion){
        this.cargaDB = new CargaDB(conexion);
    }

    public Comision crearComision(BigDecimal cantidad){
        System.out.println("Crear comision");
        return cargaDB.crearComision(cantidad);
    }

    public void vaciarBSe(){
        System.out.println("Vaciar Base");
        cargaDB.reiniciarBase();
    }

    public boolean validarBase(){
        System.out.println("Validar Base");
        return cargaDB.validandoBase();
    }

    public Comision actualizarComision(BigDecimal cantidad){
        System.out.println("Actualizar comision");
        if (cantidad.compareTo(BigDecimal.ZERO) <=0) return null;
        return cargaDB.cambiarComision(cantidad);
    }
    public boolean listarComision(){
        System.out.println("listar comision");
        Comision comision = cargaDB.listarComision();
        return comision != null;
    }

    public List<User> listarUsuariosPdf(){
        System.out.println("listar Usuarios PDF");
        return cargaDB.listarUsuariosPdf();
    }

    public void guardarPdf(List<UsuarioPdfJson>usuarioPdf){
        System.out.println("guardar Usuarios PDF");
        cargaDB.guardarPdf(usuarioPdf);
    }
}
