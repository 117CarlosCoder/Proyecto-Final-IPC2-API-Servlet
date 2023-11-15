package com.ipc2.proyectofinalservlet.model.Applicant;

import lombok.*;

import java.sql.Date;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Applicant {
    private int codigo;
    private String nombre;
    private String direccion;
    private String username;
    private String password;
    private String email;
    private String CUI;
    private Date fechaNacimiento;
    private String curriculum;
    private int[] categorias;
}
