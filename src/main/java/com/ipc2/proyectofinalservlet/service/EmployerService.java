package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.EmployerDB;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Entrevista;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Postulante;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Solicitudes;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class EmployerService {
    private EmployerDB employerDB;

    public EmployerService( Connection conexion){
        this.employerDB = new EmployerDB(conexion);
    }

    public void completarInformacion(String mision, String vision, int codigo){
        System.out.println("Completar Informacion");
        employerDB.completarInformacion(mision,vision,codigo);
    }

    public void completarInformacionTarjeta(int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad){
        System.out.println("Completar Informacion Tarjeta");
        employerDB.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad);
    }

    public void crearOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido){
        System.out.println("Crear Oferta");
        employerDB.crearOferta(nombre,descripcion,empresa, categoria,estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
    }

    public void actualizarOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido, int codigo){
        System.out.println("Actualizar Oferta");
        employerDB.actualizarOferta(nombre,descripcion,empresa, categoria,estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido,codigo);
    }

    public void actualizarOfertaEstado(int codigo){
        System.out.println("Actualizar Oferta");
        employerDB.actualizarOfertaEstado(codigo);
    }
    public List<Ofertas> listarOfertas(){
        System.out.println("listar ofertas");
        return employerDB.listarOfertas();
    }

    public List<Ofertas> listarOfertasEmpresa(int empresa){
        System.out.println("listar ofertas Empresa");
        return employerDB.listarOfertasEmpresa(empresa);
    }

    public List<Ofertas> listarOfertasEmpresaPostulaciones(int empresa){
        System.out.println("listar ofertas Empresa Postulaciones");
        return employerDB.listarOfertasEmpresaPostulantes(empresa);
    }

    public List<Solicitudes> listarPostulaciones(int empresa){
        System.out.println("listar Postulaciones");
        return employerDB.listarPostulantes(empresa);
    }

    public Postulante obtenerPostulante(int usuario){
        System.out.println("listar Postulaciones");
        return employerDB.obtenerPostulante(usuario);
    }
    public void eliminarOfetas(int codigo){
        System.out.println("eliminar ofertas");
        employerDB.eliminarOfertas(codigo);
    }

    public void generarEntrevista(int codigo, int usuario, Date fecha, String hora, String ubicacion){
        System.out.println("generar entrevista");
        employerDB.generarEntrevista(codigo,usuario,fecha,hora,ubicacion);
    }

    public List<Entrevista> listarEntrevistas(){
        System.out.println("listar entrevista");
        return employerDB.listarEntrevista();
    }

    public void finalizarEntrevista(String notas, int usuario , int codigo){
        System.out.println("finalizar entrevista");
        employerDB.finalizarEntrevista(notas,usuario,codigo);
    }
}
