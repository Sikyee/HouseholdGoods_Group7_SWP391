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

    UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        String action = request.getParameter("action");
        String userIDParam = request.getParameter("id");

        if (action != null && userIDParam != null) {
            int userID = Integer.parseInt(userIDParam);

            switch (action) {
                case "ban":
                    dao.setUserStatus(userID, false); // false = ban
                    break;
                case "unban":
                    dao.setUserStatus(userID, true);  // true = unban
                    break;
                case "delete":
                    dao.deleteUser(userID);
                    break;
            }

            response.sendRedirect("User?type=" + type); // reload page
            return;
        }

        if ("customer".equalsIgnoreCase(type)) {
            List<User> customers = dao.getUsersByRole(3); // Customer
            request.setAttribute("users", customers);
            request.getRequestDispatcher("manageCustomer.jsp").forward(request, response);
        } else if ("staff".equalsIgnoreCase(type)) {
            List<User> staff = dao.getUsersByRole(2); // Staff
            request.setAttribute("users", staff);
            request.getRequestDispatcher("manageStaff.jsp").forward(request, response);
        } else {
            response.sendRedirect("error.jsp");
        }
    }
}


