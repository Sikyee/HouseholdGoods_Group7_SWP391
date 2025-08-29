package Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Comprehensive MailService with all email functionalities
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
     * Test email service connection before sending emails
     *
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            // Test connection by creating transport and connecting
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, FROM_EMAIL, APP_PASSWORD);
            transport.close();

            logger.info("Email service connection test successful");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Email service connection test failed", e);
            return false;
        }
    }

    /**
     * Send verification code email with improved template
     *
     * @param toEmail Recipient email
     * @param code Verification code
     * @param fullName Recipient full name (optional)
     * @return true if sent successfully
     */
    public static boolean sendVerificationCode(String toEmail, String code, String fullName) {
        Transport transport = null;
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

            // Use explicit transport with connection management
            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, FROM_EMAIL, APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());

            logger.info("Verification email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "MessagingException when sending verification email to: " + toEmail + " - " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send verification email to: " + toEmail, e);
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.log(Level.WARNING, "Failed to close transport", e);
                }
            }
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
        Transport transport = null;
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to our system!");

            String textContent = createWelcomeEmailTemplate(fullName);
            message.setContent(textContent, "text/plain; charset=utf-8");

            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, FROM_EMAIL, APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());

            logger.info("Welcome email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "MessagingException when sending welcome email to: " + toEmail + " - " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send welcome email to: " + toEmail, e);
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.log(Level.WARNING, "Failed to close transport", e);
                }
            }
        }
    }

    /**
     * Send password reset verification code
     *
     * @param toEmail User email
     * @param code Reset verification code
     * @param fullName User full name
     * @return true if sent successfully
     */
    public static boolean sendPasswordResetCode(String toEmail, String code, String fullName) {
        Transport transport = null;
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Password Reset System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Verification Code");

            String textContent = createPasswordResetEmailTemplate(fullName != null ? fullName : "User", code);
            message.setContent(textContent, "text/plain; charset=utf-8");

            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, FROM_EMAIL, APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());

            logger.info("Password reset email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "MessagingException when sending password reset email to: " + toEmail + " - " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send password reset email to: " + toEmail, e);
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.log(Level.WARNING, "Failed to close transport", e);
                }
            }
        }
    }

    /**
     * Send new password to user after successful reset
     *
     * @param toEmail User email
     * @param newPassword New generated password
     * @param fullName User full name
     * @return true if sent successfully
     */
    public static boolean sendNewPassword(String toEmail, String newPassword, String fullName) {
        Transport transport = null;
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Password Reset System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your New Password");

            String textContent = createNewPasswordEmailTemplate(fullName != null ? fullName : "User", newPassword);
            message.setContent(textContent, "text/plain; charset=utf-8");

            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, FROM_EMAIL, APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());

            logger.info("New password email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "MessagingException when sending new password email to: " + toEmail + " - " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send new password email to: " + toEmail, e);
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.log(Level.WARNING, "Failed to close transport", e);
                }
            }
        }
    }

    /**
     * Send password reset confirmation email
     *
     * @param toEmail User email
     * @param fullName User full name
     * @return true if sent successfully
     */
    public static boolean sendPasswordResetConfirmation(String toEmail, String fullName) {
        Transport transport = null;
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Security System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Successfully Changed");

            String textContent = createPasswordResetConfirmationTemplate(fullName);
            message.setContent(textContent, "text/plain; charset=utf-8");

            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, FROM_EMAIL, APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());

            logger.info("Password reset confirmation sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "MessagingException when sending password reset confirmation to: " + toEmail + " - " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send password reset confirmation to: " + toEmail, e);
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.log(Level.WARNING, "Failed to close transport", e);
                }
            }
        }
    }

    /**
     * Send staff account information (username & password)
     *
     * @param toEmail Staff email
     * @param username Staff username
     * @param password Staff password
     * @return true if sent successfully
     */
    public static boolean sendAccountInfo(String toEmail, String username, String password) {
        Transport transport = null;
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Staff Management System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Staff Account Information");

            String textContent = createAccountInfoEmailTemplate(username, password);
            message.setContent(textContent, "text/plain; charset=utf-8");

            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, FROM_EMAIL, APP_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());

            logger.info("Account info email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "MessagingException when sending account info email to: " + toEmail + " - " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send account info email to: " + toEmail, e);
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.log(Level.WARNING, "Failed to close transport", e);
                }
            }
        }
    }

    // ====================== PRIVATE HELPER METHODS ======================
    private static Properties createSMTPProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Additional connection properties for better reliability
        props.put("mail.smtp.connectiontimeout", "10000"); // 10 seconds
        props.put("mail.smtp.timeout", "10000"); // 10 seconds
        props.put("mail.smtp.writetimeout", "10000"); // 10 seconds

        // Debug mode - set to true if you want detailed SMTP logs
        props.put("mail.debug", "false");

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

    private static String createPasswordResetEmailTemplate(String fullName, String code) {
        return "PASSWORD RESET REQUEST\n"
                + "==========================================\n\n"
                + "Hello " + fullName + ",\n\n"
                + "We received a request to reset your account password. To proceed with the password reset, please use the verification code below:\n\n"
                + "YOUR RESET VERIFICATION CODE: " + code + "\n\n"
                + "This code will expire in 5 minutes.\n\n"
                + "SECURITY IMPORTANT:\n"
                + "- If you did not request this password reset, please ignore this email and your password will remain unchanged.\n"
                + "- For security reasons, never share this code with anyone.\n"
                + "- If you continue to receive these emails without requesting them, please contact our support team immediately.\n\n"
                + "After entering the verification code, you will be able to set a new password for your account.\n\n"
                + "If you have any questions or need assistance, please contact our support team.\n\n"
                + "Best regards,\n"
                + "Security Team\n\n"
                + "==========================================\n"
                + "This email was sent automatically, please do not reply.";
    }

    private static String createNewPasswordEmailTemplate(String fullName, String newPassword) {
        return "YOUR NEW PASSWORD\n"
                + "==========================================\n\n"
                + "Hello " + fullName + ",\n\n"
                + "Your password has been successfully reset. Here is your new password:\n\n"
                + "NEW PASSWORD: " + newPassword + "\n\n"
                + "IMPORTANT SECURITY INSTRUCTIONS:\n"
                + "1. Please log in immediately using this new password\n"
                + "2. We strongly recommend changing this password to something more memorable after logging in\n"
                + "3. Keep this password secure and do not share it with anyone\n"
                + "4. Delete this email after you have successfully logged in and changed your password\n\n"
                + "Password Change Details:\n"
                + "- Time: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n"
                + "- Action: Automatic Password Reset\n\n"
                + "SECURITY NOTICE:\n"
                + "If you did not request this password reset, please contact our support team immediately as your account may be compromised.\n\n"
                + "For your account security:\n"
                + "- Change this temporary password after logging in\n"
                + "- Use a strong, unique password\n"
                + "- Log out from public computers\n"
                + "- Enable two-factor authentication if available\n\n"
                + "If you have any concerns about your account security, please contact our support team.\n\n"
                + "Best regards,\n"
                + "Security Team\n\n"
                + "==========================================\n"
                + "This email was sent automatically, please do not reply.";
    }

    private static String createPasswordResetConfirmationTemplate(String fullName) {
        return "PASSWORD SUCCESSFULLY CHANGED\n"
                + "==========================================\n\n"
                + "Hello " + fullName + ",\n\n"
                + "This email confirms that your account password has been successfully changed.\n\n"
                + "Change Details:\n"
                + "- Time: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n"
                + "- Action: Password Reset Completed\n\n"
                + "SECURITY NOTICE:\n"
                + "If you did not make this change, please contact our support team immediately as your account may be compromised.\n\n"
                + "For your account security:\n"
                + "- Use a strong, unique password\n"
                + "- Do not share your password with anyone\n"
                + "- Log out from public computers\n"
                + "- Enable two-factor authentication if available\n\n"
                + "If you have any concerns about your account security, please contact our support team.\n\n"
                + "Best regards,\n"
                + "Security Team\n\n"
                + "==========================================";
    }

    private static String createAccountInfoEmailTemplate(String username, String password) {
        return "STAFF ACCOUNT INFORMATION\n"
                + "==========================================\n\n"
                + "Hello,\n\n"
                + "Your staff account has been created successfully.\n\n"
                + "LOGIN CREDENTIALS:\n"
                + "- Username: " + username + "\n"
                + "- Password: " + password + "\n\n"
                + "IMPORTANT SECURITY INSTRUCTIONS:\n"
                + "1. Please log in immediately using these credentials\n"
                + "2. We strongly recommend changing your password after first login\n"
                + "3. Keep your credentials secure and do not share them with anyone\n"
                + "4. Delete this email after you have successfully logged in\n\n"
                + "Account Creation Details:\n"
                + "- Time: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n"
                + "- Role: Staff Member\n"
                + "- Status: Active\n\n"
                + "SECURITY NOTICE:\n"
                + "If you did not expect to receive this account information, please contact our support team immediately.\n\n"
                + "For your account security:\n"
                + "- Change your password after first login\n"
                + "- Use a strong, unique password\n"
                + "- Log out from public computers\n"
                + "- Never share your login credentials\n\n"
                + "If you have any questions about your account or need assistance, please contact our support team.\n\n"
                + "Best regards,\n"
                + "Staff Management Team\n\n"
                + "==========================================\n"
                + "This email was sent automatically, please do not reply.";
    }
}
