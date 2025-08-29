package Controller;

import DAO.AddressDAO;
import Model.Address;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/address")
public class AddressController extends HttpServlet {

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
            loadAddressData(request, sessionUser);

        } catch (Exception e) {
            request.setAttribute("message", "Error: " + e.getMessage());
            request.setAttribute("messageType", "error");
            loadAddressData(request, sessionUser);
        }

        request.getRequestDispatcher("manageAddress.jsp").forward(request, response);
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
            }
        } catch (Exception e) {
            request.setAttribute("message", "Error: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }

        loadAddressData(request, sessionUser);
        request.getRequestDispatcher("manageAddress.jsp").forward(request, response);
    }

    private void handleEditAddress(HttpServletRequest request, User user, String addressIDStr) {
        if (isEmpty(addressIDStr)) {
            request.setAttribute("message", "Invalid address ID.");
            request.setAttribute("messageType", "error");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = addressDAO.getAddressByID(addressID);

            if (address != null && address.getUserID() == user.getUserID()) {
                request.setAttribute("editAddress", address);
            } else {
                request.setAttribute("message", "Address not found or access denied.");
                request.setAttribute("messageType", "error");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid address ID format.");
            request.setAttribute("messageType", "error");
        } catch (Exception e) {
            request.setAttribute("message", "Error retrieving address: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }
    }

    private void handleDeleteAddress(HttpServletRequest request, User user, String addressIDStr) {
        if (isEmpty(addressIDStr)) {
            request.setAttribute("message", "Invalid address ID.");
            request.setAttribute("messageType", "error");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = addressDAO.getAddressByID(addressID);

            if (address != null && address.getUserID() == user.getUserID()) {
                boolean deleted = addressDAO.deleteAddress(addressID, user.getUserID());
                if (deleted) {
                    request.setAttribute("message", "Address deleted successfully.");
                    request.setAttribute("messageType", "success");
                } else {
                    request.setAttribute("message", "Failed to delete address.");
                    request.setAttribute("messageType", "error");
                }
            } else {
                request.setAttribute("message", "Address not found or access denied.");
                request.setAttribute("messageType", "error");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid address ID format.");
            request.setAttribute("messageType", "error");
        } catch (Exception e) {
            request.setAttribute("message", "Error deleting address: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }
    }

    private void handleSetDefaultAddress(HttpServletRequest request, User user, String addressIDStr) {
        if (isEmpty(addressIDStr)) {
            request.setAttribute("message", "Invalid address ID.");
            request.setAttribute("messageType", "error");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = addressDAO.getAddressByID(addressID);

            if (address != null && address.getUserID() == user.getUserID()) {
                boolean updated = addressDAO.setDefaultAddress(user.getUserID(), addressID);
                if (updated) {
                    request.setAttribute("message", "Default address updated successfully.");
                    request.setAttribute("messageType", "success");
                } else {
                    request.setAttribute("message", "Failed to update default address.");
                    request.setAttribute("messageType", "error");
                }
            } else {
                request.setAttribute("message", "Address not found or access denied.");
                request.setAttribute("messageType", "error");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid address ID format.");
            request.setAttribute("messageType", "error");
        } catch (Exception e) {
            request.setAttribute("message", "Error updating default address: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }
    }

    private void handleAddAddress(HttpServletRequest request, User user) {
        // Get form parameters
        String addressName = request.getParameter("addressName");
        String recipientName = request.getParameter("recipientName");
        String phone = request.getParameter("phone");
        String addressDetail = request.getParameter("addressDetail");
        String addressType = request.getParameter("addressType");
        boolean isDefault = "1".equals(request.getParameter("isDefault"));

        // Validate required fields
        if (isEmpty(addressName) || isEmpty(recipientName) || isEmpty(phone) || isEmpty(addressDetail)) {
            request.setAttribute("message", "Please fill in all required fields.");
            request.setAttribute("messageType", "error");
            preserveAddressFormData(request, addressName, recipientName, phone, addressDetail, addressType, isDefault);
            return;
        }

        // Validate phone format (basic validation)
        String cleanPhone = phone.trim();
        if (!cleanPhone.matches("^[0-9+\\-\\s()]{10,15}$")) {
            request.setAttribute("message", "Please enter a valid phone number.");
            request.setAttribute("messageType", "error");
            preserveAddressFormData(request, addressName, recipientName, phone, addressDetail, addressType, isDefault);
            return;
        }

        try {
            // Check address limit (optional - adjust as needed)
            List<Address> existingAddresses = addressDAO.getAddressesByUserID(user.getUserID());
            if (existingAddresses != null && existingAddresses.size() >= 10) {
                request.setAttribute("message", "You have reached the maximum number of addresses (10).");
                request.setAttribute("messageType", "error");
                preserveAddressFormData(request, addressName, recipientName, phone, addressDetail, addressType, isDefault);
                return;
            }

            // Create new address object
            Address address = new Address();
            address.setUserID(user.getUserID());
            address.setAddressName(addressName.trim());
            address.setRecipientName(recipientName.trim());
            address.setPhone(cleanPhone);
            address.setAddressDetail(addressDetail.trim());
            address.setAddressType(addressType != null ? addressType.trim() : "");
            address.setDefault(isDefault);

            // Set required database fields
            address.setActive(true);  // New addresses are active by default
            address.setDisplayOrder(0);  // Default display order

            // Add address to database
            boolean added = addressDAO.addAddress(address);
            if (added) {
                request.setAttribute("message", "Address added successfully.");
                request.setAttribute("messageType", "success");
                // Don't preserve form data on success
            } else {
                request.setAttribute("message", "Failed to add address. Please try again.");
                request.setAttribute("messageType", "error");
                preserveAddressFormData(request, addressName, recipientName, phone, addressDetail, addressType, isDefault);
            }

        } catch (Exception e) {
            request.setAttribute("message", "Error adding address: " + e.getMessage());
            request.setAttribute("messageType", "error");
            preserveAddressFormData(request, addressName, recipientName, phone, addressDetail, addressType, isDefault);
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
                request.setAttribute("message", "Address not found or access denied.");
                request.setAttribute("messageType", "error");
                return;
            }

            // Get form parameters
            String addressName = request.getParameter("addressName");
            String recipientName = request.getParameter("recipientName");
            String phone = request.getParameter("phone");
            String addressDetail = request.getParameter("addressDetail");
            String addressType = request.getParameter("addressType");
            boolean isDefault = "1".equals(request.getParameter("isDefault"));

            // Validate required fields
            if (isEmpty(addressName) || isEmpty(recipientName) || isEmpty(phone) || isEmpty(addressDetail)) {
                request.setAttribute("message", "Please fill in all required fields.");
                request.setAttribute("messageType", "error");
                // Keep the edit form open
                request.setAttribute("editAddress", address);
                return;
            }

            // Validate phone format
            String cleanPhone = phone.trim();
            if (!cleanPhone.matches("^[0-9+\\-\\s()]{10,15}$")) {
                request.setAttribute("message", "Please enter a valid phone number.");
                request.setAttribute("messageType", "error");
                // Keep the edit form open
                request.setAttribute("editAddress", address);
                return;
            }

            // Update address object
            address.setAddressName(addressName.trim());
            address.setRecipientName(recipientName.trim());
            address.setPhone(cleanPhone);
            address.setAddressDetail(addressDetail.trim());
            address.setAddressType(addressType != null ? addressType.trim() : "");
            address.setDefault(isDefault);

            // Update address in database
            boolean updated = addressDAO.updateAddress(address);
            if (updated) {
                request.setAttribute("message", "Address updated successfully.");
                request.setAttribute("messageType", "success");
                // Remove edit mode after successful update
            } else {
                request.setAttribute("message", "Failed to update address. Please try again.");
                request.setAttribute("messageType", "error");
                // Keep the edit form open
                request.setAttribute("editAddress", address);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid address ID format.");
            request.setAttribute("messageType", "error");
        } catch (Exception e) {
            request.setAttribute("message", "Error updating address: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }
    }

    private void preserveAddressFormData(HttpServletRequest request, String addressName, String recipientName,
            String phone, String addressDetail, String addressType, boolean isDefault) {
        request.setAttribute("preservedAddressName", addressName);
        request.setAttribute("preservedRecipientName", recipientName);
        request.setAttribute("preservedPhone", phone);
        request.setAttribute("preservedAddressDetail", addressDetail);
        request.setAttribute("preservedAddressType", addressType);
        request.setAttribute("preservedIsDefault", isDefault);
    }

    private void loadAddressData(HttpServletRequest request, User user) {
        try {
            List<Address> addressList = addressDAO.getAddressesByUserID(user.getUserID());
            request.setAttribute("addressList", addressList);
        } catch (Exception e) {
            request.setAttribute("message", "Error loading address data: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
