<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Change Password</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: #f5f5f5;
                min-height: 100vh;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 20px;
            }

            .change-password-container {
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                border: 1px solid #e0e0e0;
                overflow: hidden;
                width: 100%;
                max-width: 450px;
            }

            .header {
                background: #333;
                color: white;
                text-align: center;
                padding: 25px 20px;
                border-bottom: 1px solid #ddd;
            }

            .header h1 {
                font-size: 22px;
                margin-bottom: 5px;
                font-weight: 500;
            }

            .header p {
                opacity: 0.8;
                font-size: 14px;
            }

            .form-container {
                padding: 30px;
            }

            .form-group {
                margin-bottom: 20px;
                position: relative;
            }

            .form-group label {
                display: block;
                margin-bottom: 6px;
                font-weight: 500;
                color: #333;
                font-size: 14px;
            }

            .input-wrapper {
                position: relative;
            }

            .form-control {
                width: 100%;
                padding: 12px 16px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
                transition: border-color 0.2s, box-shadow 0.2s;
                background: white;
            }

            .form-control:focus {
                outline: none;
                border-color: #333;
                box-shadow: 0 0 0 2px rgba(51, 51, 51, 0.1);
            }

            .form-control.error {
                border-color: #d32f2f !important;
                background-color: #fef7f7 !important;
                box-shadow: 0 0 0 2px rgba(211, 47, 47, 0.1) !important;
            }

            .form-control.success {
                border-color: #2e7d32;
                background-color: #f1f8e9;
            }

            .password-input {
                padding-right: 45px;
            }

            .toggle-password {
                position: absolute;
                right: 12px;
                top: 50%;
                transform: translateY(-50%);
                background: none;
                border: none;
                color: #666;
                cursor: pointer;
                font-size: 16px;
                transition: color 0.2s;
                z-index: 10;
            }

            .toggle-password:hover {
                color: #333;
            }

            /* Enhanced error message styles */
            .error-message {
                color: #d32f2f;
                font-size: 12px;
                margin-top: 4px;
                display: none;
                align-items: center;
                background-color: #fef7f7;
                padding: 5px 8px;
                border-radius: 3px;
                border-left: 3px solid #d32f2f;
                animation: slideDown 0.3s ease;
            }

            .error-message.show {
                display: flex;
            }

            .error-message i {
                margin-right: 4px;
            }

            /* Animation cho error message */
            @keyframes slideDown {
                from {
                    opacity: 0;
                    transform: translateY(-10px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* Success message styles */
            .success-message {
                color: #2e7d32;
                font-size: 14px;
                margin-bottom: 15px;
                display: flex;
                align-items: center;
                background: #e8f5e8;
                padding: 10px 12px;
                border-radius: 4px;
                border-left: 3px solid #2e7d32;
                animation: slideDown 0.3s ease;
            }

            .success-message i {
                margin-right: 6px;
            }

            .success-message.hide {
                animation: slideUp 0.3s ease forwards;
            }

            @keyframes slideUp {
                to {
                    opacity: 0;
                    transform: translateY(-10px);
                }
            }

            .alert-message {
                color: #d32f2f;
                font-size: 14px;
                margin-bottom: 15px;
                display: flex;
                align-items: center;
                background: #fef7f7;
                padding: 10px 12px;
                border-radius: 4px;
                border-left: 3px solid #d32f2f;
            }

            .alert-message i {
                margin-right: 6px;
            }

            .password-requirements {
                margin-top: 8px;
                padding: 12px;
                background: #f9f9f9;
                border-radius: 4px;
                border: 1px solid #e0e0e0;
            }

            .requirement {
                font-size: 11px;
                color: #666;
                margin-bottom: 3px;
                display: flex;
                align-items: center;
                transition: color 0.3s ease;
            }

            .requirement i {
                margin-right: 6px;
                width: 10px;
                transition: color 0.3s ease;
            }

            .requirement.valid {
                color: #2e7d32;
            }

            .requirement.invalid {
                color: #d32f2f;
            }

            .btn {
                width: 100%;
                padding: 12px;
                border: none;
                border-radius: 4px;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.2s ease;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .btn:disabled {
                opacity: 0.6;
                cursor: not-allowed;
            }

            .btn-primary {
                background: #333;
                color: white;
                border: 1px solid #333;
            }

            .btn-primary:hover:not(:disabled) {
                background: #222;
                border-color: #222;
            }

            .btn-secondary {
                background: white;
                color: #666;
                border: 1px solid #ddd;
                margin-top: 8px;
            }

            .btn-secondary:hover {
                background: #f5f5f5;
                color: #333;
            }

            .btn i {
                margin-right: 6px;
            }

            .loading {
                display: none;
            }

            .btn.loading .btn-text {
                display: none;
            }

            .btn.loading .loading {
                display: inline-block;
            }

            .back-link {
                text-align: center;
                margin-top: 15px;
            }

            .back-link a {
                color: #666;
                text-decoration: none;
                font-size: 13px;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: color 0.2s;
            }

            .back-link a:hover {
                color: #333;
                text-decoration: underline;
            }

            .back-link a i {
                margin-right: 4px;
            }

            /* Strength meter styles */
            .strength-meter {
                margin-top: 6px;
                height: 3px;
                border-radius: 2px;
                background: #e0e0e0;
                overflow: hidden;
                position: relative;
            }

            .strength-fill {
                height: 100%;
                width: 0%;
                transition: all 0.3s ease;
                border-radius: 2px;
                position: absolute;
                top: 0;
                left: 0;
            }

            .strength-weak {
                background: #d32f2f;
                width: 25%;
            }
            .strength-fair {
                background: #f57c00;
                width: 50%;
            }
            .strength-good {
                background: #fbc02d;
                width: 75%;
            }
            .strength-strong {
                background: #388e3c;
                width: 100%;
            }

            /* Password match indicator */
            .match-indicator {
                margin-top: 4px;
                font-size: 12px;
                display: flex;
                align-items: center;
                padding: 4px 8px;
                border-radius: 3px;
            }

            .match-indicator.match {
                color: #2e7d32;
                background: #e8f5e8;
                border-left: 3px solid #2e7d32;
            }

            .match-indicator.no-match {
                color: #d32f2f;
                background: #fef7f7;
                border-left: 3px solid #d32f2f;
            }

            .match-indicator i {
                margin-right: 4px;
            }

            @media (max-width: 768px) {
                .form-container {
                    padding: 25px 20px;
                }

                .header {
                    padding: 20px;
                }

                .header h1 {
                    font-size: 20px;
                }
            }

            /* Loading spinner */
            .spinner {
                display: inline-block;
                width: 12px;
                height: 12px;
                border: 2px solid #fff;
                border-radius: 50%;
                border-top-color: transparent;
                animation: spin 1s linear infinite;
                margin-right: 5px;
            }

            @keyframes spin {
                to {
                    transform: rotate(360deg);
                }
            }
        </style>
    </head>
    <body>
        <div class="change-password-container">
            <div class="header">
                <h1><i class="fas fa-key"></i> Change Password</h1>
                <p>Update your password to secure your account</p>
            </div>

            <div class="form-container">
                <!-- Success Message -->
                <c:if test="${result.success == true}">
                    <div class="success-message" id="successMessage">
                        <i class="fas fa-check-circle"></i>
                        ${result.message}
                    </div>
                </c:if>

                <!-- Error Message -->
                <c:if test="${result.success == false}">
                    <div class="alert-message" id="alertMessage">
                        <i class="fas fa-exclamation-triangle"></i>
                        ${result.message}
                    </div>
                </c:if>

                <form id="changePasswordForm" method="post" action="changePassword">
                    <!-- Current Password -->
                    <div class="form-group">
                        <label for="currentPassword">Current Password *</label>
                        <div class="input-wrapper">
                            <input type="password" id="currentPassword" name="currentPassword" 
                                   class="form-control password-input" placeholder="Enter current password" required>
                            <button type="button" class="toggle-password" onclick="togglePassword('currentPassword')">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                        <div class="error-message" id="currentPasswordError">
                            <i class="fas fa-times-circle"></i>
                            <span>Current password is required</span>
                        </div>
                        <c:if test="${result.errors.currentPassword != null}">
                            <div class="error-message show">
                                <i class="fas fa-times-circle"></i>
                                ${result.errors.currentPassword}
                            </div>
                        </c:if>
                    </div>

                    <!-- New Password -->
                    <div class="form-group">
                        <label for="newPassword">New Password *</label>
                        <div class="input-wrapper">
                            <input type="password" id="newPassword" name="newPassword" 
                                   class="form-control password-input" placeholder="Enter new password" 
                                   required>
                            <button type="button" class="toggle-password" onclick="togglePassword('newPassword')">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>

                        <!-- Password Strength Meter -->
                        <div class="strength-meter">
                            <div id="strengthFill" class="strength-fill"></div>
                        </div>

                        <!-- Password Requirements -->
                        <div class="password-requirements">
                            <div class="requirement" id="req-length">
                                <i class="fas fa-times"></i>
                                At least 8 characters
                            </div>
                            <div class="requirement" id="req-uppercase">
                                <i class="fas fa-times"></i>
                                Contains uppercase letter
                            </div>
                            <div class="requirement" id="req-lowercase">
                                <i class="fas fa-times"></i>
                                Contains lowercase letter
                            </div>
                            <div class="requirement" id="req-number">
                                <i class="fas fa-times"></i>
                                Contains number
                            </div>
                            <div class="requirement" id="req-special">
                                <i class="fas fa-times"></i>
                                Contains special character (!@#$%^&*...)
                            </div>
                        </div>

                        <div class="error-message" id="newPasswordError">
                            <i class="fas fa-times-circle"></i>
                            <span>New password is required</span>
                        </div>

                        <c:forEach var="error" items="${result.errors}">
                            <c:if test="${error.key == 'length' || error.key == 'uppercase' || error.key == 'lowercase' || 
                                          error.key == 'digit' || error.key == 'special' || error.key == 'whitespace'}">
                                  <div class="error-message show">
                                      <i class="fas fa-times-circle"></i>
                                      ${error.value}
                                  </div>
                            </c:if>
                        </c:forEach>
                    </div>

                    <!-- Confirm Password -->
                    <div class="form-group">
                        <label for="confirmPassword">Confirm New Password *</label>
                        <div class="input-wrapper">
                            <input type="password" id="confirmPassword" name="confirmPassword" 
                                   class="form-control password-input" placeholder="Re-enter new password" 
                                   required>
                            <button type="button" class="toggle-password" onclick="togglePassword('confirmPassword')">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                        <div id="passwordMatchMsg"></div>
                        <div class="error-message" id="confirmPasswordError">
                            <i class="fas fa-times-circle"></i>
                            <span>Please confirm your password</span>
                        </div>
                        <c:if test="${result.errors.confirmPassword != null}">
                            <div class="error-message show">
                                <i class="fas fa-times-circle"></i>
                                ${result.errors.confirmPassword}
                            </div>
                        </c:if>
                    </div>

                    <!-- Submit Button -->
                    <button type="submit" id="submitBtn" class="btn btn-primary">
                        <span class="btn-text">
                            <i class="fas fa-save"></i>
                            Change Password
                        </span>
                        <span class="loading">
                            <span class="spinner"></span>
                            Processing...
                        </span>
                    </button>

                    <!-- Cancel Button -->
                    <button type="button" class="btn btn-secondary" onclick="history.back()">
                        <i class="fas fa-times"></i>
                        Cancel
                    </button>
                </form>

                <div class="back-link">
                    <a href="profile">
                        <i class="fas fa-arrow-left"></i>
                        Back to Profile
                    </a>
                </div>
            </div>
        </div>

        <script>
            // Global variables
            let formSubmitted = false;

            // Function để clear tất cả error messages
            function clearAllErrors() {
                const errorMessages = document.querySelectorAll('.error-message');
                const errorFields = document.querySelectorAll('.form-control.error');

                errorMessages.forEach(msg => {
                    if (!msg.classList.contains('show')) {
                        msg.classList.remove('show');
                    }
                });
                errorFields.forEach(field => field.classList.remove('error'));
            }

            // Function để hiển thị lỗi cho field cụ thể
            function showFieldError(fieldId, errorId, message) {
                const field = document.getElementById(fieldId);
                const errorMsg = document.getElementById(errorId);

                if (field) {
                    field.classList.add('error');
                    field.classList.remove('success');
                }
                if (errorMsg && !errorMsg.classList.contains('show')) {
                    const span = errorMsg.querySelector('span');
                    if (span)
                        span.textContent = message;
                    errorMsg.classList.add('show');
                }
            }

            // Function để clear error cho field cụ thể
            function clearFieldError(fieldId, errorId) {
                const field = document.getElementById(fieldId);
                const errorMsg = document.getElementById(errorId);

                if (field) {
                    field.classList.remove('error');
                }
                if (errorMsg) {
                    errorMsg.classList.remove('show');
                }
            }

            // Function để set success state cho field
            function setFieldSuccess(fieldId) {
                const field = document.getElementById(fieldId);
                if (field) {
                    field.classList.remove('error');
                    field.classList.add('success');
                }
            }

            // Toggle password visibility
            function togglePassword(fieldId) {
                const field = document.getElementById(fieldId);
                const icon = field.nextElementSibling.querySelector('i');

                if (field.type === 'password') {
                    field.type = 'text';
                    icon.className = 'fas fa-eye-slash';
                } else {
                    field.type = 'password';
                    icon.className = 'fas fa-eye';
                }
            }

            // Enhanced password strength checker
            function checkPasswordStrength(password) {
                const requirements = {
                    'req-length': password.length >= 8,
                    'req-uppercase': /[A-Z]/.test(password),
                    'req-lowercase': /[a-z]/.test(password),
                    'req-number': /\d/.test(password),
                    'req-special': /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]/.test(password)
                };

                let strength = 0;
                for (const req in requirements) {
                    const element = document.getElementById(req);
                    const icon = element.querySelector('i');

                    if (requirements[req]) {
                        element.classList.add('valid');
                        element.classList.remove('invalid');
                        icon.className = 'fas fa-check';
                        strength++;
                    } else {
                        element.classList.add('invalid');
                        element.classList.remove('valid');
                        icon.className = 'fas fa-times';
                    }
                }

                // Update strength meter
                const strengthFill = document.getElementById('strengthFill');
                strengthFill.className = 'strength-fill';

                if (strength === 0) {
                    // No strength class
                } else if (strength <= 2) {
                    strengthFill.classList.add('strength-weak');
                } else if (strength <= 3) {
                    strengthFill.classList.add('strength-fair');
                } else if (strength <= 4) {
                    strengthFill.classList.add('strength-good');
                } else {
                    strengthFill.classList.add('strength-strong');
                }

                // Clear error if password meets all requirements
                if (strength === 5) {
                    clearFieldError('newPassword', 'newPasswordError');
                    setFieldSuccess('newPassword');
                }

                return strength === 5;
            }

            // FIXED: Enhanced password match checker - tránh trùng lặp thông báo
            function checkPasswordMatch() {
                const newPassword = document.getElementById('newPassword').value;
                const confirmPassword = document.getElementById('confirmPassword').value;
                const msgDiv = document.getElementById('passwordMatchMsg');

                // Clear existing messages first
                msgDiv.innerHTML = '';
                clearFieldError('confirmPassword', 'confirmPasswordError');

                if (confirmPassword.length > 0) {
                    if (newPassword === confirmPassword) {
                        msgDiv.innerHTML = '<div class="match-indicator match"><i class="fas fa-check-circle"></i>Passwords match</div>';
                        setFieldSuccess('confirmPassword');
                        return true;
                    } else {
                        msgDiv.innerHTML = '<div class="match-indicator no-match"><i class="fas fa-times-circle"></i>Passwords do not match</div>';
                        // CHỈ hiển thị error field styling, KHÔNG hiển thị error message để tránh trùng lặp
                        document.getElementById('confirmPassword').classList.add('error');
                        return false;
                    }
                } else {
                    return false;
                }
            }

            // FIXED: Form validation - tránh hiển thị thông báo trùng lặp
            function validateForm() {
                const currentPassword = document.getElementById('currentPassword').value.trim();
                const newPassword = document.getElementById('newPassword').value;
                const confirmPassword = document.getElementById('confirmPassword').value;

                let isValid = true;

                // Clear all previous client-side errors
                clearAllErrors();

                // Validate current password
                if (!currentPassword) {
                    showFieldError('currentPassword', 'currentPasswordError', 'Current password is required');
                    isValid = false;
                } else {
                    setFieldSuccess('currentPassword');
                }

                // Validate new password
                if (!newPassword) {
                    showFieldError('newPassword', 'newPasswordError', 'New password is required');
                    isValid = false;
                } else if (!checkPasswordStrength(newPassword)) {
                    showFieldError('newPassword', 'newPasswordError', 'Password does not meet requirements');
                    isValid = false;
                }

                // Validate confirm password
                if (!confirmPassword) {
                    showFieldError('confirmPassword', 'confirmPasswordError', 'Please confirm your password');
                    isValid = false;
                } else {
                    // Kiểm tra match nhưng không hiển thị error message nếu không match
                    // vì checkPasswordMatch() đã hiển thị trong match indicator
                    const passwordsMatch = checkPasswordMatch();
                    if (!passwordsMatch) {
                        isValid = false;
                        // Chỉ hiển thị error message khi submit form
                        if (confirmPassword.length > 0 && newPassword !== confirmPassword) {
                            showFieldError('confirmPassword', 'confirmPasswordError', 'Passwords do not match');
                        }
                    }
                }

                return isValid;
            }

            // Enhanced form submission
            document.getElementById('changePasswordForm').addEventListener('submit', function (e) {
                if (formSubmitted) {
                    e.preventDefault();
                    return false;
                }

                // Clear server messages before validation
                const serverMessages = document.querySelectorAll('.success-message, .alert-message');
                serverMessages.forEach(msg => msg.style.display = 'none');

                if (!validateForm()) {
                    e.preventDefault();

                    // Focus on first error field
                    const firstErrorField = document.querySelector('.form-control.error');
                    if (firstErrorField) {
                        firstErrorField.focus();
                        firstErrorField.scrollIntoView({behavior: 'smooth', block: 'center'});
                    }

                    return false;
                }

                // Show loading state
                formSubmitted = true;
                const submitBtn = document.getElementById('submitBtn');
                submitBtn.classList.add('loading');
                submitBtn.disabled = true;

                // Reset form state after timeout (in case of network issues)
                setTimeout(() => {
                    if (formSubmitted) {
                        submitBtn.classList.remove('loading');
                        submitBtn.disabled = false;
                        formSubmitted = false;
                    }
                }, 10000);
            });

            // Add event listeners for real-time validation
            document.addEventListener('DOMContentLoaded', function () {
                const currentPasswordField = document.getElementById('currentPassword');
                const newPasswordField = document.getElementById('newPassword');
                const confirmPasswordField = document.getElementById('confirmPassword');

                // Current password validation
                currentPasswordField.addEventListener('input', function () {
                    if (this.value.trim()) {
                        clearFieldError('currentPassword', 'currentPasswordError');
                        setFieldSuccess('currentPassword');
                    }
                });

                currentPasswordField.addEventListener('blur', function () {
                    if (!this.value.trim()) {
                        showFieldError('currentPassword', 'currentPasswordError', 'Current password is required');
                    }
                });

                // New password validation
                newPasswordField.addEventListener('input', function () {
                    const isStrong = checkPasswordStrength(this.value);
                    if (this.value && isStrong) {
                        clearFieldError('newPassword', 'newPasswordError');
                    }

                    // Re-check confirm password if it has value
                    const confirmValue = confirmPasswordField.value;
                    if (confirmValue) {
                        checkPasswordMatch();
                    }
                });

                newPasswordField.addEventListener('blur', function () {
                    if (!this.value) {
                        showFieldError('newPassword', 'newPasswordError', 'New password is required');
                    } else if (!checkPasswordStrength(this.value)) {
                        showFieldError('newPassword', 'newPasswordError', 'Password does not meet requirements');
                    }
                });

                // FIXED: Confirm password validation - chỉ hiển thị match indicator, không duplicate error
                confirmPasswordField.addEventListener('input', function () {
                    // Chỉ gọi checkPasswordMatch để cập nhật match indicator
                    checkPasswordMatch();
                });

                confirmPasswordField.addEventListener('blur', function () {
                    if (!this.value) {
                        showFieldError('confirmPassword', 'confirmPasswordError', 'Please confirm your password');
                        // Clear match indicator when empty
                        document.getElementById('passwordMatchMsg').innerHTML = '';
                    } else {
                        // Chỉ hiển thị match indicator, không hiển thị error message ở đây
                        checkPasswordMatch();
                    }
                });

                // Auto hide success message after 5 seconds
                const successMsg = document.getElementById('successMessage');
                if (successMsg) {
                    setTimeout(function () {
                        successMsg.classList.add('hide');
                        setTimeout(() => successMsg.style.display = 'none', 300);
                    }, 5000);
                }

                // Clear form if successful
            <c:if test="${result.success == true}">
                setTimeout(function () {
                    if (!formSubmitted) {
                        document.getElementById('changePasswordForm').reset();
                        clearAllErrors();

                        // Reset password requirements display
                        const requirements = document.querySelectorAll('.requirement');
                        requirements.forEach(req => {
                            req.classList.remove('valid', 'invalid');
                            req.querySelector('i').className = 'fas fa-times';
                        });

                        // Reset strength meter
                        const strengthFill = document.getElementById('strengthFill');
                        strengthFill.className = 'strength-fill';

                        // Clear password match message
                        document.getElementById('passwordMatchMsg').innerHTML = '';
                    }
                }, 3000);
            </c:if>

                console.log('Change Password form initialized successfully');
            });

            // Keyboard shortcuts
            document.addEventListener('keydown', function (e) {
                // Escape to clear current focus
                if (e.key === 'Escape') {
                    document.activeElement.blur();
                }

                // Enter to submit form (if valid)
                if (e.key === 'Enter' && e.ctrlKey) {
                    e.preventDefault();
                    const form = document.getElementById('changePasswordForm');
                    if (validateForm()) {
                        form.submit();
                    }
                }
            });
        </script>
    </body>
</html>