package com.ipc2.proyectofinalservlet.model.Employer;

import com.ipc2.proyectofinalservlet.model.CargarDatos.Entrevista;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Solicitudes;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OfertasCarga {
    private int codigo;
    private String nombre;
    private String descripcion;
    private int empresa;
    private int categoria;
    private String estado;
    private Date fechaPublicacion;
    private Date fechaLimite;
    private BigDecimal salario;
    private String modalidad;
    private String ubicacion;
    private String detalles;
    private List<Entrevista> entrevistas;
    private List<Solicitudes> solicitudes;
    private int usuarioElegido;
}
