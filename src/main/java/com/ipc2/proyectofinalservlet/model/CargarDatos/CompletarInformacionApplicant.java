package com.ipc2.proyectofinalservlet.model.CargarDatos;

import lombok.*;

import java.util.List;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompletarInformacionApplicant {

    private String curriculum;
    private List<Integer> categorias;
}
