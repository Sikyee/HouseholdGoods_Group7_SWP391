package Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * MailService: gửi email xác thực, chào mừng, đặt lại mật khẩu, gửi mật khẩu mới,
 * xác nhận đổi mật khẩu, và gửi thông tin tài khoản nhân viên.
 *
 * @author LoiDH
 */
public class MailService {

    private static final Logger logger = Logger.getLogger(MailService.class.getName());

    // Cấu hình email (khuyến nghị đọc từ ENV trong production)
    private static final String FROM_EMAIL = "danghuuloi2312@gmail.com";
    private static final String APP_PASSWORD = "uwnv muzf jlkg azxq"; // TODO: đọc từ biến môi trường trong production
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    /**
     * Gửi mã xác thực đăng ký
     *
     * @param toEmail  Email người nhận
     * @param code     Mã xác thực
     * @param fullName Tên người nhận (tùy chọn)
     * @return true nếu gửi thành công
     */
    public static boolean sendVerificationCode(String toEmail, String code, String fullName) {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Registration System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Account Verification Code");

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
     * Backward compatibility
     */
    public static boolean sendVerificationCode(String toEmail, String code) {
        return sendVerificationCode(toEmail, code, null);
    }

    /**
     * Gửi email chào mừng sau khi đăng ký thành công
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

    /**
     * Gửi mã xác thực đặt lại mật khẩu
     */
    public static boolean sendPasswordResetCode(String toEmail, String code, String fullName) {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Password Reset System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Verification Code");

            String textContent = createPasswordResetEmailTemplate(fullName != null ? fullName : "User", code);
            message.setContent(textContent, "text/plain; charset=utf-8");

            Transport.send(message);
            logger.info("Password reset email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send password reset email to: " + toEmail, e);
            return false;
        }
    }

    /**
     * Gửi mật khẩu mới sau khi reset thành công
     */
    public static boolean sendNewPassword(String toEmail, String newPassword, String fullName) {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Password Reset System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your New Password");

            String textContent = createNewPasswordEmailTemplate(fullName != null ? fullName : "User", newPassword);
            message.setContent(textContent, "text/plain; charset=utf-8");

            Transport.send(message);
            logger.info("New password email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send new password email to: " + toEmail, e);
            return false;
        }
    }

    /**
     * Gửi email xác nhận đổi mật khẩu thành công
     */
    public static boolean sendPasswordResetConfirmation(String toEmail, String fullName) {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Security System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Successfully Changed");

            String textContent = createPasswordResetConfirmationTemplate(fullName != null ? fullName : "User");
            message.setContent(textContent, "text/plain; charset=utf-8");

            Transport.send(message);
            logger.info("Password reset confirmation sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send password reset confirmation to: " + toEmail, e);
            return false;
        }
    }

    /**
     * Gửi thông tin tài khoản nhân viên (username & password)
     */
    public static boolean sendAccountInfo(String toEmail, String username, String password) {
        try {
            Properties props = createSMTPProperties();
            Session session = createAuthenticatedSession(props);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Staff Management System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Staff Account Information");

            String textContent = createAccountInfoEmailTemplate(username, password);
            message.setContent(textContent, "text/plain; charset=utf-8");

            Transport.send(message);
            logger.info("Account info email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send account info email to: " + toEmail, e);
            return false;
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
