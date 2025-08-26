package Controller;

import DAO.UserDAO;
import Model.User;
import Service.MailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Enhanced RegisterController with improved security and validation
 *
 * @author LoiDH
 */
@WebServlet("/register")
public class RegisterController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final SecureRandom secureRandom = new SecureRandom();

    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,20}$"
    );
    // Enhanced password pattern: at least 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    // Constants
    private static final int CODE_EXPIRY_MINUTES = 5;
    private static final int MAX_VERIFICATION_ATTEMPTS = 3;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Clear any existing registration data
        HttpSession session = request.getSession();
        session.removeAttribute("tempUser");
        session.removeAttribute("verificationCode");
        session.removeAttribute("codeExpiry");
        session.removeAttribute("verificationAttempts");

        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            processRegister(request, response);
        } else if ("verify".equals(action)) {
            processVerification(request, response);
        } else if ("resend".equals(action)) {
            processResendCode(request, response);
        }
    }

    private void processRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Server-side validation
        ValidationResult validation = validateRegistrationData(fullName, email, username, password, confirmPassword);
        if (!validation.isValid()) {
            request.setAttribute("error", validation.getErrorMessage());
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("username", username);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Check for duplicate email
        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("error", "Email đã được sử dụng. Vui lòng chọn email khác.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("username", username);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Check for duplicate username
        if (userDAO.getUserByUsername(username) != null) {
            request.setAttribute("error", "Tên đăng nhập đã được sử dụng. Vui lòng chọn tên khác.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Generate secure 6-digit verification code
        String verificationCode = generateSecureCode();

        // Store verification data with expiry time
        HttpSession session = request.getSession();
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("codeExpiry", System.currentTimeMillis() + (CODE_EXPIRY_MINUTES * 60 * 1000));
        session.setAttribute("verificationAttempts", 0);

        // Store user data with plain text password
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.toLowerCase().trim());
        user.setUserName(username.trim());
        user.setPassword(password); // Store plain text password
        user.setRoleID(3); // Customer
        user.setStatus(1); // Active

        session.setAttribute("tempUser", user);

        // Send verification email
        if (MailService.sendVerificationCode(email, verificationCode, fullName)) {
            session.setAttribute("verificationEmail", email);
            response.sendRedirect("verify.jsp");
        } else {
            request.setAttribute("error", "Không thể gửi email xác thực. Vui lòng thử lại sau.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    private void processVerification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputCode = request.getParameter("code");
        HttpSession session = request.getSession();

        String realCode = (String) session.getAttribute("verificationCode");
        Long codeExpiry = (Long) session.getAttribute("codeExpiry");
        Integer attempts = (Integer) session.getAttribute("verificationAttempts");

        if (attempts == null) {
            attempts = 0;
        }

        // Check if code has expired
        if (codeExpiry == null || System.currentTimeMillis() > codeExpiry) {
            request.setAttribute("error", "Mã xác thực đã hết hạn. Vui lòng yêu cầu mã mới.");
            request.getRequestDispatcher("verify.jsp").forward(request, response);
            return;
        }

        // Check max attempts
        if (attempts >= MAX_VERIFICATION_ATTEMPTS) {
            session.removeAttribute("tempUser");
            session.removeAttribute("verificationCode");
            session.removeAttribute("codeExpiry");
            session.removeAttribute("verificationAttempts");
            request.setAttribute("error", "Đã vượt quá số lần thử. Vui lòng đăng ký lại.");
            response.sendRedirect("register.jsp");
            return;
        }

        if (inputCode != null && inputCode.trim().equals(realCode)) {
            User user = (User) session.getAttribute("tempUser");
            if (user != null) {
                try {
                    userDAO.insert(user);

                    // Clear all registration data
                    session.removeAttribute("tempUser");
                    session.removeAttribute("verificationCode");
                    session.removeAttribute("codeExpiry");
                    session.removeAttribute("verificationAttempts");
                    session.removeAttribute("verificationEmail");

                    // Registration complete
                    session.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
                    response.sendRedirect("login.jsp");

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Đăng ký thất bại. Vui lòng thử lại.");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.");
                response.sendRedirect("register.jsp");
            }
        } else {
            attempts++;
            session.setAttribute("verificationAttempts", attempts);
            int remainingAttempts = MAX_VERIFICATION_ATTEMPTS - attempts;
            request.setAttribute("error", "Mã xác thực không đúng. Còn lại " + remainingAttempts + " lần thử.");
            request.getRequestDispatcher("verify.jsp").forward(request, response);
        }
    }

    private void processResendCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User tempUser = (User) session.getAttribute("tempUser");

        if (tempUser == null) {
            response.sendRedirect("register.jsp");
            return;
        }

        // Generate new code
        String newCode = generateSecureCode();
        session.setAttribute("verificationCode", newCode);
        session.setAttribute("codeExpiry", System.currentTimeMillis() + (CODE_EXPIRY_MINUTES * 60 * 1000));
        session.setAttribute("verificationAttempts", 0);

        if (MailService.sendVerificationCode(tempUser.getEmail(), newCode, tempUser.getFullName())) {
            request.setAttribute("success", "Mã xác thực mới đã được gửi đến email của bạn.");
        } else {
            request.setAttribute("error", "Không thể gửi mã xác thực. Vui lòng thử lại.");
        }

        request.getRequestDispatcher("verify.jsp").forward(request, response);
    }

    private String generateSecureCode() {
        return String.format("%06d", secureRandom.nextInt(900000) + 100000);
    }

    private ValidationResult validateRegistrationData(String fullName, String email, String username,
            String password, String confirmPassword) {
        // Full name validation
        if (fullName == null || fullName.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập họ tên.");
        }
        if (fullName.trim().length() < 2 || fullName.trim().length() > 50) {
            return new ValidationResult(false, "Họ tên phải từ 2-50 ký tự.");
        }
        // Check if full name contains only letters and spaces
        if (!fullName.trim().matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            return new ValidationResult(false, "Họ tên chỉ được chứa chữ cái và khoảng trắng.");
        }

        // Email validation
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập email.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new ValidationResult(false, "Email không hợp lệ.");
        }

        // Username validation
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập tên đăng nhập.");
        }
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            return new ValidationResult(false, "Tên đăng nhập chỉ chứa chữ cái, số và dấu gạch dưới (3-20 ký tự).");
        }

        // Enhanced password validation
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập mật khẩu.");
        }
        if (password.length() < 8) {
            return new ValidationResult(false, "Mật khẩu phải có ít nhất 8 ký tự.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return new ValidationResult(false,
                    "Mật khẩu phải chứa ít nhất: 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt (@$!%*?&).");
        }

        // Confirm password validation
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return new ValidationResult(false, "Mật khẩu xác nhận không khớp.");
        }

        return new ValidationResult(true, "");
    }

    // Inner class for validation result
    private static class ValidationResult {

        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
