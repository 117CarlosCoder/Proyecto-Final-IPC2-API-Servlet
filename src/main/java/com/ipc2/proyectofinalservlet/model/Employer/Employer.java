package com.ipc2.proyectofinalservlet.model.Employer;

import com.ipc2.proyectofinalservlet.model.User.Telefono;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Employer {
    private int codigo;
    private String nombre;
    private String direccion;
    private String username;
    private String password;
    private String sal;
    private String email;
    private String CUI;
    private Date fechaFundacion;
    private Tarjeta tarjeta;
    private String[] telefonos;
    private String mision;
    private String vision;
}
