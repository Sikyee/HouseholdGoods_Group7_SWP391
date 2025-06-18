package Controller;

import DAO.AddressDAO;
import DAO.UserDAO;
import Model.Address;
import Model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thông tin người dùng đã đăng nhập từ session
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            // Chưa đăng nhập
            response.sendRedirect("login.jsp");
            return;
        }

        // Kiểm tra quyền truy cập (roleID = 4 là Guest, không được phép truy cập)
        if (sessionUser.getRoleID() == 4) {
            request.setAttribute("message", "You do not have permission to access this page.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Lấy thông tin user từ DB
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(sessionUser.getUserName());

        if (user == null) {
            response.getWriter().println("User not found.");
            return;
        }

        // Lấy danh sách địa chỉ
        AddressDAO addressDAO = new AddressDAO();
        List<Address> addressList = addressDAO.getAddressesByUserID(user.getUserID());

        request.setAttribute("user", user);
        request.setAttribute("addressList", addressList);

        // Lấy message từ session nếu có
        String message = (String) session.getAttribute("message");
        String messageType = (String) session.getAttribute("messageType");

        if (message != null && messageType != null) {
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");

        User user = new User();
        user.setUserName(sessionUser.getUserName());
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setGender(gender);
        user.setUserID(sessionUser.getUserID());

        try {
            Date dob = dobStr != null && !dobStr.isEmpty() ? Date.valueOf(dobStr) : null;
            user.setDob(dob);
        } catch (IllegalArgumentException e) {
            session.setAttribute("message", "Invalid date format.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        UserDAO dao = new UserDAO();
        boolean updated = dao.updateUser(user);

        if (updated) {
            session.setAttribute("message", "Profile updated successfully.");
            session.setAttribute("messageType", "success");
        } else {
            session.setAttribute("message", "Update failed.");
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect("profile");
    }
}
