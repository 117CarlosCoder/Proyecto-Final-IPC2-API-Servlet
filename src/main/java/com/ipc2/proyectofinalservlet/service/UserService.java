package com.ipc2.proyectofinalservlet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ipc2.proyectofinalservlet.data.UserDB;
import com.ipc2.proyectofinalservlet.model.Applicant.ActualizarContrasena;
import com.ipc2.proyectofinalservlet.model.CargarDatos.Ofertas;
import com.ipc2.proyectofinalservlet.model.User.Notificaciones;
import com.ipc2.proyectofinalservlet.model.User.Telefono;
import com.ipc2.proyectofinalservlet.model.User.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.Connection;
import java.util.Base64;
import java.util.List;
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

    public void crearUsuarioSolicitante(User user, boolean valor, HttpServletResponse resp) {
        System.out.println("Crer Usuario");

        if (valor){
            usuarioDB.crearUsuarioSolicitanteAdmin(user);
        }else {
            String contresena = generarContrasena(8);
            usuarioDB.crearUsuarioSolicitante(user, contresena, resp);
            enviarConGMail(user.getEmail(), "EmpleoGt", "Hola Bienvenido a EmpleoGT esta es su contraseña : " + contresena);
        }
    }

    public void crearTelefono(Telefono telefono, String user) {
        if ((telefono.getTelefono1() != 0)){
            usuarioDB.crearTelefonos( telefono.getTelefono1(), user);
        }
        if ((telefono.getTelefono2() != 0)){
            usuarioDB.crearTelefonos( telefono.getTelefono2(), user);
        }
        if ((telefono.getTelefono3() != 0)){
            usuarioDB.crearTelefonos( telefono.getTelefono3(), user);
        }

    }

    public void eliminarUsuario(String username){
        System.out.println("Eliminar Usuario");
        usuarioDB.eliminarTelefonos(username);
        usuarioDB.eliminarUsuario(username);
    }


    public boolean crearUsuarioEmpleador(User user) {
        System.out.println("Craer Usuario");
        if (user.getUsername().isEmpty()  || user.getPassword().isEmpty() || user.getNombre().isEmpty() || user.getCUI().isEmpty() || user.getCUI() == "0" || user.getEmail().isEmpty() || user.getDireccion().isEmpty()) return false;
        boolean valor = false;
        if (user.getRol().equals("Empleador")){
            valor=true;
            usuarioDB.crearUsuariOEmpleadorAdmin(user);
        }
        if (user.getRol().equals("Solicitante")){
            valor=true;
            usuarioDB.crearUsuarioSolicitanteAdmin(user);
        }
        if(!valor) {
            String contrasena = generarContrasena(8);
            usuarioDB.crearUsuariOEmpleador(user, contrasena);
            enviarConGMail(user.getEmail(), "EmpleoGt", "Hola Bienvenido a EmpleoGT esta es su contraseña : " + contrasena);
        }
        return true;
    }

    public void restablecerContrasena(String email){
        System.out.println("Restablecer Contrasena");
        String contresena = generarContrasena(8);
        usuarioDB.restablecerContrasena(email,contresena);
        enviarConGMail(email,"EmpleoGt","Restablecimiento de contraseña, la contraseña nueva es : " + contresena);
    }
    public void enviarConGMail(String destinatario, String asunto, String cuerpo) {
        //La dirección de correo de envío
        String remitente = "empleogtatencion@gmail.com";
        //La clave de aplicación obtenida según se explica en este artículo:
        String claveemail = "bokvegadmgpqktcb";

        Properties props = getProperties(remitente, claveemail);

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
            System.out.println("Error emvio de email " + me);
        }
    }

    private static Properties getProperties(String remitente, String claveemail) {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
        props.put("mail.smtp.user", remitente);
        props.put("mail.smtp.clave", claveemail);    //La clave de la cuenta
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google
        return props;
    }

    public  String generarContrasena(int longitud) {
        char[] caracteres = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '+', '='};
        Random random = new Random();
        StringBuilder contrasena = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            contrasena.append(caracteres[random.nextInt(caracteres.length)]);
        }
        return contrasena.toString();
    }

    public User validarUsuario(Connection conexion,String username, String password, String email) {
        System.out.println("validar : ");
        SesionService sesionService = new SesionService(conexion);
        return sesionService.obtenerUsuario(username, password, email);
    }

    public String[] autorizacion(String authorizationHeader, HttpServletResponse resp) {
        String[] parts = {};
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            parts = credentials.split(":", 2);
        }
        else {
            System.out.println("Usuario no aceptado");
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
        return  parts;

    }

    public Object leerJson(HttpServletResponse resp, HttpServletRequest req , Class clase)  {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Object valor = null ;
        try {
            valor = objectMapper.readValue(req.getInputStream(), clase);
        } catch (IOException e) {
           return null;
        }
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        return valor;
    }

    public void enviarJson(HttpServletResponse resp, Object valor) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.writeValue(resp.getWriter(), valor);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    }

    public List<Notificaciones> listarNotificaciones(int usuario){
        System.out.println("listar notificaciones");
        return usuarioDB.listarNotificaciones(usuario);
    }

    public boolean cambiarContrasena(ActualizarContrasena actualizarContrasena){
        System.out.println("cambiar cotrasena");
        if (actualizarContrasena == null || actualizarContrasena.getContrasena().isEmpty())return false;
        usuarioDB.cambiarContrasena(actualizarContrasena);
        return true;
    }
}
