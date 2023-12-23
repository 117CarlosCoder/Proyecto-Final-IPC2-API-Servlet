package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.SesionDB;
import com.ipc2.proyectofinalservlet.data.UserDB;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import jakarta.servlet.http.HttpServletResponse;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class UserService  {
    private final UserDB usuarioDB;
    private SesionService sesionService;

    public UserService(Connection conexion){
        this.usuarioDB = new UserDB(conexion);
    }

    public boolean comprobarCorreo(String email) {
       User user = usuarioDB.comprobarCorreo(email);
        return user != null;
    }
    public void crearUsuarioSolicitante(User user, boolean valor) {
        System.out.println("Crer Usuario");
        if (valor){
            usuarioDB.crearUsuarioSolicitanteAdmin(user);
        }else {
            String contreseña = generarContraseña(8);
            usuarioDB.crearUsuarioSolicitante(user, contreseña);
            enviarConGMail(user.getEmail(), "EmpleoGt", "Hola Bienvenido a EmpleoGT esta es su contraseña : " + contreseña);
        }
    }

    public void crearTelefono(String telefono, User user) {
        usuarioDB.crearTelefonos( telefono, user);
    }

    public void eliminarUsuario(String username){
        System.out.println("Eliminar Usuario");
        usuarioDB.eliminarTelefonos(username);
        usuarioDB.eliminarUsuario(username);
    }


    public void crearUsuarioEmpleador(User user) {
        System.out.println("Crer Usuario");
        boolean valor = false;
        if (user.getRol().equals("Empleador")){
            valor=true;
            if (valor){
                usuarioDB.crearUsuariOEmpleadorAdmin(user);
            }
        }
        if (user.getRol().equals("Solicitante")){
            valor=true;
            if (valor){
                usuarioDB.crearUsuarioSolicitanteAdmin(user);
            }
        }
        if(!valor) {
            String contreseña = generarContraseña(8);
            usuarioDB.crearUsuariOEmpleador(user, contreseña);
            enviarConGMail(user.getEmail(), "EmpleoGt", "Hola Bienvenido a EmpleoGT esta es su contraseña : " + contreseña);
        }
    }

    public void restablecerContrasena(String email){
        System.out.println("Restablecer Contrasena");
        String contreseña = generarContraseña(8);
        usuarioDB.restablecerContraseña(email,contreseña);
        enviarConGMail(email,"EmpleoGt","Restablecimiento de contraseña, la contraseña nueva es : " + contreseña);
    }
    public void enviarConGMail(String destinatario, String asunto, String cuerpo) {
        //La dirección de correo de envío
        String remitente = "empleogtatencion@gmail.com";
        //La clave de aplicación obtenida según se explica en este artículo:
        String claveemail = "bokvegadmgpqktcb";

        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
        props.put("mail.smtp.user", remitente);
        props.put("mail.smtp.clave", claveemail);    //La clave de la cuenta
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google

        Session session = Session.getDefaultInstance(props);
        System.out.println("envion por gmail");
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(remitente));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.setSubject(asunto);
            message.setText(cuerpo);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", remitente, claveemail);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("envio gmail");
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }

    public  String generarContraseña(int longitud) {
        char[] caracteres = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '+', '='};
        Random random = new Random();
        StringBuilder contraseña = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            contraseña.append(caracteres[random.nextInt(caracteres.length)]);
        }
        return contraseña.toString();
    }

    public User validarUsuario(Connection conexion,String username, String password, String email) {
        System.out.println("validar : ");
        sesionService = new SesionService(conexion);
        return sesionService.obtenerUsuario(username, password, email);
    }

    public String[] autorizacion(String authorizationHeader, HttpServletResponse resp) {
        String[] parts = {};
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            parts = credentials.split(":", 2);
            return  parts;
            //username = parts[0];
            //password = parts[1];
            //System.out.println("Username: " + username);
            //System.out.println("Password: " + password);
        }
        else {
            System.out.println("Usuario no aceptado");
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return parts;
        }

    }
}
