package com.ipc2.proyectofinalservlet.model.Applicant;

import com.ipc2.proyectofinalservlet.model.CargarDatos.Categoria;
import com.ipc2.proyectofinalservlet.model.Employer.Tarjeta;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import lombok.*;

import java.sql.Date;
import java.util.List;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Usuarios {
    private int codigo;
    private String nombre;
    private String direccion;
    private String username;
    private String password;
    private String email;
    private String CUI;
    private Date fechaNacimiento;
    private Date fechaFundacion;
    private String curriculum;
    private int[] categorias;
}
