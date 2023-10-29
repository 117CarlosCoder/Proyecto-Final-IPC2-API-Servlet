package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.ApplicantDB;
import com.ipc2.proyectofinalservlet.data.CargaDB;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Entrevista;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Solicitudes;

import java.sql.Connection;
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

    public void crearCategoria(int usuario, int categoria){
        System.out.println("Completar Informacion");
        applicantDB.crearCategorias(usuario, categoria);

    }

    public void aplicarOferta(int oferta, int usuario, String mensaje){
        System.out.println("Aplicar Oferta");
        applicantDB.aplicarOferta(oferta,usuario,mensaje);

    }

    public List<Ofertas> listarOfeta(){
        System.out.println("Listar Ofertas");
        return applicantDB.listarOfertas();
    }

    public Ofertas listarOfetaCodigo(int codigo){
        System.out.println("Listar Oferta Codigo");
        return applicantDB.listarOfertasCodigo(codigo);
    }

    public List<Solicitudes> listarPostulaciones(int usuario){
        System.out.println("Listar Ofertas");
        return applicantDB.listarPostulaciones(usuario);
    }

    public void eliminarPostulacion(int codigo){
        System.out.println("Eliminar Postulacion");
        applicantDB.eliminarPostulacion(codigo);
    }

    public List<Entrevista> listarEntrevistas(int usuario){
        System.out.println("Listar Entrevistas");
        return applicantDB.listarEntrevistas(usuario);
    }
}
