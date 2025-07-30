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
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("message", "New password and confirmation do not match.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        // Check password length
        if (newPassword.length() < 6) {
            session.setAttribute("message", "New password must be at least 6 characters long.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        // Check if new password is different from current
        if (newPassword.equals(currentPassword)) {
            session.setAttribute("message", "New password must be different from the current password.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        // ✅ Check password strength (uppercase, lowercase, digit, ≥6 characters, no special char required)
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            session.setAttribute("message", "Password must contain at least one uppercase letter, one lowercase letter, and one digit.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        UserDAO dao = new UserDAO();

        try {
            User actualUser = dao.getUserByUsername(sessionUser.getUserName());

            if (actualUser == null) {
                session.setAttribute("message", "User not found in database.");
                session.setAttribute("messageType", "error");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            if (!actualUser.getPassword().equals(currentPassword)) {
                session.setAttribute("message", "Current password is incorrect.");
                session.setAttribute("messageType", "error");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            boolean changed = dao.changePassword(actualUser.getUserID(), newPassword);

            if (changed) {
                sessionUser.setPassword(newPassword);
                session.setAttribute("user", sessionUser);
                session.setAttribute("message", "Password changed successfully!");
                session.setAttribute("messageType", "success");
                System.out.println("Password changed successfully for user ID: " + actualUser.getUserID());
            } else {
                session.setAttribute("message", "Failed to change password. Please try again.");
                session.setAttribute("messageType", "error");
                System.out.println("Failed to change password for user ID: " + actualUser.getUserID());
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "An error occurred while changing password: " + e.getMessage());
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
