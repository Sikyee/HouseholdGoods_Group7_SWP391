package Controller;

import DAO.UserDAO;
import Model.User;
import Service.MailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;

@WebServlet(name = "ForgotPasswordController", urlPatterns = {"/forgot-password"})
public class ForgotPasswordController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ForgotPasswordController.class.getName());
    private UserDAO userDAO = new UserDAO();

    // Code expiration time (5 minutes)
    private static final long CODE_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            // Show forgot password form
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
        } else if ("verify".equals(action)) {
            // Show verify code form
            request.getRequestDispatcher("verify-reset-code.jsp").forward(request, response);
        } else if ("reset".equals(action)) {
            // Show new password form (chỉ hiện khi đã verify code thành công)
            HttpSession session = request.getSession();
            if (session.getAttribute("codeVerified") == null
                    || !(Boolean) session.getAttribute("codeVerified")) {
                response.sendRedirect("forgot-password");
                return;
            }
            request.getRequestDispatcher("new-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || "request".equals(action)) {
            handleForgotPasswordRequest(request, response);
        } else if ("verify".equals(action)) {
            handleVerifyCode(request, response);
        } else if ("reset".equals(action)) {
            handleNewPasswordSubmit(request, response);
        }
    }

    /**
     * Handle forgot password request - send verification code
     */
    private void handleForgotPasswordRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        try {
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Email is required");
                request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
                return;
            }

            // Check if email exists in database
            User user = userDAO.getUserByEmail(email.trim());
            if (user == null) {
                request.setAttribute("error", "Email not found in our system");
                request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
                return;
            }

            // Check if user is active
            if (user.getStatus() != 1) {
                request.setAttribute("error", "Account is inactive. Please contact support.");
                request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
                return;
            }

            // Generate verification code
            String verificationCode = generateVerificationCode();

            // Send email
            boolean emailSent = MailService.sendPasswordResetCode(email, verificationCode, user.getFullName());

            if (emailSent) {
                // Store verification data in session
                HttpSession session = request.getSession();
                session.setAttribute("resetEmail", email);
                session.setAttribute("resetCode", verificationCode);
                session.setAttribute("resetCodeTime", System.currentTimeMillis());
                session.setAttribute("resetUserId", user.getUserID());
                session.removeAttribute("codeVerified"); // Reset verification status

                logger.info("Password reset code sent to: " + email);

                request.setAttribute("success", "Verification code sent to your email. Please check your inbox.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("verify-reset-code.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Failed to send verification email. Please try again later.");
                request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in handleForgotPasswordRequest", e);
            request.setAttribute("error", "System error. Please try again later.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
        }
    }

    /**
     * Handle verify code - redirect to new password form
     */
    private void handleVerifyCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputCode = request.getParameter("code");
        HttpSession session = request.getSession();

        try {
            // Validate input
            if (inputCode == null || inputCode.trim().isEmpty()) {
                request.setAttribute("error", "Verification code is required");
                request.setAttribute("email", session.getAttribute("resetEmail"));
                request.getRequestDispatcher("verify-reset-code.jsp").forward(request, response);
                return;
            }

            // Get stored data from session
            String storedCode = (String) session.getAttribute("resetCode");
            Long codeTime = (Long) session.getAttribute("resetCodeTime");
            String email = (String) session.getAttribute("resetEmail");
            Integer userId = (Integer) session.getAttribute("resetUserId");

            if (storedCode == null || codeTime == null || email == null || userId == null) {
                request.setAttribute("error", "Session expired. Please request a new code.");
                response.sendRedirect("forgot-password");
                return;
            }

            // Check code expiration
            if (System.currentTimeMillis() - codeTime > CODE_EXPIRATION_TIME) {
                session.removeAttribute("resetCode");
                session.removeAttribute("resetCodeTime");
                session.removeAttribute("codeVerified");
                request.setAttribute("error", "Verification code has expired. Please request a new one.");
                response.sendRedirect("forgot-password");
                return;
            }

            // Verify code
            if (!inputCode.trim().equals(storedCode)) {
                request.setAttribute("error", "Invalid verification code");
                request.setAttribute("email", email);
                request.getRequestDispatcher("verify-reset-code.jsp").forward(request, response);
                return;
            }

            // Code is valid - Mark as verified and redirect to new password form
            session.setAttribute("codeVerified", true);
            logger.info("Verification code confirmed for email: " + email);

            // Redirect to new password form
            response.sendRedirect("forgot-password?action=reset");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in handleVerifyCode", e);
            request.setAttribute("error", "System error. Please try again later.");
            request.getRequestDispatcher("verify-reset-code.jsp").forward(request, response);
        }
    }

    /**
     * Handle new password submission
     */
    private void handleNewPasswordSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        HttpSession session = request.getSession();

        try {
            // Check if user has verified the code
            if (session.getAttribute("codeVerified") == null
                    || !(Boolean) session.getAttribute("codeVerified")) {
                request.setAttribute("error", "Invalid session. Please restart the password reset process.");
                response.sendRedirect("forgot-password");
                return;
            }

            // Validate input
            if (newPassword == null || newPassword.trim().isEmpty()) {
                request.setAttribute("error", "New password is required");
                request.getRequestDispatcher("new-password.jsp").forward(request, response);
                return;
            }

            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                request.setAttribute("error", "Confirm password is required");
                request.getRequestDispatcher("new-password.jsp").forward(request, response);
                return;
            }

            // Check password match
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match");
                request.getRequestDispatcher("new-password.jsp").forward(request, response);
                return;
            }

            // Validate password strength
            String passwordValidation = validatePassword(newPassword);
            if (passwordValidation != null) {
                request.setAttribute("error", passwordValidation);
                request.getRequestDispatcher("new-password.jsp").forward(request, response);
                return;
            }

            // Get user data from session
            Integer userId = (Integer) session.getAttribute("resetUserId");
            String email = (String) session.getAttribute("resetEmail");

            if (userId == null || email == null) {
                request.setAttribute("error", "Session expired. Please restart the password reset process.");
                response.sendRedirect("forgot-password");
                return;
            }

            // Update password in database
            boolean updated = userDAO.updatePassword(userId, newPassword);

            if (updated) {
                // Get user details for email
                User user = userDAO.getUserByID(userId);

                // Send password reset confirmation email
                MailService.sendPasswordResetConfirmation(email, user.getFullName());

                // Clear all session data
                session.removeAttribute("resetEmail");
                session.removeAttribute("resetCode");
                session.removeAttribute("resetCodeTime");
                session.removeAttribute("resetUserId");
                session.removeAttribute("codeVerified");

                logger.info("Password successfully updated for user ID: " + userId);

                request.setAttribute("success", "Password reset successfully! You can now log in with your new password.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Failed to update password. Please try again.");
                request.getRequestDispatcher("new-password.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in handleNewPasswordSubmit", e);
            request.setAttribute("error", "System error. Please try again later.");
            request.getRequestDispatcher("new-password.jsp").forward(request, response);
        }
    }

    /**
     * Validate password strength (same as ChangePasswordController)
     */
    private String validatePassword(String password) {
        List<String> errors = validatePasswordStrength(password);

        if (!errors.isEmpty()) {
            return String.join("; ", errors);
        }

        return null; // Password is valid
    }

    private List<String> validatePasswordStrength(String password) {
        List<String> errors = new ArrayList<>();

        if (password.length() < 8) {
            errors.add("Password must be at least 8 characters long");
        }

        if (password.length() > 50) {
            errors.add("Password cannot exceed 50 characters");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least 1 lowercase letter");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least 1 uppercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            errors.add("Password must contain at least 1 number");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            errors.add("Password must contain at least 1 special character (@$!%*?&)");
        }

        if (password.contains(" ")) {
            errors.add("Password cannot contain spaces");
        }

        // Check for consecutive characters
        if (hasConsecutiveChars(password, 3)) {
            errors.add("Password cannot contain more than 2 consecutive identical characters");
        }

        // Check for sequential characters
        if (hasSequentialChars(password)) {
            errors.add("Password cannot contain sequential characters (abc, 123, ...)");
        }

        return errors;
    }

    private boolean hasConsecutiveChars(String password, int maxConsecutive) {
        char prev = 0;
        int count = 1;

        for (char c : password.toCharArray()) {
            if (c == prev) {
                count++;
                if (count >= maxConsecutive) {
                    return true;
                }
            } else {
                count = 1;
                prev = c;
            }
        }
        return false;
    }

    private boolean hasSequentialChars(String password) {
        String lowerPassword = password.toLowerCase();

        // Check for sequential alphabets
        for (int i = 0; i < lowerPassword.length() - 2; i++) {
            char c1 = lowerPassword.charAt(i);
            char c2 = lowerPassword.charAt(i + 1);
            char c3 = lowerPassword.charAt(i + 2);

            if ((c2 == c1 + 1) && (c3 == c2 + 1)) {
                return true;
            }
        }

        // Check for sequential numbers
        for (int i = 0; i < password.length() - 2; i++) {
            if (Character.isDigit(password.charAt(i))
                    && Character.isDigit(password.charAt(i + 1))
                    && Character.isDigit(password.charAt(i + 2))) {

                int n1 = Character.getNumericValue(password.charAt(i));
                int n2 = Character.getNumericValue(password.charAt(i + 1));
                int n3 = Character.getNumericValue(password.charAt(i + 2));

                if ((n2 == n1 + 1) && (n3 == n2 + 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Generate random 6-digit verification code
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // Generate 6-digit code
        return String.valueOf(code);
    }
}
