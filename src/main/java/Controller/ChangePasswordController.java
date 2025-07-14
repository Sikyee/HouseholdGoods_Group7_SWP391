package Controller;

import DAO.UserDAO;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate input fields
        if (currentPassword == null || newPassword == null || confirmPassword == null
                || currentPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            session.setAttribute("message", "All password fields are required.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("message", "New password and confirmation do not match.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // Check password length
        if (newPassword.length() < 6) {
            session.setAttribute("message", "New password must be at least 6 characters long.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // Check if new password is different from current
        if (newPassword.equals(currentPassword)) {
            session.setAttribute("message", "New password must be different from the current password.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // Check password strength (optional)
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")) {
            session.setAttribute("message", "Password must include uppercase, lowercase, number, and special character.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        UserDAO dao = new UserDAO();
        
        // Get current user from database to verify current password
        User actualUser = dao.getUserByUsername(sessionUser.getUserName());
        
        if (actualUser == null) {
            session.setAttribute("message", "User not found in database.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // Verify current password
        if (!actualUser.getPassword().equals(currentPassword)) {
            session.setAttribute("message", "Current password is incorrect.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // Change password
        boolean changed = dao.changePassword(actualUser.getUserID(), newPassword);

        if (changed) {
            // Update session user password to prevent inconsistency
            sessionUser.setPassword(newPassword);
            session.setAttribute("user", sessionUser);
            
            session.setAttribute("message", "Password changed successfully.");
            session.setAttribute("messageType", "success");
        } else {
            session.setAttribute("message", "Failed to change password. Please try again.");
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect("profile");
    }
}