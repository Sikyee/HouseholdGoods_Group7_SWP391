package Controller;

import DAO.ChangePasswordDAO;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/changePassword")
public class ChangePasswordController extends HttpServlet {

    private ChangePasswordDAO changePasswordDAO = new ChangePasswordDAO();

    // Password validation patterns
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Forward to change password page
        request.getRequestDispatcher("changePassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // Validate input
            List<String> errors = validatePasswordChange(sessionUser, currentPassword, newPassword, confirmPassword);

            if (!errors.isEmpty()) {
                request.setAttribute("message", String.join("<br>", errors));
                request.setAttribute("messageType", "error");
                preserveFormData(request, currentPassword, newPassword, confirmPassword);
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                return;
            }

            // Update password in database (no hashing)
            boolean passwordChanged = changePasswordDAO.changePassword(sessionUser.getUserID(), newPassword);

            if (passwordChanged) {
                // Update session user object
                sessionUser.setPassword(newPassword);
                session.setAttribute("user", sessionUser);

                // Log successful password change (without IP)
                changePasswordDAO.logPasswordChangeActivity(sessionUser.getUserID(), true);

                request.setAttribute("message", "Password changed successfully!");
                request.setAttribute("messageType", "success");

                System.out.println("Password changed successfully for user ID: " + sessionUser.getUserID()
                        + " (" + sessionUser.getUserName() + ") at " + new java.util.Date());

            } else {
                // Log failed attempt (without IP)
                changePasswordDAO.logPasswordChangeActivity(sessionUser.getUserID(), false);

                request.setAttribute("message", "Password change failed. Please try again.");
                request.setAttribute("messageType", "error");
            }

        } catch (Exception e) {
            System.err.println("Error in change password: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("message", "System error. Please try again later.");
            request.setAttribute("messageType", "error");
        }

        request.getRequestDispatcher("changePassword.jsp").forward(request, response);
    }

    private List<String> validatePasswordChange(User user, String currentPassword, String newPassword, String confirmPassword) {
        List<String> errors = new ArrayList<>();

        // 1. Validate current password
        if (isEmpty(currentPassword)) {
            errors.add("Current password is required.");
        } else if (!changePasswordDAO.verifyCurrentPassword(user.getUserID(), currentPassword)) {
            errors.add("Current password is incorrect.");
        }

        // 2. Validate new password
        if (isEmpty(newPassword)) {
            errors.add("New password is required.");
        } else {
            // Check password strength
            List<String> strengthErrors = validatePasswordStrength(newPassword);
            errors.addAll(strengthErrors);

            // Check if password contains personal info
            if (changePasswordDAO.containsPersonalInfo(user.getUserID(), newPassword)) {
                errors.add("Password cannot contain personal information (name, username, email).");
            }

            // Check if it's a common password
            if (changePasswordDAO.isCommonPassword(newPassword)) {
                errors.add("This password is too common. Please choose a different password.");
            }
        }

        // 3. Validate confirm password
        if (isEmpty(confirmPassword)) {
            errors.add("Password confirmation is required.");
        } else if (!newPassword.equals(confirmPassword)) {
            errors.add("Password confirmation does not match.");
        }

        // 4. Check if new password is same as current
        if (!isEmpty(newPassword) && newPassword.equals(user.getPassword())) {
            errors.add("New password must be different from current password.");
        }

        // 5. Check if password exists for other users
        if (!isEmpty(newPassword) && changePasswordDAO.isPasswordExistsForOtherUsers(user.getUserID(), newPassword)) {
            errors.add("This password is already used by another user. Please choose a different password.");
        }

        // 6. Check account status
        if (!changePasswordDAO.isAccountActive(user.getUserID())) {
            errors.add("Your account has been locked or disabled.");
        }

        return errors;
    }

    private List<String> validatePasswordStrength(String password) {
        List<String> errors = new ArrayList<>();

        if (password.length() < 8) {
            errors.add("Password must be at least 8 characters long.");
        }

        if (password.length() > 50) {
            errors.add("Password cannot exceed 50 characters.");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least 1 lowercase letter.");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least 1 uppercase letter.");
        }

        if (!password.matches(".*\\d.*")) {
            errors.add("Password must contain at least 1 number.");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            errors.add("Password must contain at least 1 special character (@$!%*?&).");
        }

        if (password.contains(" ")) {
            errors.add("Password cannot contain spaces.");
        }

        // Check for consecutive characters
        if (hasConsecutiveChars(password, 3)) {
            errors.add("Password cannot contain more than 2 consecutive identical characters.");
        }

        // Check for sequential characters
        if (hasSequentialChars(password)) {
            errors.add("Password cannot contain sequential characters (abc, 123, ...).");
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

    private void preserveFormData(HttpServletRequest request, String currentPassword, String newPassword, String confirmPassword) {
        // Don't preserve passwords for security reasons - just clear the form
        request.setAttribute("clearForm", true);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
