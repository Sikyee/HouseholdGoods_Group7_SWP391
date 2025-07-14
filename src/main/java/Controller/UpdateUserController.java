package Controller;

import DAO.UserDAO;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/UpdateUser")
public class UpdateUserController extends HttpServlet {

    private UserDAO dao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("userID"));
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String dob = request.getParameter("dob");
            String gender = request.getParameter("gender");

            User user = dao.getUserByID(id);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setGender(gender);
            user.setDob(dob != null && !dob.isEmpty() ? java.sql.Date.valueOf(dob) : null);

            dao.updateUser(user);

            response.sendRedirect("User");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Update failed.");
        }
    }
}
