package com.ipc2.proyectofinalservlet.controller.UserController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Encriptador {
    public String encriptarContrasena(String password, String salt) throws NoSuchAlgorithmException {
        String passwordWithSalt = password + salt;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(passwordWithSalt.getBytes());

        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    public  boolean varificarContrasena(String enteredPassword, String storedPassword, String salt) throws NoSuchAlgorithmException {
        String enteredPasswordHashed = encriptarContrasena(enteredPassword, salt);
        return enteredPasswordHashed.equals(storedPassword);
    }

     public String generarSecuencia() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

}
