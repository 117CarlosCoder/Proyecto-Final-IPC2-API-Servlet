package com.ipc2.proyectofinalservlet.model.Admin;

import lombok.*;

import java.sql.Date;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private int codigo;
    private String nombre;
    private String direccion;
    private String username;
    private String password;
    private String email;
    private String CUI;
    private Date fechaNacimiento;
}
