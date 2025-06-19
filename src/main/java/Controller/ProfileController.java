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
import java.util.regex.Pattern;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Enhanced phone validation patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9+()\\s.-]{10,20}$"
    );

    // Vietnamese phone number patterns
    private static final Pattern VN_MOBILE_PATTERN = Pattern.compile(
            "^(\\+84|84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$"
    );

    private static final Pattern VN_LANDLINE_PATTERN = Pattern.compile(
            "^(\\+84|84|0)(2[0-9])[0-9]{8}$"
    );

    // International phone pattern (basic)
    private static final Pattern INTERNATIONAL_PATTERN = Pattern.compile(
            "^\\+[1-9][0-9]{6,14}$"
    );

    // Full name validation patterns
    private static final Pattern FULLNAME_PATTERN = Pattern.compile(
            "^[\\p{L}\\p{M}\\s.''-]{2,100}$", Pattern.UNICODE_CASE
    );

    // Pattern to detect consecutive spaces or special characters
    private static final Pattern CONSECUTIVE_SPACES = Pattern.compile("\\s{2,}");
    private static final Pattern CONSECUTIVE_SPECIAL = Pattern.compile("[.''-]{2,}");

    // Pattern for Vietnamese name characters (optional - for stricter Vietnamese validation)
    private static final Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile(
            "^[a-zA-ZàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ\\s.''-]+$"
    );

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

        try {
            // Lấy thông tin user từ DB bằng cả username và userID để đảm bảo dữ liệu chính xác
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByUsername(sessionUser.getUserName());

            // Fallback: nếu không tìm thấy bằng username, thử bằng userID
            if (user == null) {
                user = userDAO.getUserByID(sessionUser.getUserID());
            }

            if (user == null) {
                // User không tồn tại trong DB, có thể session bị lỗi
                session.setAttribute("message", "User data not found. Please login again.");
                session.setAttribute("messageType", "error");
                response.sendRedirect("login.jsp");
                return;
            }

            // Cập nhật session user với dữ liệu mới nhất từ DB
            updateSessionUser(session, user);

            // Lấy danh sách địa chỉ với error handling
            AddressDAO addressDAO = new AddressDAO();
            List<Address> addressList = null;

            try {
                addressList = addressDAO.getAddressesByUserID(user.getUserID());
            } catch (Exception e) {
                System.err.println("Error loading addresses for user " + user.getUserID() + ": " + e.getMessage());
                // Không throw exception, chỉ log lỗi và để addressList = null
            }

            // Set attributes cho JSP
            request.setAttribute("user", user);
            request.setAttribute("addressList", addressList);

            // Lấy message từ session nếu có
            handleSessionMessages(request, session);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error loading profile data: " + e.getMessage());
            request.setAttribute("messageType", "error");

            // Vẫn forward để hiển thị lỗi thay vì crash
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            return;
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

        // Kiểm tra quyền truy cập
        if (sessionUser.getRoleID() == 4) {
            session.setAttribute("message", "You do not have permission to update profile.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        // Lấy parameters từ form
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");

        // Validate input data
        String validationError = validateProfileData(fullName, email, phone, dobStr);
        if (validationError != null) {
            session.setAttribute("message", validationError);
            session.setAttribute("messageType", "error");
            response.sendRedirect("profile");
            return;
        }

        try {
            // Lấy thông tin user hiện tại từ DB
            UserDAO dao = new UserDAO();
            User currentUser = dao.getUserByID(sessionUser.getUserID());

            if (currentUser == null) {
                session.setAttribute("message", "User not found. Please login again.");
                session.setAttribute("messageType", "error");
                response.sendRedirect("login.jsp");
                return;
            }

            // Cập nhật thông tin user
            User updatedUser = new User();
            updatedUser.setUserID(currentUser.getUserID());
            updatedUser.setUserName(currentUser.getUserName());
            updatedUser.setPassword(currentUser.getPassword()); // Giữ nguyên password
            updatedUser.setRoleID(currentUser.getRoleID()); // Giữ nguyên role
            updatedUser.setStatus(currentUser.getStatus()); // Giữ nguyên status
            updatedUser.setRegistrationDate(currentUser.getRegistrationDate()); // Giữ nguyên registration date

            // Cập nhật các thông tin mới
            updatedUser.setFullName(fullName.trim());
            updatedUser.setEmail(email.trim().toLowerCase());
            updatedUser.setPhone(phone.trim());
            updatedUser.setGender(gender);

            // Xử lý date of birth
            if (dobStr != null && !dobStr.trim().isEmpty()) {
                try {
                    Date dob = Date.valueOf(dobStr.trim());
                    updatedUser.setDob(dob);
                } catch (IllegalArgumentException e) {
                    session.setAttribute("message", "Invalid date format for date of birth.");
                    session.setAttribute("messageType", "error");
                    response.sendRedirect("profile");
                    return;
                }
            } else {
                updatedUser.setDob(null);
            }

            // Cập nhật vào database
            boolean updated = dao.updateUser(updatedUser);

            if (updated) {
                // Cập nhật session user với thông tin mới
                updateSessionUser(session, updatedUser);

                session.setAttribute("message", "Profile updated successfully!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Failed to update profile. Please try again.");
                session.setAttribute("messageType", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "Error updating profile: " + e.getMessage());
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect("profile");
    }

    /**
     * Enhanced phone number validation
     */
    private String validatePhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required.";
        }

        // Remove all spaces, dashes, dots, parentheses for validation
        String cleanPhone = phone.replaceAll("[\\s.()-]", "");

        // Basic format check
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return "Phone number contains invalid characters. Only digits, +, (), spaces, dots, and dashes are allowed.";
        }

        // Length validation
        if (cleanPhone.length() < 10 || cleanPhone.length() > 15) {
            return "Phone number must be between 10 and 15 digits.";
        }

        // Check if it's all digits (after removing + sign)
        String digitsOnly = cleanPhone.replaceAll("^\\+", "");
        if (!digitsOnly.matches("^[0-9]+$")) {
            return "Phone number must contain only digits after country code.";
        }

        // Enhanced validation for Vietnamese numbers
        if (isVietnamesePhoneNumber(cleanPhone)) {
            return validateVietnamesePhone(cleanPhone);
        }

        // International number validation
        if (cleanPhone.startsWith("+")) {
            if (!INTERNATIONAL_PATTERN.matcher(cleanPhone).matches()) {
                return "Invalid international phone number format. Use format: +[country code][number]";
            }
        }

        // General validation for other formats
        if (!cleanPhone.startsWith("+") && !cleanPhone.startsWith("0")) {
            // If not international and not starting with 0, might be missing country code
            if (cleanPhone.length() >= 10) {
                return "Phone number should start with country code (+) or local prefix (0).";
            }
        }

        return null; // Valid phone number
    }

    /**
     * Check if phone number is Vietnamese format
     */
    private boolean isVietnamesePhoneNumber(String phone) {
        return phone.startsWith("84") || phone.startsWith("+84") || phone.startsWith("0");
    }

    /**
     * Validate Vietnamese phone number specifically
     */
    private String validateVietnamesePhone(String phone) {
        // Check mobile numbers
        if (VN_MOBILE_PATTERN.matcher(phone).matches()) {
            return null; // Valid Vietnamese mobile
        }

        // Check landline numbers
        if (VN_LANDLINE_PATTERN.matcher(phone).matches()) {
            return null; // Valid Vietnamese landline
        }

        // If it looks like Vietnamese but doesn't match patterns
        if (phone.startsWith("84") || phone.startsWith("+84") || phone.startsWith("0")) {
            return "Invalid Vietnamese phone number format. "
                    + "Mobile: 0[3,5,7,8,9]xxxxxxxx, Landline: 0[2x]xxxxxxxx";
        }

        return null;
    }

    /**
     * Validate profile data
     */
    private String validateProfileData(String fullName, String email, String phone, String dobStr) {
        // Enhanced full name validation
        String nameValidationError = validateFullName(fullName);
        if (nameValidationError != null) {
            return nameValidationError;
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email address is required.";
        }

        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required.";
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Invalid email format.";
        }

        // Validate phone format with enhanced validation
        String phoneValidationError = validatePhoneNumber(phone.trim());
        if (phoneValidationError != null) {
            return phoneValidationError;
        }

        // Validate date format if provided
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                Date.valueOf(dobStr.trim());
            } catch (IllegalArgumentException e) {
                return "Invalid date format for date of birth. Use YYYY-MM-DD format.";
            }
        }

        return null; // No validation errors
    }

    /**
     * Enhanced full name validation
     */
    private String validateFullName(String fullName) {
        // Check if null or empty
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required.";
        }

        String trimmedName = fullName.trim();

        // Length validation
        if (trimmedName.length() < 2) {
            return "Full name must be at least 2 characters long.";
        }

        if (trimmedName.length() > 100) {
            return "Full name must not exceed 100 characters.";
        }

        // Check for valid characters (letters, spaces, apostrophes, hyphens, dots)
        if (!FULLNAME_PATTERN.matcher(trimmedName).matches()) {
            return "Full name can only contain letters, spaces, apostrophes ('), hyphens (-), and dots (.).";
        }

        // Check for consecutive spaces
        if (CONSECUTIVE_SPACES.matcher(trimmedName).find()) {
            return "Full name cannot contain consecutive spaces.";
        }

        // Check for consecutive special characters
        if (CONSECUTIVE_SPECIAL.matcher(trimmedName).find()) {
            return "Full name cannot contain consecutive special characters.";
        }

        // Cannot start or end with special characters
        if (trimmedName.matches("^[.''-].*") || trimmedName.matches(".*[.''-]$")) {
            return "Full name cannot start or end with special characters.";
        }

        // Cannot start or end with space (already trimmed, but double check)
        if (fullName.startsWith(" ") || fullName.endsWith(" ")) {
            return "Full name cannot start or end with spaces.";
        }

        // Must contain at least one letter
        if (!trimmedName.matches(".*[\\p{L}].*")) {
            return "Full name must contain at least one letter.";
        }

        // Check for reasonable word count (1-10 words)
        String[] words = trimmedName.split("\\s+");
        if (words.length > 10) {
            return "Full name cannot exceed 10 words.";
        }

        // Each word should be at least 1 character and not be only special characters
        for (String word : words) {
            if (word.length() == 0) {
                continue; // Skip empty strings from split
            }

            // Each word must contain at least one letter
            if (!word.matches(".*[\\p{L}].*")) {
                return "Each word in full name must contain at least one letter.";
            }

            // Word cannot be only special characters
            if (word.matches("^[.''-]+$")) {
                return "Full name cannot contain words made only of special characters.";
            }
        }

        // Optional: Stricter validation for Vietnamese names
        // Uncomment below if you want to enforce Vietnamese character set only
        /*
        if (!VIETNAMESE_NAME_PATTERN.matcher(trimmedName).matches()) {
            return "Full name contains invalid characters for Vietnamese names.";
        }
         */
        // Check for common invalid patterns
        if (trimmedName.matches(".*[0-9].*")) {
            return "Full name cannot contain numbers.";
        }

        // Check for suspicious patterns (all caps, all lowercase for long names)
        if (trimmedName.length() > 20) {
            if (trimmedName.equals(trimmedName.toUpperCase()) && !trimmedName.matches(".*[a-z].*")) {
                return "Full name should not be in all capital letters.";
            }
            if (trimmedName.equals(trimmedName.toLowerCase()) && !trimmedName.matches(".*[A-Z].*")) {
                return "Full name should have proper capitalization.";
            }
        }

        return null; // Valid full name
    }

    /**
     * Update session user with fresh data from database
     */
    private void updateSessionUser(HttpSession session, User dbUser) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null && dbUser != null) {
            sessionUser.setFullName(dbUser.getFullName());
            sessionUser.setEmail(dbUser.getEmail());
            sessionUser.setPhone(dbUser.getPhone());
            sessionUser.setGender(dbUser.getGender());
            sessionUser.setDob(dbUser.getDob());
            sessionUser.setStatus(dbUser.getStatus());
            // Không cập nhật password trong session vì lý do bảo mật
            session.setAttribute("user", sessionUser);
        }
    }

    /**
     * Handle session messages and transfer them to request attributes
     */
    private void handleSessionMessages(HttpServletRequest request, HttpSession session) {
        String message = (String) session.getAttribute("message");
        String messageType = (String) session.getAttribute("messageType");

        if (message != null && messageType != null) {
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);

            // Clear messages from session after transferring to request
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }
    }
}
