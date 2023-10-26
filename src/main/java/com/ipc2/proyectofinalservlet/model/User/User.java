package com.ipc2.proyectofinalservlet.model.User;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int codigo;
    private String nombre;
    private String direccion;
    private String username;
    private String password;
    private String email;
    private String CUI;
    private Date fechaFundacion;
    private Date fechaNacimiento;
    private String curriculum;
    private String rol;
    private String mision;
    private String vision;
}
