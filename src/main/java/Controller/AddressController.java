package Controller;

import DAO.AddressDAO;
import Model.Address;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/manage-address")
public class AddressController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        AddressDAO dao = new AddressDAO();

        if ("edit".equals(action)) {
            try {
                int addressID = Integer.parseInt(request.getParameter("addressID"));
                Address address = dao.getAddressByID(addressID);

                if (address != null && address.getUserID() == user.getUserID()) {
                    request.setAttribute("editAddress", address);
                } else {
                    session.setAttribute("message", "Address not found or unauthorized.");
                    session.setAttribute("messageType", "error");
                    response.sendRedirect("profile");
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("message", "Invalid address ID.");
                session.setAttribute("messageType", "error");
                response.sendRedirect("profile");
                return;
            }

            // Chuyển đến form để update
            request.getRequestDispatcher("manageAddress.jsp").forward(request, response);
            return;

        } else if ("delete".equals(action)) {
            try {
                int addressID = Integer.parseInt(request.getParameter("addressID"));
                Address address = dao.getAddressByID(addressID);

                if (address != null && address.getUserID() == user.getUserID()) {
                    boolean deleted = dao.deleteAddress(addressID);
                    session.setAttribute("message", deleted ? "Address deleted successfully." : "Delete failed.");
                    session.setAttribute("messageType", deleted ? "success" : "error");
                } else {
                    session.setAttribute("message", "Address not found or unauthorized.");
                    session.setAttribute("messageType", "error");
                }

            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("message", "Invalid address ID.");
                session.setAttribute("messageType", "error");
            }

            response.sendRedirect("profile");
            return;
        }

        // Mặc định: hiển thị form thêm mới
        request.getRequestDispatcher("manageAddress.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String addressDetail = request.getParameter("addressDetail");
        boolean isDefault = request.getParameter("isDefault") != null;

        AddressDAO dao = new AddressDAO();

        String addressIDStr = request.getParameter("addressID"); // Nếu có là update

        if (addressIDStr != null && !addressIDStr.isEmpty()) {
            // Cập nhật
            try {
                int addressID = Integer.parseInt(addressIDStr);
                Address address = dao.getAddressByID(addressID);

                if (address != null && address.getUserID() == user.getUserID()) {
                    address.setAddressDetail(addressDetail);
                    address.setDefault(isDefault);
                    boolean updated = dao.updateAddress(address);

                    session.setAttribute("message", updated ? "Address updated successfully." : "Update failed.");
                    session.setAttribute("messageType", updated ? "success" : "error");
                } else {
                    session.setAttribute("message", "Unauthorized update attempt.");
                    session.setAttribute("messageType", "error");
                }

            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("message", "Invalid address ID.");
                session.setAttribute("messageType", "error");
            }

        } else {
            // Thêm mới
            Address address = new Address();
            address.setUserID(user.getUserID());
            address.setAddressDetail(addressDetail);
            address.setDefault(isDefault);

            boolean added = dao.addAddress(address);
            session.setAttribute("message", added ? "Address added successfully." : "Add failed.");
            session.setAttribute("messageType", added ? "success" : "error");
        }

        response.sendRedirect("profile");
    }
}
