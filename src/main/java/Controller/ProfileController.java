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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private AddressDAO addressDAO = new AddressDAO();

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+84|84|0)(3|5|7|8|9)[0-9]{8}$"
    );

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-ZÀ-ỹ\\s]{2,50}$"
    );

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
                    request.setAttribute("message", "Địa chỉ không tồn tại.");
                    request.setAttribute("messageType", "error");
                }
            } catch (Exception e) {
                request.setAttribute("message", "ID địa chỉ không hợp lệ.");
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
                    // Pass userID parameter to match AddressDAO.deleteAddress(int addressID, int userID)
                    boolean deleted = addressDAO.deleteAddress(addressID, user.getUserID());
                    if (deleted) {
                        request.setAttribute("message", "Xóa địa chỉ thành công.");
                        request.setAttribute("messageType", "success");
                    } else {
                        request.setAttribute("message", "Xóa địa chỉ thất bại.");
                        request.setAttribute("messageType", "error");
                    }
                } else {
                    request.setAttribute("message", "Địa chỉ không tồn tại.");
                    request.setAttribute("messageType", "error");
                }
            } catch (Exception e) {
                request.setAttribute("message", "Lỗi khi xóa địa chỉ.");
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
                        request.setAttribute("message", "Cập nhật địa chỉ mặc định thành công.");
                        request.setAttribute("messageType", "success");
                    } else {
                        request.setAttribute("message", "Cập nhật địa chỉ mặc định thất bại.");
                        request.setAttribute("messageType", "error");
                    }
                } else {
                    request.setAttribute("message", "Địa chỉ không tồn tại.");
                    request.setAttribute("messageType", "error");
                }
            } catch (Exception e) {
                request.setAttribute("message", "Lỗi khi cập nhật địa chỉ mặc định.");
                request.setAttribute("messageType", "error");
            }
        }
    }

    private void handleAddAddress(HttpServletRequest request, User user) {
        String addressName = request.getParameter("addressName");
        String recipientName = request.getParameter("recipientName");
        String phone = request.getParameter("phone");
        String addressDetail = request.getParameter("addressDetail");
        String addressType = request.getParameter("addressType");
        boolean isDefault = "1".equals(request.getParameter("isDefault"));

        // Create Address object with required fields (removed province, district, ward)
        Address address = new Address();
        address.setUserID(user.getUserID());
        address.setAddressName(addressName != null ? addressName.trim() : "");
        address.setRecipientName(recipientName != null ? recipientName.trim() : "");
        address.setPhone(phone != null ? phone.trim() : "");
        address.setAddressDetail(addressDetail != null ? addressDetail.trim() : "");
        address.setAddressType(addressType != null ? addressType.trim() : "");
        address.setDefault(isDefault);

        boolean added = addressDAO.addAddress(address);
        if (added) {
            request.setAttribute("message", "Thêm địa chỉ thành công.");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Thêm địa chỉ thất bại.");
            request.setAttribute("messageType", "error");
            // Preserve form data
            preserveAddressFormData(request, addressName, recipientName, phone, addressDetail, addressType, isDefault);
        }
    }

    private void handleUpdateAddress(HttpServletRequest request, User user) {
        String addressIDStr = request.getParameter("addressID");

        if (isEmpty(addressIDStr)) {
            request.setAttribute("message", "ID địa chỉ không hợp lệ.");
            request.setAttribute("messageType", "error");
            return;
        }

        try {
            int addressID = Integer.parseInt(addressIDStr);
            Address address = addressDAO.getAddressByID(addressID);

            if (address == null || address.getUserID() != user.getUserID()) {
                request.setAttribute("message", "Địa chỉ không tồn tại.");
                request.setAttribute("messageType", "error");
                return;
            }

            String addressName = request.getParameter("addressName");
            String recipientName = request.getParameter("recipientName");
            String phone = request.getParameter("phone");
            String addressDetail = request.getParameter("addressDetail");
            String addressType = request.getParameter("addressType");
            boolean isDefault = "1".equals(request.getParameter("isDefault"));

            // Update fields (removed province, district, ward)
            address.setAddressName(addressName != null ? addressName.trim() : "");
            address.setRecipientName(recipientName != null ? recipientName.trim() : "");
            address.setPhone(phone != null ? phone.trim() : "");
            address.setAddressDetail(addressDetail != null ? addressDetail.trim() : "");
            address.setAddressType(addressType != null ? addressType.trim() : "");
            address.setDefault(isDefault);

            boolean updated = addressDAO.updateAddress(address);
            if (updated) {
                request.setAttribute("message", "Cập nhật địa chỉ thành công.");
                request.setAttribute("messageType", "success");
            } else {
                request.setAttribute("message", "Cập nhật địa chỉ thất bại.");
                request.setAttribute("messageType", "error");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("message", "ID địa chỉ không hợp lệ.");
            request.setAttribute("messageType", "error");
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, User user) {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");

        // Enhanced validation for profile - ALL FIELDS REQUIRED
        List<String> errors = validateProfile(fullName, email, phone, dobStr, gender);

        if (!errors.isEmpty()) {
            request.setAttribute("message", String.join("<br>", errors));
            request.setAttribute("messageType", "error");
            // Preserve form data
            preserveProfileFormData(request, fullName, email, phone, dobStr, gender);
            return;
        }

        // Check if email already exists (except current user)
        if (!email.trim().equals(user.getEmail())) {
            User existingUser = userDAO.getUserByEmail(email.trim());
            if (existingUser != null && existingUser.getUserID() != user.getUserID()) {
                request.setAttribute("message", "Email này đã được sử dụng bởi tài khoản khác.");
                request.setAttribute("messageType", "error");
                preserveProfileFormData(request, fullName, email, phone, dobStr, gender);
                return;
            }
        }

        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhone(phone.trim());
        user.setGender(gender.trim()); // Now required, so no null check needed

        // Parse date of birth - now required
        try {
            Date dob = Date.valueOf(dobStr);
            user.setDob(dob);
        } catch (IllegalArgumentException e) {
            request.setAttribute("message", "Định dạng ngày sinh không hợp lệ.");
            request.setAttribute("messageType", "error");
            preserveProfileFormData(request, fullName, email, phone, dobStr, gender);
            return;
        }

        boolean updated = userDAO.updateUser(user);
        if (updated) {
            request.getSession().setAttribute("user", user);
            request.setAttribute("message", "Cập nhật thông tin thành công.");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Cập nhật thông tin thất bại.");
            request.setAttribute("messageType", "error");
        }
    }

    // Enhanced validation methods - ALL FIELDS REQUIRED
    private List<String> validateProfile(String fullName, String email, String phone, String dobStr, String gender) {
        List<String> errors = new ArrayList<>();

        // Validate full name - REQUIRED
        if (isEmpty(fullName)) {
            errors.add("Họ tên là trường bắt buộc, không được để trống.");
        } else if (fullName.trim().length() < 2) {
            errors.add("Họ tên phải có ít nhất 2 ký tự.");
        } else if (fullName.trim().length() > 50) {
            errors.add("Họ tên không được vượt quá 50 ký tự.");
        } else if (!NAME_PATTERN.matcher(fullName.trim()).matches()) {
            errors.add("Họ tên chỉ được chứa chữ cái và khoảng trắng.");
        }

        // Validate email - REQUIRED
        if (isEmpty(email)) {
            errors.add("Email là trường bắt buộc, không được để trống.");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.add("Định dạng email không hợp lệ.");
        }

        // Validate phone - REQUIRED
        if (isEmpty(phone)) {
            errors.add("Số điện thoại là trường bắt buộc, không được để trống.");
        } else if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            errors.add("Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số).");
        }

        // Validate date of birth - REQUIRED
        if (isEmpty(dobStr)) {
            errors.add("Ngày sinh là trường bắt buộc, không được để trống.");
        } else {
            try {
                Date dob = Date.valueOf(dobStr);
                Date currentDate = new Date(System.currentTimeMillis());

                if (dob.after(currentDate)) {
                    errors.add("Ngày sinh không thể là ngày trong tương lai.");
                }

                // Check minimum age (13 years old)
                long minBirthTime = System.currentTimeMillis() - (13L * 365 * 24 * 60 * 60 * 1000);
                if (dob.getTime() > minBirthTime) {
                    errors.add("Tuổi phải từ 13 trở lên.");
                }

                // Check maximum age (120 years old)
                long maxBirthTime = System.currentTimeMillis() - (120L * 365 * 24 * 60 * 60 * 1000);
                if (dob.getTime() < maxBirthTime) {
                    errors.add("Ngày sinh không hợp lệ.");
                }
            } catch (IllegalArgumentException e) {
                errors.add("Định dạng ngày sinh không hợp lệ. Vui lòng sử dụng định dạng YYYY-MM-DD.");
            }
        }

        // Validate gender - REQUIRED
        if (isEmpty(gender)) {
            errors.add("Giới tính là trường bắt buộc, vui lòng chọn.");
        } else if (!gender.equals("Nam") && !gender.equals("Nữ") && !gender.equals("Khác")) {
            errors.add("Giới tính không hợp lệ. Vui lòng chọn Nam, Nữ hoặc Khác.");
        }

        return errors;
    }

    // Helper methods to preserve form data
    private void preserveProfileFormData(HttpServletRequest request, String fullName, String email, String phone, String dobStr, String gender) {
        request.setAttribute("preservedFullName", fullName);
        request.setAttribute("preservedEmail", email);
        request.setAttribute("preservedPhone", phone);
        request.setAttribute("preservedDob", dobStr);
        request.setAttribute("preservedGender", gender);
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

    // Security enhancement: Sanitize input to prevent XSS
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;");
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
            request.setAttribute("message", "Lỗi khi tải dữ liệu.");
            request.setAttribute("messageType", "error");
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
