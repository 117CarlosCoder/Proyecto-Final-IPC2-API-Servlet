package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.ApplicantDB;
import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.model.Applicant.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class ApplicantService {
    private ApplicantDB applicantDB;
    public ApplicantService( Connection conexion){
        this.applicantDB = new ApplicantDB(conexion);
    }


    public void completarInformacion(String curriculum, int usuario){
        System.out.println("Completar Informacion");
        applicantDB.completarInformacion(curriculum,usuario);

    }

    public void completarInformacionTarjeta(int codigo, int codigoUsuario, String Titular, int numero, int codigoSeguridad, Date fechaExpiracion){
        System.out.println("Completar Informacion Tarjeta");
        applicantDB.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad,fechaExpiracion);
    }

    public List<Categoria> listarCategorias(){
        System.out.println("listar Categorias");
        return applicantDB.listarCategorias();
    }

    public void crearCategoria(int usuario, int categoria){
        System.out.println("Completar Informacion");
        applicantDB.crearCategorias(usuario, categoria);

    }

    public void aplicarOferta(int oferta, int usuario, String mensaje){
        System.out.println("Aplicar Oferta");
        applicantDB.aplicarOferta(oferta,usuario,mensaje);

    }

    public List<OfertasEmpresa> listarOfeta(){
        System.out.println("Listar Ofertas");
        return applicantDB.listarOfertas();
    }

    public Ofertas listarOfetaCodigo(int codigo){
        System.out.println("Listar Oferta Codigo");
        return applicantDB.listarOfertasCodigo(codigo);
    }

    public List<EstadoSolicitud> listarPostulaciones(int usuario){
        System.out.println("Listar Ofertas");
        return applicantDB.listarPostulaciones(usuario);
    }

    public void eliminarPostulacion(int codigo){
        System.out.println("Eliminar Postulacion");
        applicantDB.eliminarPostulacion(codigo);
    }

    public List<OfertaCostos> listarCostos(int usuario ){
        System.out.println("Listar Costos");
        return applicantDB.listarOfertasCostos(usuario);
    }
    public List<EntrevitaN> listarEntrevistas(int usuario){
        System.out.println("Listar Entrevistas");
        return applicantDB.listarEntrevistas(usuario);
    }


}
