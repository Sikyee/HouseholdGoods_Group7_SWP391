package Controller;

import DAO.AddressDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;
import DAO.UserDAO;
import Model.Address;
import Model.User;
import java.util.List;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_USERNAME = "Sikyee"; // Mock login username

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    // Retrieve user information
    UserDAO userDAO = new UserDAO();
    User user = userDAO.getUserByUsername(DEFAULT_USERNAME);

    if (user == null) {
        response.getWriter().println("User not found: " + DEFAULT_USERNAME);
        return;
    }

    // Retrieve the list of addresses for the user
    AddressDAO addressDAO = new AddressDAO();
    List<Address> addressList = addressDAO.getAddressesByCustomerID(user.getUserID());

    // Set user and address list attributes for JSP
    request.setAttribute("user", user);
    request.setAttribute("addressList", addressList);

    // ✅ Retrieve message and messageType from session if available
    HttpSession session = request.getSession();
    String message = (String) session.getAttribute("message");
    String messageType = (String) session.getAttribute("messageType");

    if (message != null && messageType != null) {
        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);

        // ✅ Remove from session after displaying once
        session.removeAttribute("message");
        session.removeAttribute("messageType");
    }

    // Forward to profile.jsp
    RequestDispatcher dispatcher = request.getRequestDispatcher("profile.jsp");
    dispatcher.forward(request, response);
}


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");

        User user = new User();
        user.setUserName(DEFAULT_USERNAME);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setGender(gender);

        try {
            Date dob = dobStr != null && !dobStr.isEmpty() ? Date.valueOf(dobStr) : null;
            user.setDob(dob);
        } catch (IllegalArgumentException e) {
            // Truyền lỗi qua session
            request.getSession().setAttribute("message", "Invalid date format.");
            request.getSession().setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        UserDAO dao = new UserDAO();
        boolean updated = dao.updateUser(user);

        // ✅ Truyền thông báo qua session để đọc trong doGet
        if (updated) {
            request.getSession().setAttribute("message", "Profile updated successfully.");
            request.getSession().setAttribute("messageType", "success");
        } else {
            request.getSession().setAttribute("message", "Update failed.");
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect("profile");
    }

}
