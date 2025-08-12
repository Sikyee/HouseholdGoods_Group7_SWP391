package Controller;

import DAO.UserDAO;
import Model.User;
import Service.MailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author LoiDH
 */
@WebServlet("/register")
public class RegisterController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    // Handle GET requests (e.g., when user visits /register directly)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to register.jsp to show the registration form
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    // Handle POST requests (e.g., form submission)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            processRegister(request, response);
        } else if ("verify".equals(action)) {
            processVerification(request, response);
        }
    }

    private void processRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Check for duplicate email
        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("error", "Email is already in use.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Check for duplicate username
        if (userDAO.getUserByUsername(username) != null) {
            request.setAttribute("error", "Username is already taken.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Generate 6-digit verification code
        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);

        // Store verification code and user temporarily in session
        HttpSession session = request.getSession();
        session.setAttribute("verificationCode", verificationCode);

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setUserName(username);
        user.setPassword(password);
        user.setRoleID(3); // Customer
        user.setStatus(1); // Active

        session.setAttribute("tempUser", user);

        // Send verification email
        if (MailService.sendVerificationCode(email, verificationCode)) {
            response.sendRedirect("verify.jsp"); // redirect allowed here for verification step
        } else {
            request.setAttribute("error", "We couldn't send the verification email. Please try again later.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    private void processVerification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputCode = request.getParameter("code");
        HttpSession session = request.getSession();
        String realCode = (String) session.getAttribute("verificationCode");

        if (inputCode != null && inputCode.equals(realCode)) {
            User user = (User) session.getAttribute("tempUser");
            try {
                userDAO.insert(user);
                session.removeAttribute("tempUser");
                session.removeAttribute("verificationCode");

                // Registration complete, redirect to login
                session.setAttribute("success", "Registration successful. Please log in.");
                response.sendRedirect("login.jsp");

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Invalid verification code.");
            request.getRequestDispatcher("verify.jsp").forward(request, response);
        }
    }
}
