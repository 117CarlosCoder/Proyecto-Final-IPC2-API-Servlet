package com.ipc2.proyectofinalservlet.model.CargarDatos;

import com.ipc2.proyectofinalservlet.model.Admin.Admin;
import com.ipc2.proyectofinalservlet.model.Applicant.Usuarios;
import com.ipc2.proyectofinalservlet.model.Employer.Employer;
import com.ipc2.proyectofinalservlet.model.Employer.OfertasCarga;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CargarDatosFinal {
    private List<Categoria> categorias;
    private Admin admin;
    private List<Employer> empleadores;
    private List<Usuarios> usuarios;
    private List<OfertasCarga> ofertas;
}
