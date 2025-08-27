package Controller;

import DAO.UserDAO;
import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import DAO.UserDAO;
import Service.MailService;
import jakarta.servlet.RequestDispatcher;
import java.sql.Date;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        {
            String action = request.getParameter("action");
            String type = request.getParameter("type");

            if ("create".equals(action) && "staff".equals(type)) {
                try {
                    String fullName = request.getParameter("fullName");
                    String email = request.getParameter("email");
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    String phone = request.getParameter("phone");
                    String gender = request.getParameter("gender");
                    String dobStr = request.getParameter("dob");

                    java.sql.Date dob = null;
                    if (dobStr != null && !dobStr.isEmpty()) {
                        java.util.Date parsed = new SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
                        dob = new java.sql.Date(parsed.getTime());
                    }

                    User staff = new User();
                    staff.setFullName(fullName);
                    staff.setEmail(email);
                    staff.setUserName(username);
                    staff.setPassword(password);
                    staff.setPhone(phone);
                    staff.setRoleID(2); // Staff role = 2
                    staff.setStatus(1); // Active
                    staff.setGender(gender);
                    staff.setDob(dob);

                    // Insert vào DB
                    dao.insert(staff);

                    // ✅ Gửi email với username + password
                    boolean sent = MailService.sendAccountInfo(email, username, password);
                    if (sent) {
                        request.setAttribute("successMessage", "Staff account created successfully! Login info sent to email.");
                    } else {
                        request.setAttribute("errorMessage", "Staff created but failed to send email.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Failed to create staff account: " + e.getMessage());
                }

                RequestDispatcher rd = request.getRequestDispatcher("createStaff.jsp");
                rd.forward(request, response);
            }
        }
    }
}
