package com.ipc2.proyectofinalservlet.model.Applicant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPdfJson {
    private int codigo;
    private int codigoUsuario;
    private String pdf;
    private Blob pdfBlob;

    @JsonIgnore
    public Blob getPdfBlob() {
        if (pdf != null && !pdf.isEmpty()) {
            // Convierte la cadena Base64 a bytes
            byte[] pdfBytes = Base64.getDecoder().decode(pdf);

            try {
                // Crea un nuevo Blob con los bytes
                return new javax.sql.rowset.serial.SerialBlob(pdfBytes);
            } catch (SQLException e) {
                throw new RuntimeException("Error al crear Blob a partir de la cadena Base64", e);
            }
        }
        return null;
    }
}
