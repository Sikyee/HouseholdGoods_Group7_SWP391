package Controller;

import DAO.UserDAO;
import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/User")
public class UserController extends HttpServlet {

    private UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            int id = request.getParameter("id") != null ? Integer.parseInt(request.getParameter("id")) : -1;

            if ("ban".equals(action)) {
                dao.setUserStatus(id, false);
            } else if ("unban".equals(action)) {
                dao.setUserStatus(id, true);
            }

            List<User> users = dao.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
