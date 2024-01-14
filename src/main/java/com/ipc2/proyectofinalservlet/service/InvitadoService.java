package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.EmployerDB;
import com.ipc2.proyectofinalservlet.data.InvitadoDB;
import com.ipc2.proyectofinalservlet.model.Applicant.Filtros;
import com.ipc2.proyectofinalservlet.model.CargarDatos.OfertasEmpresa;
import com.ipc2.proyectofinalservlet.model.Invitado.OfertaEmpresaInvitado;
import com.ipc2.proyectofinalservlet.model.Invitado.OfertaInformacion;

import java.sql.Connection;
import java.util.List;

public class InvitadoService {
    private final InvitadoDB invitadoDB;

    public InvitadoService( Connection conexion){
        this.invitadoDB = new InvitadoDB(conexion);
    }

    public List<OfertasEmpresa> listarOfeta(){
        System.out.println("Listar Ofertas");
        return invitadoDB.listarOfertas();
    }

    public List<OfertasEmpresa> listarOfertaEmpresa(int empresa){
        System.out.println("Listar Ofertas");
        return invitadoDB.listarOfertasEmpresa(empresa);
    }


    public OfertaInformacion listarEmpresa(int empresa){
        System.out.println("Listar Empresa");
        return invitadoDB.listarEmpresa(empresa);
    }

    public List<OfertasEmpresa> listarOfertasFiltrados(Filtros filtros) {
        System.out.println("Listar Ofertas Filtrados");
        return invitadoDB.listarOfertasFiltros(filtros);
    }

    public OfertaEmpresaInvitado listarOfertasCodigoSinEntrevista(int codigo){
        System.out.println("Listar Oferta Codigo sin e");
        return invitadoDB.listarOfertasCodigoSinEntrevista(codigo);
    }

    public void actulizarVista(){
        System.out.println("Actualizar Vista");
         invitadoDB.actualizarVistaInvitado();
    }

    public void crearVista(){
        System.out.println("Crear Vista");
        invitadoDB.crearVistaInvitado();
    }
}
