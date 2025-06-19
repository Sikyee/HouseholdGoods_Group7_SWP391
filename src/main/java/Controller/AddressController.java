package Controller;

import DAO.AddressDAO;
import Model.Address;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/manage-address")
public class AddressController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int MAX_ADDRESSES_PER_USER = 10; // Limit addresses per user

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

        try {
            if ("edit".equals(action)) {
                handleEditAction(request, response, session, user, dao);
                return;
            } else if ("delete".equals(action)) {
                handleDeleteAction(request, response, session, user, dao);
                return;
            } else if ("set-default".equals(action)) {
                handleSetDefaultAction(request, response, session, user, dao);
                return;
            }

            // Default: show add new address form
            request.getRequestDispatcher("manageAddress.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "An unexpected error occurred. Please try again.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
        }
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

        try {
            // Get form parameters
            String addressIDStr = request.getParameter("addressID");
            String addressDetail = request.getParameter("addressDetail");
            String addressName = request.getParameter("addressName");
            String recipientName = request.getParameter("recipientName");
            String phone = request.getParameter("phone");
            String province = request.getParameter("province");
            String district = request.getParameter("district");
            String ward = request.getParameter("ward");
            String specificAddress = request.getParameter("specificAddress");
            String addressType = request.getParameter("addressType");
            boolean isDefault = "on".equals(request.getParameter("isDefault")) || 
                              "true".equals(request.getParameter("isDefault"));

            // Validate required fields
            if (!validateRequiredFields(addressDetail, recipientName, phone)) {
                session.setAttribute("message", "Please fill in all required fields (Address Detail, Recipient Name, Phone).");
                session.setAttribute("messageType", "error");
                response.sendRedirect("manage-address");
                return;
            }

            // Validate phone number format
            if (!isValidPhone(phone)) {
                session.setAttribute("message", "Please enter a valid phone number.");
                session.setAttribute("messageType", "error");
                response.sendRedirect("manage-address");
                return;
            }

            AddressDAO dao = new AddressDAO();

            if (addressIDStr != null && !addressIDStr.trim().isEmpty()) {
                // Update existing address
                handleUpdateAddress(request, response, session, user, dao, addressIDStr,
                    addressDetail, addressName, recipientName, phone, province, 
                    district, ward, specificAddress, addressType, isDefault);
            } else {
                // Add new address
                handleAddAddress(request, response, session, user, dao,
                    addressDetail, addressName, recipientName, phone, province, 
                    district, ward, specificAddress, addressType, isDefault);
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "An error occurred while processing your request.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
        }
    }

    private void handleEditAction(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user, AddressDAO dao) 
            throws ServletException, IOException {

        String addressIDStr = request.getParameter("addressID");
        if (addressIDStr == null || addressIDStr.trim().isEmpty()) {
            session.setAttribute("message", "Address ID is required.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = dao.getAddressByID(addressID);

            if (address != null && address.getUserID() == user.getUserID() && address.isActive()) {
                request.setAttribute("editAddress", address);
                request.getRequestDispatcher("manageAddress.jsp").forward(request, response);
            } else {
                session.setAttribute("message", "Address not found or you don't have permission to edit it.");
                session.setAttribute("messageType", "error");
                response.sendRedirect("profile");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid address ID format.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
        }
    }

    private void handleDeleteAction(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user, AddressDAO dao) throws IOException {

        String addressIDStr = request.getParameter("addressID");
        if (addressIDStr == null || addressIDStr.trim().isEmpty()) {
            session.setAttribute("message", "Address ID is required.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = dao.getAddressByID(addressID);

            if (address != null && address.getUserID() == user.getUserID() && address.isActive()) {
                // Check if it's the default address and user has other addresses
                if (address.isDefault()) {
                    List<Address> userAddresses = dao.getAddressesByUserID(user.getUserID());
                    if (userAddresses.size() <= 1) {
                        session.setAttribute("message", "Cannot delete your only address. Please add another address first.");
                        session.setAttribute("messageType", "error");
                        response.sendRedirect("profile");
                        return;
                    }
                }

                boolean deleted = dao.deleteAddress(addressID);
                if (deleted) {
                    // If deleted address was default, set another address as default
                    if (address.isDefault()) {
                        List<Address> remainingAddresses = dao.getAddressesByUserID(user.getUserID());
                        if (!remainingAddresses.isEmpty()) {
                            dao.setDefaultAddress(user.getUserID(), remainingAddresses.get(0).getAddressID());
                        }
                    }
                    session.setAttribute("message", "Address deleted successfully.");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Failed to delete address. Please try again.");
                    session.setAttribute("messageType", "error");
                }
            } else {
                session.setAttribute("message", "Address not found or you don't have permission to delete it.");
                session.setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid address ID format.");
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect("profile");
    }

    private void handleSetDefaultAction(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user, AddressDAO dao) throws IOException {

        String addressIDStr = request.getParameter("addressID");
        if (addressIDStr == null || addressIDStr.trim().isEmpty()) {
            session.setAttribute("message", "Address ID is required.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = dao.getAddressByID(addressID);

            if (address != null && address.getUserID() == user.getUserID() && address.isActive()) {
                boolean success = dao.setDefaultAddress(user.getUserID(), addressID);
                session.setAttribute("message", success ? 
                    "Default address updated successfully." : 
                    "Failed to update default address.");
                session.setAttribute("messageType", success ? "success" : "error");
            } else {
                session.setAttribute("message", "Address not found or you don't have permission to modify it.");
                session.setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid address ID format.");
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect("profile");
    }

    private void handleUpdateAddress(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user, AddressDAO dao, String addressIDStr,
            String addressDetail, String addressName, String recipientName, String phone,
            String province, String district, String ward, String specificAddress,
            String addressType, boolean isDefault) throws IOException {

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address existingAddress = dao.getAddressByID(addressID);

            if (existingAddress != null && existingAddress.getUserID() == user.getUserID() && existingAddress.isActive()) {
                // Update the address fields
                existingAddress.setAddressDetail(sanitizeInput(addressDetail));
                existingAddress.setAddressName(sanitizeInput(addressName));
                existingAddress.setRecipientName(sanitizeInput(recipientName));
                existingAddress.setPhone(sanitizeInput(phone));
                existingAddress.setProvince(sanitizeInput(province));
                existingAddress.setDistrict(sanitizeInput(district));
                existingAddress.setWard(sanitizeInput(ward));
                existingAddress.setSpecificAddress(sanitizeInput(specificAddress));
                existingAddress.setAddressType(addressType != null && !addressType.trim().isEmpty() ? 
                    addressType.toUpperCase() : "OTHER");
                existingAddress.setDefault(isDefault);

                boolean updated = dao.updateAddress(existingAddress);
                session.setAttribute("message", updated ? 
                    "Address updated successfully." : 
                    "Failed to update address. Please try again.");
                session.setAttribute("messageType", updated ? "success" : "error");
            } else {
                session.setAttribute("message", "Address not found or you don't have permission to update it.");
                session.setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid address ID format.");
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect("profile");
    }

    private void handleAddAddress(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user, AddressDAO dao,
            String addressDetail, String addressName, String recipientName, String phone,
            String province, String district, String ward, String specificAddress,
            String addressType, boolean isDefault) throws IOException {

        // Check address limit
        int currentAddressCount = dao.countAddressesByUser(user.getUserID());
        if (currentAddressCount >= MAX_ADDRESSES_PER_USER) {
            session.setAttribute("message", "You have reached the maximum limit of " + 
                MAX_ADDRESSES_PER_USER + " addresses.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // If this is the first address, make it default automatically
        if (currentAddressCount == 0) {
            isDefault = true;
        }

        // Create new address
        Address address = new Address();
        address.setUserID(user.getUserID());
        address.setAddressDetail(sanitizeInput(addressDetail));
        address.setAddressName(sanitizeInput(addressName));
        address.setRecipientName(sanitizeInput(recipientName));
        address.setPhone(sanitizeInput(phone));
        address.setProvince(sanitizeInput(province));
        address.setDistrict(sanitizeInput(district));
        address.setWard(sanitizeInput(ward));
        address.setSpecificAddress(sanitizeInput(specificAddress));
        address.setAddressType(addressType != null && !addressType.trim().isEmpty() ? 
            addressType.toUpperCase() : "OTHER");
        address.setDefault(isDefault);
        address.setActive(true);

        boolean added = dao.addAddress(address);
        session.setAttribute("message", added ? 
            "Address added successfully." : 
            "Failed to add address. Please try again.");
        session.setAttribute("messageType", added ? "success" : "error");

        response.sendRedirect("profile");
    }

    private boolean validateRequiredFields(String addressDetail, String recipientName, String phone) {
        return addressDetail != null && !addressDetail.trim().isEmpty() &&
               recipientName != null && !recipientName.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty();
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Remove all non-digit characters for validation
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        // Check for Vietnamese phone number patterns
        return cleanPhone.length() >= 9 && cleanPhone.length() <= 12 &&
               (cleanPhone.startsWith("0") || cleanPhone.startsWith("84"));
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("\\s+", " "); // Remove extra whitespace
    }
}