package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.ApplicantDB;
import com.ipc2.proyectofinalservlet.model.Applicant.*;
import com.ipc2.proyectofinalservlet.model.Employer.OfertaCostos;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
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

    public void guardarPfs(int usuario, InputStream inputStream){
        System.out.println("guardar pdf ");
        applicantDB.guardarPdf( usuario, inputStream);

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

    public List<OfertasEmpresa> listarOfeta(int usuarion){
        System.out.println("Listar Ofertas");
        return applicantDB.listarOfertas(usuarion);
    }

    public OfertasEmpresa listarOfetaCodigo(int codigo, int usuarion){
        System.out.println("Listar Oferta Codigo");
        return applicantDB.listarOfertasCodigo(codigo, usuarion);
    }
    public OfertasEmpresa listarOfetaCodigoSinEntrevista(int codigo, int usuarion){
        System.out.println("Listar Oferta Codigo sin e");
        return applicantDB.listarOfertasCodigoSinEntrevista(codigo, usuarion);
    }

    public List<OfertasEmpresa> lisatarOfertaNombre(String nombre, int usuario) {
        System.out.println("Listar Oferta Nombre");
        return applicantDB.listarOfertasNombre(nombre, usuario);
    }

    public List<Salario> listarSalarios() {
        System.out.println("Listar Salarios");
        return applicantDB.listarSalarios();
    }

    public List<OfertasEmpresa> listarOfertasFiltrados(Filtros filtros, int codigo) {
        System.out.println("Listar Ofertas Filtrados");
        return applicantDB.listarOfertasFiltros(filtros, codigo);
    }

    public List<OfertasEmpresa> listarOfertasSugerencia(int codigo){
        System.out.println("Listar Ofertas Sugerencia");
        return applicantDB.listarOfertasSugerencia(applicantDB.listarCategoriasusuario(codigo),codigo);
    }

    public List<Ubicacion> listarUbicaciones() {
        System.out.println("Listar Ubicaciones");
        return applicantDB.listarUbicaciones();
    }

    public List<OfertasEmpresaFecha> listarOfertasFecha(int usuario,String estado, String fechaA, String fechaB){
        System.out.println("Listar Oferta Fecha");
        return applicantDB.listarOfertasFecha(usuario,estado,fechaA,fechaB);
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

    public List<EntrevistaOferta> listarEntrevistaInfo(int usuario){
        System.out.println("Listar Entrevistas");
        return applicantDB.listarEntrevistasInfo(usuario);
    }

    public List<RegistroPostulacion> listarRegistroPostulacio (int usuario, String fechaA, String fechaB){
        System.out.println("Listar Registro Postulacion");
        return applicantDB.listarRegistros(usuario, fechaA, fechaB);
    }
    public void registrarPostulacion(int usuario, String oferta, String fecha){
        System.out.println("Registrar Postulacion");
        applicantDB.registroPostulaciones(usuario, oferta,fecha);
    }

    public UsuarioPdf listarCurriculum(int usuario){
        System.out.println("Registrar Postulacion");
        return applicantDB.visualizarPdf(usuario);
    }

}
