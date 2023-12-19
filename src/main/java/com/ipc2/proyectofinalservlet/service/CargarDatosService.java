package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.data.SesionDB;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Comision;

import java.math.BigDecimal;
import java.sql.Connection;

public class CargarDatosService {

    private final CargaDB cargaDB;

    public CargarDatosService( Connection conexion){
        this.cargaDB = new CargaDB(conexion);
    }

    public Comision crearComision(BigDecimal cantidad){
        System.out.println("Crear comision");
        return cargaDB.crearComision(cantidad);
    }
    public Comision actualizarComision(BigDecimal cantidad){
        System.out.println("Actualizar comision");
        return cargaDB.cambiarComision(cantidad);
    }
    public boolean listarComision(){
        System.out.println("listar comision");
        Comision comision = cargaDB.listarComision();
        return comision != null;
    }
}
