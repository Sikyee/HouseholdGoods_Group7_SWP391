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
    

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9+()\\s-]{10,15}$"
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
     * Validate profile data
     */
    private String validateProfileData(String fullName, String email, String phone, String dobStr) {
        // Check required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required.";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Email address is required.";
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required.";
        }

        // Validate full name length
        if (fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            return "Full name must be between 2 and 100 characters.";
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Invalid email format.";
        }

        // Validate phone format
        if (!PHONE_PATTERN.matcher(phone.trim().replaceAll("\\s+", "")).matches()) {
            return "Invalid phone number format.";
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