package Controller;

import DAO.AddressDAO;
import DAO.UserDAO;
import Model.Address;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private AddressDAO addressDAO = new AddressDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String addressIDStr = request.getParameter("addressID");

        try {
            if (action != null) {
                switch (action) {
                    case "edit":
                        handleEditAddress(request, sessionUser, addressIDStr);
                        break;
                    case "delete":
                        handleDeleteAddress(request, sessionUser, addressIDStr);
                        break;
                    case "set-default":
                        handleSetDefaultAddress(request, sessionUser, addressIDStr);
                        break;
                }
            }
            loadProfileData(request, sessionUser);

        } catch (Exception e) {
            request.setAttribute("message", "Error: " + e.getMessage());
            request.setAttribute("messageType", "error");
            loadProfileData(request, sessionUser);
        }

        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add-address".equals(action)) {
                handleAddAddress(request, sessionUser);
            } else if ("update-address".equals(action)) {
                handleUpdateAddress(request, sessionUser);
            } else {
                handleUpdateProfile(request, sessionUser);
            }

        } catch (Exception e) {
            request.setAttribute("message", "Error: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }

        loadProfileData(request, sessionUser);
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    private void handleEditAddress(HttpServletRequest request, User user, String addressIDStr) {
        if (addressIDStr != null) {
            try {
                int addressID = Integer.parseInt(addressIDStr);
                Address address = addressDAO.getAddressByID(addressID);

                if (address != null && address.getUserID() == user.getUserID()) {
                    request.setAttribute("editAddress", address);
                } else {
                    request.setAttribute("message", "Address not found.");
                    request.setAttribute("messageType", "error");
                }
            } catch (Exception e) {
                request.setAttribute("message", "Invalid address ID.");
                request.setAttribute("messageType", "error");
            }
        }
    }

    private void handleDeleteAddress(HttpServletRequest request, User user, String addressIDStr) {
        if (addressIDStr != null) {
            try {
                int addressID = Integer.parseInt(addressIDStr);
                Address address = addressDAO.getAddressByID(addressID);

                if (address != null && address.getUserID() == user.getUserID()) {
                    boolean deleted = addressDAO.deleteAddress(addressID);
                    if (deleted) {
                        request.setAttribute("message", "Address deleted successfully.");
                        request.setAttribute("messageType", "success");
                    } else {
                        request.setAttribute("message", "Failed to delete address.");
                        request.setAttribute("messageType", "error");
                    }
                } else {
                    request.setAttribute("message", "Address not found.");
                    request.setAttribute("messageType", "error");
                }
            } catch (Exception e) {
                request.setAttribute("message", "Error deleting address.");
                request.setAttribute("messageType", "error");
            }
        }
    }

    private void handleSetDefaultAddress(HttpServletRequest request, User user, String addressIDStr) {
        if (addressIDStr != null) {
            try {
                int addressID = Integer.parseInt(addressIDStr);
                Address address = addressDAO.getAddressByID(addressID);

                if (address != null && address.getUserID() == user.getUserID()) {
                    boolean updated = addressDAO.setDefaultAddress(user.getUserID(), addressID);
                    if (updated) {
                        request.setAttribute("message", "Default address updated.");
                        request.setAttribute("messageType", "success");
                    } else {
                        request.setAttribute("message", "Failed to update default address.");
                        request.setAttribute("messageType", "error");
                    }
                } else {
                    request.setAttribute("message", "Address not found.");
                    request.setAttribute("messageType", "error");
                }
            } catch (Exception e) {
                request.setAttribute("message", "Error updating default address.");
                request.setAttribute("messageType", "error");
            }
        }
    }

    private void handleAddAddress(HttpServletRequest request, User user) {
        String addressName = request.getParameter("addressName");
        String recipientName = request.getParameter("recipientName");
        String phone = request.getParameter("phone");
        String addressDetail = request.getParameter("addressDetail");
        boolean isDefault = "1".equals(request.getParameter("isDefault"));

        // Simple validation
        if (isEmpty(addressName) || isEmpty(recipientName) || isEmpty(phone) || isEmpty(addressDetail)) {
            request.setAttribute("message", "Please fill all required fields.");
            request.setAttribute("messageType", "error");
            return;
        }

        Address address = new Address();
        address.setUserID(user.getUserID());
        address.setAddressName(addressName.trim());
        address.setRecipientName(recipientName.trim());
        address.setPhone(phone.trim());
        address.setAddressDetail(addressDetail.trim());
        address.setDefault(isDefault);

        boolean added = addressDAO.addAddress(address);
        if (added) {
            request.setAttribute("message", "Address added successfully.");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Failed to add address.");
            request.setAttribute("messageType", "error");
        }
    }

    private void handleUpdateAddress(HttpServletRequest request, User user) {
        String addressIDStr = request.getParameter("addressID");
        
        if (isEmpty(addressIDStr)) {
            request.setAttribute("message", "Invalid address ID.");
            request.setAttribute("messageType", "error");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = addressDAO.getAddressByID(addressID);

            if (address == null || address.getUserID() != user.getUserID()) {
                request.setAttribute("message", "Address not found.");
                request.setAttribute("messageType", "error");
                return;
            }

            String addressName = request.getParameter("addressName");
            String recipientName = request.getParameter("recipientName");
            String phone = request.getParameter("phone");
            String addressDetail = request.getParameter("addressDetail");
            boolean isDefault = "1".equals(request.getParameter("isDefault"));

            // Simple validation
            if (isEmpty(addressName) || isEmpty(recipientName) || isEmpty(phone) || isEmpty(addressDetail)) {
                request.setAttribute("message", "Please fill all required fields.");
                request.setAttribute("messageType", "error");
                request.setAttribute("editAddress", address);
                return;
            }

            address.setAddressName(addressName.trim());
            address.setRecipientName(recipientName.trim());
            address.setPhone(phone.trim());
            address.setAddressDetail(addressDetail.trim());
            address.setDefault(isDefault);

            boolean updated = addressDAO.updateAddress(address);
            if (updated) {
                request.setAttribute("message", "Address updated successfully.");
                request.setAttribute("messageType", "success");
            } else {
                request.setAttribute("message", "Failed to update address.");
                request.setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid address ID.");
            request.setAttribute("messageType", "error");
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, User user) {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");

        // Simple validation
        if (isEmpty(fullName) || isEmpty(email) || isEmpty(phone)) {
            request.setAttribute("message", "Please fill all required fields.");
            request.setAttribute("messageType", "error");
            return;
        }

        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhone(phone.trim());
        user.setGender(gender != null ? gender.trim() : null);

        // Parse date of birth
        if (!isEmpty(dobStr)) {
            try {
                Date dob = Date.valueOf(dobStr);
                user.setDob(dob);
            } catch (IllegalArgumentException e) {
                request.setAttribute("message", "Invalid date format.");
                request.setAttribute("messageType", "error");
                return;
            }
        }

        boolean updated = userDAO.updateUser(user);
        if (updated) {
            request.getSession().setAttribute("user", user);
            request.setAttribute("message", "Profile updated successfully.");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Failed to update profile.");
            request.setAttribute("messageType", "error");
        }
    }

    private void loadProfileData(HttpServletRequest request, User user) {
        try {
            User freshUser = userDAO.getUserByID(user.getUserID());
            if (freshUser != null) {
                request.setAttribute("user", freshUser);
                request.getSession().setAttribute("user", freshUser);
            } else {
                request.setAttribute("user", user);
            }

            List<Address> addressList = addressDAO.getAddressesByUserID(user.getUserID());
            request.setAttribute("addressList", addressList);

        } catch (Exception e) {
            request.setAttribute("user", user);
            request.setAttribute("message", "Error loading data.");
            request.setAttribute("messageType", "error");
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}