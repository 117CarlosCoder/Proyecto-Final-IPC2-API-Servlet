package com.ipc2.proyectofinalservlet.model.Applicant;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OfertaCostos {
    private int codigo;
    private String Oferta;
    private String Empresa;
    private String estado;
    private int cantidad;
}
