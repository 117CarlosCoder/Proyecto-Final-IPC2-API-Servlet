package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.EmployerDB;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.Employer.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public void completarInformacionTarjeta(int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad, java.sql.Date fechaExpiracion, BigDecimal cantidad){
        System.out.println("Completar Informacion Tarjeta");
        employerDB.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad,fechaExpiracion,cantidad);
    }

    public void crearOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido){
        System.out.println("Crear Oferta");
        employerDB.crearOferta(nombre,descripcion,empresa, categoria,estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido);
    }

    public void actualizarOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido, int codigo){
        System.out.println("Actualizar Oferta");
        employerDB.actualizarOferta(nombre,descripcion,empresa, categoria,estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido,codigo);
    }

    public Ofertas listarOfetaCodigo(int codigo){
        System.out.println("Listar Oferta Codigo");
        return employerDB.listarOfertasCodigo(codigo);
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

    public List<Ofertas> listarOfertasEmpresaFecha(java.sql.Date fechaA, java.sql.Date fechaB){
        System.out.println("listar ofertas Empresa");
        return employerDB.listarOfertasEmpresaFecha(fechaA, fechaB);
    }

    public List<Ofertas> listarOfertasEmpresaPos(int empresa){
        System.out.println("listar ofertas Empresa");
        return employerDB.listarOfertasEmpresaPostulaciones(empresa);
    }

    public List<Ofertas> listarOfertasEmpresaPostulaciones(int empresa){
        System.out.println("listar ofertas Empresa Postulaciones");
        return employerDB.listarOfertasEmpresaPostulantes(empresa);
    }

    public List<EstadoSolicitudPostulante> listarPostulaciones(int codigo){
        System.out.println("listar Postulaciones");
        return employerDB.listarPostulaciones(codigo);
    }

    public Postulante obtenerPostulante(int usuario, int oferta){
        System.out.println("listar Postulaciones");
        return employerDB.obtenerPostulante(usuario, oferta);
    }
    public void eliminarOfetas(int codigo){
        System.out.println("eliminar ofertas");
        employerDB.eliminarSolicitu(codigo);
        employerDB.eliminarOfertas(codigo);
    }

    public void generarEntrevista(int codigo,int codigoOferta, int usuario, Date fecha, String hora, String ubicacion){
        System.out.println("generar entrevista");
        employerDB.generarEntrevista(codigo,codigoOferta,usuario,fecha,hora,ubicacion);
    }

    public List<EntrevistaInfo> listarEntrevistas(int empresa)   {
        System.out.println("listar entrevista");
        return employerDB.listarEntrevista(empresa);
    }

    public List<OfertaCostos> listarCostosOfertas( ){
        System.out.println("Listar Costos");
        return employerDB.listarOfertasCostos();
    }
    public List<Categoria> listarCategorias(){
        System.out.println("listar Categorias");
        return employerDB.listarCategorias();
    }

    public List<EntrevistaFecha> listarFechaEntrevista(java.sql.Date fecha, int empresa, String estado){
        System.out.println("listar Entrevista fecha");
        return employerDB.listarEntrevistaFecha(fecha,empresa, estado);
    }

    public List<OfertasEmpresaFecha> listarFechaOferta(String fechaA, String fechaB, String estado,int empresa){
        System.out.println("listar Ofertas fecha");
        return employerDB.listarOfertaFecha(fechaA,fechaB, estado,empresa);
    }

    public void finalizarEntrevista(String notas, int usuario , int codigo){
        System.out.println("finalizar entrevista");
        employerDB.finalizarEntrevista(notas,usuario,codigo);
    }

    public void contratar(int usuario, int codigo){
        System.out.println("Contratar");
        employerDB.contratar(usuario, codigo);
    }

    public List<Modalidades> listarModalidades(){
        List<Modalidades> modalidad= new ArrayList<>();
        Modalidades modalidades= null;
        modalidades = new Modalidades(Modalidad.PRESENCIAL.toString());
        modalidad.add(modalidades);
        modalidades = new Modalidades(Modalidad.REMOTO.toString());
        modalidad.add(modalidades);
        modalidades = new Modalidades(Modalidad.HIBRIDO.toString());
        modalidad.add(modalidades);
        return modalidad;
    }

    public List<Estado> listarEstados(){
        List<Estado> estados= new ArrayList<>();
        Estado estado= null;
        estado = new Estado(Estados.PENDIENTE.toString());
        estados.add(estado);
        estado = new Estado(Estados.FINALIZADO.toString());
        estados.add(estado);
        return estados;
    }

    public List<EstadoOferta> listarEstadosOferta(){
        List<EstadoOferta> estadoOfertas= new ArrayList<>();
        EstadoOferta estadoOferta= null;
        estadoOferta = new EstadoOferta(EstadosOfertas.ACTIVA.toString());
        estadoOfertas.add(estadoOferta);
        estadoOferta = new EstadoOferta(EstadosOfertas.SELECCION.toString());
        estadoOfertas.add(estadoOferta);
        estadoOferta = new EstadoOferta(EstadosOfertas.ENTREVISTA.toString());
        estadoOfertas.add(estadoOferta);
        return estadoOfertas;
    }
}
