package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.EmployerDB;
import com.ipc2.proyectofinalservlet.model.CargarDatos.*;
import com.ipc2.proyectofinalservlet.model.Employer.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EmployerService {
    private EmployerDB employerDB;

    public EmployerService( Connection conexion){
        this.employerDB = new EmployerDB(conexion);
    }


    public boolean completarInformacion(String mision, String vision, int codigo){
        System.out.println("Completar Informacion");
        if (mision.isEmpty() || vision.isEmpty() || codigo == 0 ) return false;
        employerDB.completarInformacion(mision,vision,codigo);
        return true;
    }

    public boolean completarInformacionTarjeta(int codigo, int codigoUsuario, String Titular, String numero, int codigoSeguridad, java.sql.Date fechaExpiracion, BigDecimal cantidad){
        System.out.println("Completar Informacion Tarjeta");
        if (codigoUsuario == 0 || Titular.isEmpty() || numero.equals("0") || fechaExpiracion == null || cantidad.compareTo(BigDecimal.ZERO)<0) return false;
        employerDB.completarInformacionTarjeta(codigo,codigoUsuario,Titular,numero,codigoSeguridad,fechaExpiracion,cantidad);
        return true;
    }

    public boolean actualizarInformacionTarjeta(TarjetaDatos tarjeta, int usuario){
        System.out.println("Actualizar Informacion Tarjeta");
        if (tarjeta.getTitular().isEmpty() || tarjeta.getNumero().compareTo(BigDecimal.ZERO) <= 0|| tarjeta.getCantidad().compareTo(BigDecimal.ZERO) <= 0 || tarjeta.getCodigoSeguridad() <= 0 || tarjeta.getFechaExpiracion() == null ) return false;
        employerDB.actualizarInformacionTarjeta(tarjeta, usuario);
        return true;
    }

    public boolean crearOferta(Ofertas oferta, int empresa){
        System.out.println("Crear Oferta");
        long fechaActualMillis = System.currentTimeMillis();
        Date fechaActual = new Date(fechaActualMillis);
        if (oferta.getNombre().isEmpty() || oferta.getDescripcion().isEmpty() || oferta.getCategoria() == 0  || oferta.getFechaLimite() == null || oferta.getSalario().compareTo(BigDecimal.ZERO)<=0 || oferta.getModalidad().isEmpty() || oferta.getUbicacion().isEmpty() || oferta.getDetalles().isEmpty() || oferta.getFechaLimite().before(fechaActual)|| oferta.getFechaLimite().equals(fechaActual)) return false;
        employerDB.crearOferta(oferta.getNombre(),oferta.getDescripcion(),empresa, oferta.getCategoria(),oferta.getEstado(), oferta.getFechaPublicacion(), oferta.getFechaLimite(), oferta.getSalario(), oferta.getModalidad(), oferta.getUbicacion(), oferta.getDetalles(), oferta.getUsuarioElegido());
        return true;
    }

    public boolean actualizarOferta(String nombre, String descripcion, int empresa, int categoria, String estado, Date fechaPublicacion, Date fechaLimite, BigDecimal salario, String modalidad, String ubicacion, String detalles, int usuarioElegido, int codigo){
        System.out.println("Actualizar Oferta");
        if (nombre.isEmpty() || descripcion.isEmpty() || categoria == 0  || fechaLimite == null || salario.compareTo(BigDecimal.ZERO) <= 0 || ubicacion.isEmpty() || detalles.isEmpty()) return false;
        employerDB.actualizarOferta(nombre,descripcion,empresa, categoria,estado, fechaPublicacion, fechaLimite, salario, modalidad, ubicacion, detalles, usuarioElegido,codigo);
        return true;
    }

    public Ofertas listarOfetaCodigo(int codigo){
        System.out.println("Listar Oferta Codigo");
        return employerDB.listarOfertasCodigo(codigo);
    }

    public boolean actualizarOfertaEstado(int codigo){
        System.out.println("Actualizar Oferta");
        if (codigo == 0 ) return false;
        employerDB.actualizarOfertaEstado(codigo);
        return true;
    }

    public void crearNotificacion(String mensaje, int empresa, int usuario){
        System.out.println("crear notificacion");
        employerDB.crearNotificacion(mensaje, empresa, usuario);
    }
    public List<Ofertas> listarOfertas(){
        System.out.println("listar ofertas");
        return employerDB.listarOfertas();
    }

    public List<Ofertas> listarOfertasEmpresa(int empresa){
        System.out.println("listar ofertas Empresa");
        return employerDB.listarOfertasEmpresa(empresa);
    }

    public List<Ofertas> listarOfertasEmpresaFecha(java.sql.Date fechaA, java.sql.Date fechaB, String fechaS, int empresa){
        System.out.println("listar ofertas Empresa");
        return employerDB.listarOfertasEmpresaFecha(fechaA, fechaB, fechaS, empresa);
    }

    public TarjetaDatos listarTarjetaEmpresa(int empresa){
        System.out.println("listar Tarjeta Empresa");
        return employerDB.listarTarjetaEmpresa(empresa);
    }

    public List<Ofertas> listarOfertasEmpresaPos(int empresa){
        System.out.println("listar ofertas Empresa");
        return employerDB.listarOfertasEmpresaPostulaciones(empresa);
    }

    public List<Ofertas> listarOfertasEmpresaEnt(int empresa){
        System.out.println("listar ofertas Empresa");
        return employerDB.listarOfertasEmpresaEntrevistas(empresa);
    }

    public List<Ofertas> listarOfertasEmpresaPostulaciones(int empresa){
        System.out.println("listar ofertas Empresa Postulaciones");
        return employerDB.listarOfertasEmpresaPostulantes(empresa);
    }

    public List<EstadoSolicitudPostulante> listarPostulaciones(int codigo, int empresa){
        System.out.println("listar Postulaciones");
        return employerDB.listarPostulaciones(codigo,empresa);
    }

    public Postulante obtenerPostulante(int usuario, int oferta){
        System.out.println("listar Postulaciones");
        return employerDB.obtenerPostulante(usuario, oferta);
    }
    public boolean eliminarOfetas(int codigo){
        System.out.println("eliminar ofertas");
        if(Objects.equals(listarOfetaCodigo(codigo).getEstado(), "FINALIZADA") || Objects.equals(listarOfetaCodigo(codigo).getEstado(), "ENTRVISTA") || listarOfetaCodigo(codigo) == null) return false;
        employerDB.eliminarSolicitu(codigo);
        employerDB.eliminarOfertas(codigo);
        return true;
    }

    public void generarEntrevista(int codigo,int codigoOferta, int usuario, Date fecha, String hora, String ubicacion){
        System.out.println("generar entrevista");
        employerDB.generarEntrevista(codigo,codigoOferta,usuario,fecha,hora,ubicacion);
    }

    public List<EntrevistaInfo> listarEntrevistas(int codigo, int empresa)   {
        System.out.println("listar entrevista");
        return employerDB.listarEntrevista(codigo,empresa);
    }

    public List<EntrevistaInfo> listarEntrevistasContratacion(int codigop ,int empresa)   {
        System.out.println("listar entrevista");
        return employerDB.listarEntrevistaContratacion(codigop,empresa);
    }

    public List<OfertaCostos> listarCostosOfertas(int empresa){
        System.out.println("Listar Costos");
        return employerDB.listarOfertasCostos(empresa);
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

    public boolean finalizarEntrevista(String notas, int usuario , int codigo){
        System.out.println("finalizar entrevista");
        if (notas.isEmpty() || usuario == 0 || codigo == 0) return false;
        employerDB.finalizarEntrevista(notas,usuario,codigo);
        return true;
    }

    public boolean contratar(int usuario, int codigo){
        System.out.println("Contratar");
        if (usuario == 0 || codigo == 0) return false;
        employerDB.contratar(usuario, codigo);
        return true;
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
        estado = new Estado(Estados.FINALIZADA.toString());
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

    public void actualizarEstadoOferta(){
        employerDB.actualizarEstadoOferta();
    }

    public boolean faseEntrevista(int codigo, int empresa){
        List<EntrevistaInfo> entrevistaInfos = listarEntrevistas(codigo,empresa);
        if (entrevistaInfos.isEmpty()) return false;
        employerDB.faseEntrevistas(codigo, empresa);
        return true;
    }

    public boolean faseContratacion(int codigo, int empresa){
        List<EntrevistaInfo> entrevistaInfos = listarEntrevistasContratacion(codigo,empresa);
        if (entrevistaInfos.isEmpty()) return false;
        employerDB.faseEntrevistas(codigo, empresa);
        return true;
    }
}
