package com.ipc2.proyectofinalservlet.model.Applicant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Blob;
import java.sql.SQLException;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPdf {
    private int codigo;
    private int codigoUsuario;
    private Blob pdf;

    @JsonIgnore
    public Blob getPdf() {
        return pdf;
    }

    // Agrega este método para obtener los bytes del Blob
    public byte[] getPdfBytes() {
        try {
            if (pdf != null) {
                return pdf.getBytes(1, (int) pdf.length());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los bytes del Blob", e);
        }
        return null;
    }
}
