package Controller;

import DAO.UserDAO;
import Model.User;
import Service.MailService;
import Security.PasswordHasher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Enhanced RegisterController with secure password hashing and improved error
 * handling
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
        clearRegistrationSession(session);

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

        try {
            // Server-side validation
            ValidationResult validation = validateRegistrationData(fullName, email, username, password, confirmPassword);
            if (!validation.isValid()) {
                handleRegistrationError(request, response, validation.getErrorMessage(), fullName, email, username);
                return;
            }

            // Check for duplicate email
            if (userDAO.getUserByEmail(email) != null) {
                handleRegistrationError(request, response,
                        "Email '" + email + "' đã được sử dụng. Vui lòng chọn email khác hoặc đăng nhập nếu đây là tài khoản của bạn.",
                        fullName, null, username);
                return;
            }

            // Check for duplicate username
            if (userDAO.getUserByUsername(username) != null) {
                handleRegistrationError(request, response,
                        "Tên đăng nhập '" + username + "' đã được sử dụng. Vui lòng chọn tên đăng nhập khác.",
                        fullName, email, null);
                return;
            }

            // Validate email format more strictly
            if (!isValidEmailDomain(email)) {
                handleRegistrationError(request, response,
                        "Email không hợp lệ hoặc tên miền không được hỗ trợ. Vui lòng sử dụng email từ các nhà cung cấp phổ biến.",
                        fullName, null, username);
                return;
            }

            // Generate secure 6-digit verification code
            String verificationCode = generateSecureCode();

            // Test email service before proceeding
            if (!MailService.testConnection()) {
                handleRegistrationError(request, response,
                        "Dịch vụ email hiện không khả dụng. Vui lòng thử lại sau ít phút.",
                        fullName, email, username);
                return;
            }

            // Store verification data with expiry time
            HttpSession session = request.getSession();
            session.setAttribute("verificationCode", verificationCode);
            session.setAttribute("codeExpiry", System.currentTimeMillis() + (CODE_EXPIRY_MINUTES * 60 * 1000));
            session.setAttribute("verificationAttempts", 0);

            // Store user data - password will be hashed when inserting to database
            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.toLowerCase().trim());
            user.setUserName(username.trim());
            user.setPassword(password); // Will be hashed in UserDAO.insert() method
            user.setRoleID(3); // Customer
            user.setStatus(1); // Active

            session.setAttribute("tempUser", user);

            // Send verification email
            if (MailService.sendVerificationCode(email, verificationCode, fullName)) {
                session.setAttribute("verificationEmail", email);
                session.setAttribute("success", "Mã xác thực đã được gửi đến email " + maskEmail(email) + ". Vui lòng kiểm tra hộp thư.");
                response.sendRedirect("verify.jsp");
            } else {
                // Clear session data if email sending fails
                clearRegistrationSession(session);
                handleRegistrationError(request, response,
                        "Không thể gửi email xác thực đến địa chỉ '" + email + "'. Vui lòng kiểm tra địa chỉ email và thử lại.",
                        fullName, email, username);
            }

        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();

            // Clear any session data
            clearRegistrationSession(request.getSession());

            // Provide user-friendly error message
            String errorMessage = "Đã xảy ra lỗi trong quá trình đăng ký. ";
            if (e.getMessage() != null && e.getMessage().contains("database")) {
                errorMessage += "Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại sau.";
            } else if (e.getMessage() != null && e.getMessage().contains("email")) {
                errorMessage += "Lỗi dịch vụ email. Vui lòng thử lại sau.";
            } else {
                errorMessage += "Vui lòng thử lại sau ít phút.";
            }

            handleRegistrationError(request, response, errorMessage, fullName, email, username);
        }
    }

    private void processVerification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputCode = request.getParameter("code");
        HttpSession session = request.getSession();

        try {
            String realCode = (String) session.getAttribute("verificationCode");
            Long codeExpiry = (Long) session.getAttribute("codeExpiry");
            Integer attempts = (Integer) session.getAttribute("verificationAttempts");
            User tempUser = (User) session.getAttribute("tempUser");

            // Check if session data is valid
            if (tempUser == null || realCode == null) {
                clearRegistrationSession(session);
                request.setAttribute("error", "Phiên đăng ký đã hết hạn hoặc không hợp lệ. Vui lòng đăng ký lại.");
                response.sendRedirect("register.jsp");
                return;
            }

            if (attempts == null) {
                attempts = 0;
            }

            // Validate input code
            if (inputCode == null || inputCode.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng nhập mã xác thực.");
                request.getRequestDispatcher("verify.jsp").forward(request, response);
                return;
            }

            // Check if code has expired
            if (codeExpiry == null || System.currentTimeMillis() > codeExpiry) {
                request.setAttribute("error", "Mã xác thực đã hết hạn sau " + CODE_EXPIRY_MINUTES + " phút. Vui lòng yêu cầu mã mới.");
                request.getRequestDispatcher("verify.jsp").forward(request, response);
                return;
            }

            // Check max attempts
            if (attempts >= MAX_VERIFICATION_ATTEMPTS) {
                clearRegistrationSession(session);
                request.setAttribute("error", "Đã vượt quá " + MAX_VERIFICATION_ATTEMPTS + " lần thử. Quá trình đăng ký đã bị hủy. Vui lòng đăng ký lại.");
                response.sendRedirect("register.jsp");
                return;
            }

            if (inputCode.trim().equals(realCode)) {
                // Verification successful - now try to insert into database
                try {
                    // Double-check that user doesn't exist before inserting
                    if (userDAO.getUserByEmail(tempUser.getEmail()) != null) {
                        clearRegistrationSession(session);
                        request.setAttribute("error", "Email đã được đăng ký bởi người dùng khác trong khi bạn đang xác thực. Vui lòng đăng ký lại với email khác.");
                        response.sendRedirect("register.jsp");
                        return;
                    }

                    if (userDAO.getUserByUsername(tempUser.getUserName()) != null) {
                        clearRegistrationSession(session);
                        request.setAttribute("error", "Tên đăng nhập đã được sử dụng bởi người dùng khác trong khi bạn đang xác thực. Vui lòng đăng ký lại với tên đăng nhập khác.");
                        response.sendRedirect("register.jsp");
                        return;
                    }

                    // Password will be securely hashed in UserDAO.insert()
                    userDAO.insert(tempUser);

                    // Clear all registration data
                    clearRegistrationSession(session);

                    // Registration complete
                    session.setAttribute("success", "Đăng ký thành công! Chào mừng " + tempUser.getFullName() + ". Vui lòng đăng nhập để tiếp tục.");
                    response.sendRedirect("login.jsp");

                } catch (Exception e) {
                    System.err.println("Database insert error: " + e.getMessage());
                    e.printStackTrace();

                    // Clear registration data since insertion failed
                    clearRegistrationSession(session);

                    String errorMessage = "Đăng ký thất bại do lỗi hệ thống. ";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("duplicate") || e.getMessage().contains("UNIQUE")) {
                            errorMessage += "Thông tin đăng ký đã tồn tại trong hệ thống.";
                        } else if (e.getMessage().contains("connection") || e.getMessage().contains("database")) {
                            errorMessage += "Lỗi kết nối cơ sở dữ liệu.";
                        } else {
                            errorMessage += "Lỗi không xác định.";
                        }
                    }
                    errorMessage += " Vui lòng thử đăng ký lại.";

                    request.setAttribute("error", errorMessage);
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                }
            } else {
                attempts++;
                session.setAttribute("verificationAttempts", attempts);
                int remainingAttempts = MAX_VERIFICATION_ATTEMPTS - attempts;

                String errorMessage = "Mã xác thực '" + inputCode.trim() + "' không đúng. ";
                if (remainingAttempts > 0) {
                    errorMessage += "Còn lại " + remainingAttempts + " lần thử.";
                } else {
                    errorMessage += "Đã hết số lần thử.";
                }

                request.setAttribute("error", errorMessage);
                request.getRequestDispatcher("verify.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("Verification process error: " + e.getMessage());
            e.printStackTrace();

            clearRegistrationSession(session);
            request.setAttribute("error", "Đã xảy ra lỗi trong quá trình xác thực. Vui lòng đăng ký lại.");
            response.sendRedirect("register.jsp");
        }
    }

    private void processResendCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User tempUser = (User) session.getAttribute("tempUser");

        if (tempUser == null) {
            request.setAttribute("error", "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.");
            response.sendRedirect("register.jsp");
            return;
        }

        try {
            // Generate new code
            String newCode = generateSecureCode();
            session.setAttribute("verificationCode", newCode);
            session.setAttribute("codeExpiry", System.currentTimeMillis() + (CODE_EXPIRY_MINUTES * 60 * 1000));
            session.setAttribute("verificationAttempts", 0);

            if (MailService.sendVerificationCode(tempUser.getEmail(), newCode, tempUser.getFullName())) {
                request.setAttribute("success", "Mã xác thực mới đã được gửi đến email " + maskEmail(tempUser.getEmail()) + ". Mã có hiệu lực trong " + CODE_EXPIRY_MINUTES + " phút.");
            } else {
                request.setAttribute("error", "Không thể gửi mã xác thực đến email " + maskEmail(tempUser.getEmail()) + ". Vui lòng kiểm tra kết nối internet và thử lại.");
            }

        } catch (Exception e) {
            System.err.println("Resend code error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi khi gửi lại mã xác thực. Vui lòng thử lại.");
        }

        request.getRequestDispatcher("verify.jsp").forward(request, response);
    }

    private void handleRegistrationError(HttpServletRequest request, HttpServletResponse response,
            String errorMessage, String fullName, String email, String username)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("username", username);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    private void clearRegistrationSession(HttpSession session) {
        session.removeAttribute("tempUser");
        session.removeAttribute("verificationCode");
        session.removeAttribute("codeExpiry");
        session.removeAttribute("verificationAttempts");
        session.removeAttribute("verificationEmail");
    }

    private String generateSecureCode() {
        return String.format("%06d", secureRandom.nextInt(900000) + 100000);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "*@" + domain;
        } else {
            return localPart.charAt(0) + "*".repeat(localPart.length() - 2) + localPart.charAt(localPart.length() - 1) + "@" + domain;
        }
    }

    private boolean isValidEmailDomain(String email) {
        // Add basic domain validation
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        String[] validDomains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "edu.vn", "fpt.edu.vn", "student.hust.edu.vn"};

        for (String validDomain : validDomains) {
            if (domain.equals(validDomain) || domain.endsWith("." + validDomain)) {
                return true;
            }
        }
        return domain.matches(".*\\.(com|org|net|edu|gov|vn|co|info)$");
    }

    private ValidationResult validateRegistrationData(String fullName, String email, String username,
            String password, String confirmPassword) {
        // Full name validation
        if (fullName == null || fullName.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập họ tên đầy đủ.");
        }
        if (fullName.trim().length() < 2 || fullName.trim().length() > 50) {
            return new ValidationResult(false, "Họ tên phải từ 2-50 ký tự. Hiện tại: " + fullName.trim().length() + " ký tự.");
        }
        // Check if full name contains only letters and spaces
        if (!fullName.trim().matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            return new ValidationResult(false, "Họ tên chỉ được chứa chữ cái tiếng Việt và khoảng trắng. Không được chứa số hoặc ký tự đặc biệt.");
        }

        // Email validation
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập địa chỉ email.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new ValidationResult(false, "Định dạng email không hợp lệ. Ví dụ đúng: example@domain.com");
        }
        if (email.trim().length() > 100) {
            return new ValidationResult(false, "Email quá dài. Tối đa 100 ký tự.");
        }

        // Username validation
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập tên đăng nhập.");
        }
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            return new ValidationResult(false, "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới (_). Độ dài từ 3-20 ký tự.");
        }

        // Enhanced password validation
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập mật khẩu.");
        }
        if (password.length() < 8) {
            return new ValidationResult(false, "Mật khẩu phải có ít nhất 8 ký tự. Hiện tại: " + password.length() + " ký tự.");
        }
        if (password.length() > 128) {
            return new ValidationResult(false, "Mật khẩu quá dài. Tối đa 128 ký tự.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return new ValidationResult(false,
                    "Mật khẩu không đủ mạnh. Cần có: ít nhất 1 chữ hoa (A-Z), 1 chữ thường (a-z), 1 số (0-9) và 1 ký tự đặc biệt (@$!%*?&).");
        }

        // Confirm password validation
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return new ValidationResult(false, "Vui lòng xác nhận mật khẩu.");
        }
        if (!password.equals(confirmPassword)) {
            return new ValidationResult(false, "Mật khẩu xác nhận không khớp với mật khẩu đã nhập.");
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
