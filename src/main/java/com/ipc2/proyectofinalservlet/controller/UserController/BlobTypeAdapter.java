package com.ipc2.proyectofinalservlet.controller.UserController;

import com.google.gson.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

public class BlobTypeAdapter implements JsonDeserializer<Blob> {

    @Override
    public Blob deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            String base64String = json.getAsJsonPrimitive().getAsString();
            byte[] bytes = Base64.getDecoder().decode(base64String);
            try {
                // Aquí asumes que el Blob es un Blob de MySQL, puedes ajustarlo según tu base de datos específica.
                return new javax.sql.rowset.serial.SerialBlob(bytes);
            } catch (SQLException e) {
                throw new JsonParseException("Error al convertir la cadena Base64 a Blob", e);
            }
        } else {
            throw new JsonParseException("Formato incorrecto para Blob");
        }
    }

    public static BlobTypeAdapter create() {
        return new BlobTypeAdapter();
    }
}
