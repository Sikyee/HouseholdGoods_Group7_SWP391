package Controller;

import DAO.AddressDAO;
import DAO.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import Model.Address;
import Model.User;

import java.io.IOException;

@WebServlet("/add-address")
public class AddressController extends HttpServlet {

    private static final String DEFAULT_USERNAME = "Sikyee";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("addAddress.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String addressDetail = request.getParameter("addressDetail");
        boolean isDefault = "on".equals(request.getParameter("isDefault"));

        User user = new UserDAO().getUserByUsername(DEFAULT_USERNAME);

        Address address = new Address();
        address.setCustomerID(user.getUserID());
        address.setAddressDetail(addressDetail);
        address.setDefault(isDefault);

        boolean success = new AddressDAO().addAddress(address);

        // Redirect to profile with accompanying message
        if (success) {
            response.sendRedirect("profile?message=Address+added+successfully!&messageType=success");
        } else {
            response.sendRedirect("profile?message=Failed+to+add+address.&messageType=error");
        }
    }
}
