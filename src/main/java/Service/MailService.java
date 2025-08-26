package Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Simplified MailService for development
 *
 * @author LoiDH
 */
public class MailService {

    private static final Logger logger = Logger.getLogger(MailService.class.getName());

    // Cấu hình email trực tiếp (có thể thay đổi theo nhu cầu)
    private static final String FROM_EMAIL = "danghuuloi2312@gmail.com";
    private static final String APP_PASSWORD = "uwnv muzf jlkg azxq";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    /**
     * Send verification code email with improved template
     *
     * @param toEmail Recipient email
     * @param code Verification code
     * @param fullName Recipient full name (optional)
     * @return true if sent successfully
     */
    public static boolean sendVerificationCode(String toEmail, String code, String fullName) {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Registration System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Account Verification Code");

            // Create plain text email content
            String textContent = createVerificationEmailTemplate(fullName != null ? fullName : "User", code);
            message.setContent(textContent, "text/plain; charset=utf-8");

            Transport.send(message);
            logger.info("Verification email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send verification email to: " + toEmail, e);
            return false;
        }
    }

    /**
     * Send verification code (backward compatibility)
     */
    public static boolean sendVerificationCode(String toEmail, String code) {
        return sendVerificationCode(toEmail, code, null);
    }

    /**
     * Send welcome email after successful registration
     *
     * @param toEmail User email
     * @param fullName User full name
     * @return true if sent successfully
     */
    public static boolean sendWelcomeEmail(String toEmail, String fullName) {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to our system!");

            String textContent = createWelcomeEmailTemplate(fullName);
            message.setContent(textContent, "text/plain; charset=utf-8");

            Transport.send(message);
            logger.info("Welcome email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send welcome email to: " + toEmail, e);
            return false;
        }
    }

    private static Properties createSMTPProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    private static Session createAuthenticatedSession(Properties props) {
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }

    private static String createVerificationEmailTemplate(String fullName, String code) {
        return "ACCOUNT VERIFICATION\n"
                + "==========================================\n\n"
                + "Hello " + fullName + ",\n\n"
                + "Thank you for registering an account with us. To complete your registration process, please use the verification code below:\n\n"
                + "YOUR VERIFICATION CODE: " + code + "\n\n"
                + "This code will expire in 5 minutes.\n\n"
                + "IMPORTANT SECURITY NOTICE:\n"
                + "For security reasons, please do not share this code with anyone. "
                + "If you did not request to register an account, please ignore this email.\n\n"
                + "If you have any questions, please contact our support team.\n\n"
                + "Best regards,\n"
                + "Support Team\n\n"
                + "==========================================\n"
                + "This email was sent automatically, please do not reply.";
    }

    private static String createWelcomeEmailTemplate(String fullName) {
        return "WELCOME!\n"
                + "==========================================\n\n"
                + "Hello " + fullName + "!\n\n"
                + "Your account has been successfully created!\n\n"
                + "We are excited to welcome you to our community. "
                + "You can now log in and explore all our amazing features.\n\n"
                + "If you have any questions, don't hesitate to contact us.\n\n"
                + "We hope you have a great experience!\n\n"
                + "Best regards,\n"
                + "Support Team\n\n"
                + "==========================================";
    }
}
