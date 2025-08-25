/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 *
 * @author loiDH
 */

public class MailService {

    public static boolean sendVerificationCode(String toEmail, String code) {
        final String fromEmail = "danghuuloi2312@gmail.com";       
        final String appPassword = "uwnv muzf jlkg azxq";        // Gmail App Password (not login password)

        // SMTP configuration for Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // TLS port

        // Create authenticated session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject("Email Verification Code");
            message.setText("Hello,\n\nYour verification code is: " + code + "\n\nRegards,\nSupport Team");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean sendAccountInfo(String toEmail, String username, String password) {
    final String fromEmail = "danghuuloi2312@gmail.com";
    final String appPassword = "uwnv muzf jlkg azxq"; // Gmail App Password

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(fromEmail, appPassword);
        }
    });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Your Staff Account Information");
        message.setText(
            "Hello,\n\n" +
            "Your staff account has been created successfully.\n\n" +
            "- Username: " + username + "\n" +
            "- Password: " + password + "\n\n" +
            "Please keep this information safe.\n\n" +
            "Regards,\nSupport Team"
        );

        Transport.send(message);
        return true;
    } catch (MessagingException e) {
        e.printStackTrace();
        return false;
    }
}
}
