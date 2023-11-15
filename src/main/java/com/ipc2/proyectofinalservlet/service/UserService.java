package com.ipc2.proyectofinalservlet.service;

import com.ipc2.proyectofinalservlet.data.SesionDB;
import com.ipc2.proyectofinalservlet.data.UserDB;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class UserService  {
    private final UserDB usuarioDB;

    public UserService(Connection conexion){
        this.usuarioDB = new UserDB(conexion);
    }

    public boolean comprobarCorreo(String email) {
       User user = usuarioDB.comprobarCorreo(email);
        return user != null;
    }
    public void crearUsuarioSolicitante(User user) {
        System.out.println("Crer Usuario");
        String contreseña = generarContraseña(8);
        usuarioDB.crearUsuarioSolicitante(user,contreseña);
        enviarConGMail(user.getEmail(),"EmpleoGt","Hola Bienvenido a EmpleoGT esta es su contraseña : " + contreseña);
    }

    public void crearTelefono(int usuario, Telefono telefono) {
        usuarioDB.crearTelefonos(usuario, telefono);
    }

    public void crearUsuarioEmpleador(User user) {
        System.out.println("Crer Usuario");
        String contreseña = generarContraseña(8);
        usuarioDB.crearUsuariOEmpleador(user,contreseña);
        enviarConGMail(user.getEmail(),"EmpleoGt","Hola Bienvenido a EmpleoGT esta es su contraseña : " + contreseña);
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
}
