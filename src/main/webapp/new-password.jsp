<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Set New Password</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                background: linear-gradient(to right, #e8d4b4, #f9f3ea);
                min-height: 100vh;
                margin: 0;
                padding: 0;
                font-family: 'Segoe UI', sans-serif;
            }
            .password-container {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 2rem 15px;
            }
            .card-wrapper {
                max-width: 450px;
                width: 100%;
            }
            .card {
                background-color: #fff8f2;
                border: none;
                border-radius: 12px;
                box-shadow: 0px 8px 20px rgba(0,0,0,0.1);
                padding: 40px 30px;
            }
            .card-header {
                background: transparent;
                border: none;
                padding: 0 0 20px 0;
                text-align: center;
            }
            .card-body {
                padding: 0;
            }
            .form-control {
                border-radius: 6px;
                border: 1px solid #ddd;
                padding: 12px 16px;
                padding-right: 45px;
            }
            .form-control:focus {
                border-color: #a67c52;
                box-shadow: 0 0 0 0.2rem rgba(166, 124, 82, 0.25);
            }
            .btn-primary {
                background-color: #a67c52;
                border: none;
                border-radius: 6px;
                padding: 12px;
                font-weight: 600;
                color: white;
            }
            .btn-primary:hover {
                background-color: #8b6744;
            }
            .btn-secondary {
                background-color: #6c757d;
                border: none;
                border-radius: 6px;
                padding: 12px;
                color: white;
            }
            .btn-secondary:hover {
                background-color: #545b62;
            }
            .alert {
                border-radius: 6px;
                margin-bottom: 1.5rem;
            }
            .password-icon {
                color: #a67c52;
                margin-bottom: 1rem;
                font-size: 40px;
            }
            .form-label {
                font-weight: 500;
                color: #5e412f;
            }
            .page-title {
                font-size: 28px;
                font-weight: bold;
                color: #6e4b3a;
                margin-bottom: 10px;
            }
            .page-subtitle {
                color: #8b7355;
                margin-bottom: 20px;
            }
            .password-requirements {
                background-color: #f8f9fa;
                border-left: 4px solid #a67c52;
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 6px;
            }
            .password-requirements ul {
                margin: 0;
                padding-left: 20px;
            }
            .password-requirements li {
                color: #6e4b3a;
                margin-bottom: 5px;
                font-size: 14px;
            }
            .input-group {
                position: relative;
            }
            .password-toggle {
                position: absolute;
                right: 12px;
                top: 50%;
                transform: translateY(-50%);
                border: none;
                background: none;
                color: #a67c52;
                cursor: pointer;
                z-index: 10;
                padding: 0;
                width: 20px;
                height: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            .password-toggle:hover {
                color: #8b6744;
            }
            .password-strength {
                margin-top: 8px;
                font-size: 12px;
            }
            .strength-weak {
                color: #dc3545;
            }
            .strength-medium {
                color: #ffc107;
            }
            .strength-strong {
                color: #28a745;
            }
        </style>
    </head>
    <body>
        <div class="password-container">
            <div class="card-wrapper">
                <div class="card">
                    <div class="card-header">
                        <div class="password-icon">🔐</div>
                        <h4 class="page-title">Set New Password</h4>
                        <p class="page-subtitle">Create a strong password for your account</p>
                    </div>
                    <div class="card-body">
                        <!-- Password Requirements -->
                        <div class="password-requirements">
                            <small style="color: #5e412f; font-weight: 600;">
                                <i class="fas fa-info-circle me-2"></i>Password Requirements:
                            </small>
                            <ul>
                                <li>At least 8 characters long (maximum 50)</li>
                                <li>At least 1 lowercase letter (a-z)</li>
                                <li>At least 1 uppercase letter (A-Z)</li>
                                <li>At least 1 number (0-9)</li>
                                <li>At least 1 special character (@$!%*?&)</li>
                                <li>No spaces allowed</li>
                                <li>No more than 2 consecutive identical characters</li>
                                <li>No sequential characters (abc, 123, etc.)</li>
                            </ul>
                        </div>

                        <!-- Display error message -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                ${error}
                            </div>
                        </c:if>

                        <!-- Display success message -->
                        <c:if test="${not empty success}">
                            <div class="alert alert-success" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                ${success}
                            </div>
                        </c:if>

                        <form action="forgot-password" method="post" id="newPasswordForm">
                            <input type="hidden" name="action" value="reset">

                            <div class="mb-3">
                                <label for="newPassword" class="form-label">
                                    <i class="fas fa-lock me-2"></i>New Password
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" 
                                           required placeholder="Enter your new password" autocomplete="new-password">
                                    <button type="button" class="password-toggle" id="toggleNew" title="Show/Hide Password">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                </div>
                                <div class="password-strength" id="passwordStrength"></div>
                            </div>

                            <div class="mb-4">
                                <label for="confirmPassword" class="form-label">
                                    <i class="fas fa-lock me-2"></i>Confirm New Password
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                           required placeholder="Confirm your new password" autocomplete="new-password">
                                    <button type="button" class="password-toggle" id="toggleConfirm" title="Show/Hide Password">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                </div>
                                <div class="form-text" id="passwordMatch"></div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary" id="submitBtn">
                                    <i class="fas fa-save me-2"></i>
                                    Update Password
                                </button>
                                <a href="login.jsp" class="btn btn-secondary">
                                    <i class="fas fa-times me-2"></i>
                                    Cancel
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Security notice -->
                <div class="text-center mt-3">
                    <small style="color: #8b7355;">
                        <i class="fas fa-shield-alt me-2"></i>
                        Choose a strong password to keep your account secure
                    </small>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Password toggle functionality
            function togglePasswordVisibility(inputId, toggleId) {
                const input = document.getElementById(inputId);
                const toggle = document.getElementById(toggleId);
                const icon = toggle.querySelector('i');

                toggle.addEventListener('click', function () {
                    if (input.type === 'password') {
                        input.type = 'text';
                        icon.classList.remove('fa-eye');
                        icon.classList.add('fa-eye-slash');
                    } else {
                        input.type = 'password';
                        icon.classList.remove('fa-eye-slash');
                        icon.classList.add('fa-eye');
                    }
                });
            }

            togglePasswordVisibility('newPassword', 'toggleNew');
            togglePasswordVisibility('confirmPassword', 'toggleConfirm');

            // Password strength checker
            function checkPasswordStrength(password) {
                const strengthDiv = document.getElementById('passwordStrength');
                let strength = 0;
                let feedback = '';
                const errors = [];

                // Length check
                if (password.length >= 8)
                    strength++;
                else if (password.length > 0)
                    errors.push('Too short');

                // Character requirements
                if (/[a-z]/.test(password))
                    strength++;
                else if (password.length > 0)
                    errors.push('Need lowercase');

                if (/[A-Z]/.test(password))
                    strength++;
                else if (password.length > 0)
                    errors.push('Need uppercase');

                if (/\d/.test(password))
                    strength++;
                else if (password.length > 0)
                    errors.push('Need number');

                if (/[@$!%*?&]/.test(password))
                    strength++;
                else if (password.length > 0)
                    errors.push('Need special char');

                // Additional validations
                if (password.includes(' ') && password.length > 0)
                    errors.push('No spaces');

                if (errors.length > 0) {
                    strengthDiv.innerHTML = `<span class="strength-weak"><i class="fas fa-exclamation-triangle me-1"></i>Weak: ${errors.join(', ')}</span>`;
                } else {
                    switch (strength) {
                        case 0:
                        case 1:
                        case 2:
                            strengthDiv.innerHTML = '<span class="strength-weak"><i class="fas fa-exclamation-triangle me-1"></i>Weak password</span>';
                            break;
                        case 3:
                        case 4:
                            strengthDiv.innerHTML = '<span class="strength-medium"><i class="fas fa-exclamation me-1"></i>Medium password</span>';
                            break;
                        case 5:
                            strengthDiv.innerHTML = '<span class="strength-strong"><i class="fas fa-check me-1"></i>Strong password</span>';
                            break;
                    }
                }
            }

            // Password match checker
            function checkPasswordMatch() {
                const password = document.getElementById('newPassword').value;
                const confirm = document.getElementById('confirmPassword').value;
                const matchDiv = document.getElementById('passwordMatch');

                if (confirm === '') {
                    matchDiv.innerHTML = '';
                    return;
                }

                if (password === confirm) {
                    matchDiv.innerHTML = '<small class="text-success"><i class="fas fa-check me-1"></i>Passwords match</small>';
                } else {
                    matchDiv.innerHTML = '<small class="text-danger"><i class="fas fa-times me-1"></i>Passwords do not match</small>';
                }
            }

            // Event listeners
            document.getElementById('newPassword').addEventListener('input', function () {
                checkPasswordStrength(this.value);
                checkPasswordMatch();
            });

            document.getElementById('confirmPassword').addEventListener('input', checkPasswordMatch);

            // Form validation
            document.getElementById('newPasswordForm').addEventListener('submit', function (e) {
                const newPassword = document.getElementById('newPassword').value;
                const confirmPassword = document.getElementById('confirmPassword').value;

                if (!newPassword || !confirmPassword) {
                    e.preventDefault();
                    alert('Please fill in both password fields.');
                    return;
                }

                if (newPassword !== confirmPassword) {
                    e.preventDefault();
                    alert('Passwords do not match.');
                    return;
                }

                // Strict validation matching server-side
                if (newPassword.length < 8) {
                    e.preventDefault();
                    alert('Password must be at least 8 characters long.');
                    return;
                }

                if (newPassword.length > 50) {
                    e.preventDefault();
                    alert('Password cannot exceed 50 characters.');
                    return;
                }

                if (!/[a-z]/.test(newPassword)) {
                    e.preventDefault();
                    alert('Password must contain at least 1 lowercase letter.');
                    return;
                }

                if (!/[A-Z]/.test(newPassword)) {
                    e.preventDefault();
                    alert('Password must contain at least 1 uppercase letter.');
                    return;
                }

                if (!/\d/.test(newPassword)) {
                    e.preventDefault();
                    alert('Password must contain at least 1 number.');
                    return;
                }

                if (!/[@$!%*?&]/.test(newPassword)) {
                    e.preventDefault();
                    alert('Password must contain at least 1 special character (@$!%*?&).');
                    return;
                }

                if (newPassword.includes(' ')) {
                    e.preventDefault();
                    alert('Password cannot contain spaces.');
                    return;
                }

                // Check consecutive characters
                let hasConsecutive = false;
                let count = 1;
                let prev = '';
                for (let i = 0; i < newPassword.length; i++) {
                    if (newPassword[i] === prev) {
                        count++;
                        if (count >= 3) {
                            hasConsecutive = true;
                            break;
                        }
                    } else {
                        count = 1;
                        prev = newPassword[i];
                    }
                }
                if (hasConsecutive) {
                    e.preventDefault();
                    alert('Password cannot contain more than 2 consecutive identical characters.');
                    return;
                }

                // Check sequential characters
                const lower = newPassword.toLowerCase();
                for (let i = 0; i < lower.length - 2; i++) {
                    const c1 = lower.charCodeAt(i);
                    const c2 = lower.charCodeAt(i + 1);
                    const c3 = lower.charCodeAt(i + 2);

                    if ((c2 === c1 + 1) && (c3 === c2 + 1)) {
                        e.preventDefault();
                        alert('Password cannot contain sequential characters (abc, 123, ...).');
                        return;
                    }
                }

                // Show loading state
                const submitBtn = document.getElementById('submitBtn');
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Updating Password...';
                submitBtn.disabled = true;
            });

            // Auto-focus on password field
            document.getElementById('newPassword').focus();
        </script>
    </body>
</html>