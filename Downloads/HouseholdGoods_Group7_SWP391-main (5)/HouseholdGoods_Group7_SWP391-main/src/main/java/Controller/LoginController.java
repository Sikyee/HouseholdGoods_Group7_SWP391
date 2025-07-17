/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author thong
 */
import DAO.UserDAO;
import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private UserDAO dao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User user = dao.login(username, password);

            if (user == null) {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            if (user.getStatus() != 1) {
                request.setAttribute("error", "Your account has been banned");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userID", user.getUserID());
            session.setMaxInactiveInterval(30 * 60); // 🕒 Timeout sau 30 phút (tính bằng giây)

            int role = user.getRoleID();
            if (role == 3) {
                response.sendRedirect("homePage.jsp");
            } else if (role == 1 || role == 2) {
                response.sendRedirect("dashboard.jsp");
            } else {
                request.setAttribute("error", "Role not supported");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
