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

        // Set attributes for JSP
        request.setAttribute("user", user);
        request.setAttribute("addressList", addressList);

        // Retrieve optional message from query parameters (e.g. after redirect from add-address)
        String message = request.getParameter("message");
        String messageType = request.getParameter("messageType");
        if (message != null && messageType != null) {
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
        }

        // Forward to profile.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("profile.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve updated profile data from form
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");

        // Build user object
        User user = new User();
        user.setUserName(DEFAULT_USERNAME); // Always update mock user
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setGender(gender);

        try {
            // Parse date of birth
            Date dob = dobStr != null && !dobStr.isEmpty() ? Date.valueOf(dobStr) : null;
            user.setDob(dob);
        } catch (IllegalArgumentException e) {
            // Handle invalid date format
            request.setAttribute("error", "Invalid date format.");
            doGet(request, response);
            return;
        }

        // Update user in database
        UserDAO dao = new UserDAO();
        boolean updated = dao.updateUser(user);

        if (updated) {
            request.setAttribute("success", "Profile updated successfully.");
        } else {
            request.setAttribute("error", "Update failed.");
        }

        // Retrieve address list again after update
        AddressDAO addressDAO = new AddressDAO();
        List<Address> addressList = addressDAO.getAddressesByCustomerID(user.getUserID());
        request.setAttribute("addressList", addressList);

        // Forward to profile page
        doGet(request, response);
    }
}
