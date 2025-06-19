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
            } else if ("view".equals(action) || "update".equals(action)) {
                User user = dao.getUserByID(id);
                if (user == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                request.setAttribute("user", user);
                String target = "viewUser.jsp"; // default
                if ("update".equals(action)) {
                    target = "updateUser.jsp";
                }
                request.getRequestDispatcher(target).forward(request, response);
                return;
            }

            // Load lại danh sách người dùng
            List<User> users = dao.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("updateSubmit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("userID"));
                String name = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                String dobStr = request.getParameter("dob");
                String gender = request.getParameter("gender");

                User user = dao.getUserByID(id);
                user.setFullName(name);
                user.setEmail(email);
                user.setPhone(phone);
                user.setGender(gender);
                if (dobStr != null && !dobStr.isEmpty()) {
                    user.setDob(java.sql.Date.valueOf(dobStr));
                }

                dao.updateUser(user);
                response.sendRedirect("User");

            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
            }
        }
    }
}
