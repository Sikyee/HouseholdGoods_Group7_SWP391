<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Register - Household Goods</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                background: linear-gradient(to right, #e8d4b4, #f9f3ea);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                font-family: 'Segoe UI', sans-serif;
                padding: 20px 0;
            }
            .register-box {
                background-color: #fff8f2;
                padding: 40px 30px;
                border-radius: 12px;
                box-shadow: 0px 8px 20px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 480px;
            }
            .register-title {
                font-size: 28px;
                font-weight: bold;
                color: #6e4b3a;
                margin-bottom: 20px;
            }
            .btn-register {
                background-color: #a67c52;
                color: white;
                border: none;
                padding: 12px;
                font-weight: 600;
                transition: all 0.3s ease;
            }
            .btn-register:hover {
                background-color: #8b6744;
                color: white;
            }
            .btn-register:disabled {
                background-color: #6c757d;
                color: white;
            }
            .form-label {
                font-weight: 500;
                color: #5e412f;
                margin-bottom: 8px;
            }
            .form-control {
                border: 1px solid #d4b896;
                border-radius: 6px;
                padding: 12px 15px;
                font-size: 16px;
                transition: all 0.3s ease;
                background-color: #fefcfa;
            }
            .form-control:focus {
                border-color: #a67c52;
                box-shadow: 0 0 0 0.2rem rgba(166, 124, 82, 0.25);
                background-color: white;
            }
            .icon {
                font-size: 40px;
                color: #a67c52;
            }
            .password-toggle {
                position: absolute;
                right: 15px;
                top: 50%;
                transform: translateY(-50%);
                cursor: pointer;
                color: #6c757d;
                z-index: 10;
            }
            .password-toggle:hover {
                color: #a67c52;
            }
            .password-strength {
                margin-top: 8px;
                font-size: 12px;
            }
            .strength-bar {
                height: 4px;
                border-radius: 2px;
                background: #e9ecef;
                margin: 5px 0;
                overflow: hidden;
            }
            .strength-fill {
                height: 100%;
                width: 0%;
                transition: all 0.3s ease;
                border-radius: 2px;
            }
            .strength-weak {
                background: #dc3545;
            }
            .strength-medium {
                background: #ffc107;
            }
            .strength-strong {
                background: #28a745;
            }
            .form-text {
                font-size: 12px;
                color: #8b6744;
                margin-top: 5px;
            }
            .loading-spinner {
                display: none;
                margin-right: 10px;
            }
            .alert {
                border: none;
                border-radius: 8px;
                padding: 12px;
                margin-bottom: 15px;
                font-weight: 500;
            }
            .form-group {
                margin-bottom: 20px;
                position: relative;
            }
            .password-field .form-control {
                padding-right: 75px;
            }
            .password-match-icon {
                position: absolute;
                right: 45px;
                top: 50%;
                transform: translateY(-50%);
                z-index: 5;
                display: none;
                font-size: 14px;
            }
            .password-match-icon.show {
                display: block;
            }
            .password-match-icon.match {
                color: #28a745;
            }
            .password-match-icon.no-match {
                color: #dc3545;
            }
            .password-match-feedback {
                font-size: 12px;
                margin-top: 5px;
                min-height: 16px;
            }
            .password-match-feedback.match {
                color: #28a745;
            }
            .password-match-feedback.no-match {
                color: #dc3545;
            }
            .password-field .password-toggle {
                right: 75px;
            }
            @media (max-width: 576px) {
                .register-box {
                    margin: 10px;
                    padding: 30px 20px;
                }
            }
        </style>
    </head>
    <body>
        <div class="register-box">
            <div class="text-center">
                <div class="icon mb-2">🛋️</div>
                <div class="register-title">Household Goods Register</div>
            </div>

            <!-- Display Messages -->
            <% String error = (String) request.getAttribute("error");
                String success = (String) request.getAttribute("success");
                if (error != null) {%>
            <div class="alert alert-danger text-center">
                <%= error%>
            </div>
            <% }
                if (success != null) {%>
            <div class="alert alert-success text-center">
                <%= success%>
            </div>
            <% }%>

            <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post">
                <input type="hidden" name="action" value="register">

                <!-- Full Name -->
                <div class="form-group">
                    <label class="form-label" for="fullName">Full Name</label>
                    <input type="text" 
                           class="form-control" 
                           id="fullName" 
                           name="fullName" 
                           value="${fullName != null ? fullName : ''}"
                           required 
                           minlength="2" 
                           maxlength="50"
                           placeholder="Enter your full name">
                    <div class="form-text">Enter your full name (2-50 characters)</div>
                </div>

                <!-- Username -->
                <div class="form-group">
                    <label class="form-label" for="username">Username</label>
                    <input type="text" 
                           class="form-control" 
                           id="username" 
                           name="username" 
                           value="${username != null ? username : ''}"
                           required 
                           pattern="^[a-zA-Z0-9_]{3,20}$"
                           minlength="3" 
                           maxlength="20"
                           placeholder="Choose your username">
                    <div class="form-text">3-20 characters, letters, numbers and underscore only</div>
                </div>

                <!-- Email -->
                <div class="form-group">
                    <label class="form-label" for="email">Email</label>
                    <input type="email" 
                           class="form-control" 
                           id="email" 
                           name="email" 
                           value="${email != null ? email : ''}"
                           required
                           placeholder="Enter your email address">
                    <div class="form-text">We'll send verification code to this email</div>
                </div>

                <!-- Password -->
                <div class="form-group password-field">
                    <label class="form-label" for="password">Password</label>
                    <div class="position-relative">
                        <input type="password" 
                               class="form-control" 
                               id="password" 
                               name="password" 
                               required 
                               minlength="8"
                               placeholder="Enter your password">
                        <i class="fas fa-eye password-toggle" id="togglePassword"></i>
                    </div>
                    <div class="password-strength">
                        <div class="strength-bar">
                            <div class="strength-fill" id="strengthBar"></div>
                        </div>
                        <small id="strengthText">Enter password to check strength</small>
                    </div>
                    <div class="form-text">At least 8 characters with uppercase, lowercase, number and special character</div>
                </div>

                <!-- Confirm Password -->
                <div class="form-group password-field">
                    <label class="form-label" for="confirmPassword">Confirm Password</label>
                    <div class="position-relative">
                        <input type="password" 
                               class="form-control" 
                               id="confirmPassword" 
                               name="confirmPassword" 
                               required
                               placeholder="Confirm your password">
                        <i class="fas fa-eye password-toggle" id="toggleConfirmPassword"></i>
                        <i class="fas password-match-icon" id="passwordMatchIcon"></i>
                    </div>
                    <div id="passwordMatchFeedback" class="password-match-feedback"></div>
                </div>

                <!-- Submit Button -->
                <div class="d-grid mb-3">
                    <button type="submit" class="btn btn-register" id="submitBtn">
                        <span class="loading-spinner">
                            <i class="fas fa-spinner fa-spin"></i>
                        </span>
                        <span class="btn-text">Register</span>
                    </button>
                </div>

                <div class="text-center mt-3">
                    <small>Already have an account? 
                        <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
                    </small>
                </div>
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-sm">⬅ Back to Home</a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const password = document.getElementById('password');
                const confirmPassword = document.getElementById('confirmPassword');
                const strengthBar = document.getElementById('strengthBar');
                const strengthText = document.getElementById('strengthText');
                const submitBtn = document.getElementById('submitBtn');
                const btnText = document.querySelector('.btn-text');
                const loadingSpinner = document.querySelector('.loading-spinner');
                const passwordMatchIcon = document.getElementById('passwordMatchIcon');
                const passwordMatchFeedback = document.getElementById('passwordMatchFeedback');

                // Password visibility toggle
                const togglePassword = document.getElementById('togglePassword');
                const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');

                togglePassword.addEventListener('click', function () {
                    const type = password.type === 'password' ? 'text' : 'password';
                    password.type = type;
                    this.classList.toggle('fa-eye');
                    this.classList.toggle('fa-eye-slash');
                });

                toggleConfirmPassword.addEventListener('click', function () {
                    const type = confirmPassword.type === 'password' ? 'text' : 'password';
                    confirmPassword.type = type;
                    this.classList.toggle('fa-eye');
                    this.classList.toggle('fa-eye-slash');
                });

                // Password strength checker (only visual feedback, no validation)
                password.addEventListener('input', function () {
                    const value = this.value;
                    const strength = checkPasswordStrength(value);
                    updateStrengthBar(strength);
                    // Check password match when password changes
                    checkPasswordMatch();
                });

                // Check password match when confirm password changes
                confirmPassword.addEventListener('input', function () {
                    checkPasswordMatch();
                });

                function checkPasswordMatch() {
                    const passwordValue = password.value;
                    const confirmPasswordValue = confirmPassword.value;

                    // Clear previous state
                    passwordMatchIcon.className = 'fas password-match-icon';
                    passwordMatchFeedback.className = 'password-match-feedback';
                    passwordMatchFeedback.textContent = '';

                    // Only show feedback if confirm password has content
                    if (confirmPasswordValue.length > 0) {
                        passwordMatchIcon.classList.add('show');

                        if (passwordValue === confirmPasswordValue) {
                            // Passwords match
                            passwordMatchIcon.classList.add('fa-check-circle', 'match');
                            passwordMatchFeedback.classList.add('match');
                            passwordMatchFeedback.textContent = 'Passwords match!';
                        } else {
                            // Passwords don't match
                            passwordMatchIcon.classList.add('fa-times-circle', 'no-match');
                            passwordMatchFeedback.classList.add('no-match');
                            passwordMatchFeedback.textContent = 'Passwords do not match';
                        }
                    } else {
                        // Hide icon when confirm password is empty
                        passwordMatchIcon.classList.remove('show');
                    }
                }

                function checkPasswordStrength(password) {
                    let score = 0;
                    if (password.length >= 8)
                        score++;
                    if (/[a-z]/.test(password))
                        score++;
                    if (/[A-Z]/.test(password))
                        score++;
                    if (/[0-9]/.test(password))
                        score++;
                    if (/[^A-Za-z0-9]/.test(password))
                        score++;
                    return Math.min(score, 5);
                }

                function updateStrengthBar(strength) {
                    const percentage = (strength / 5) * 100;
                    strengthBar.style.width = percentage + '%';

                    if (strength < 3) {
                        strengthBar.className = 'strength-fill strength-weak';
                        strengthText.textContent = 'Weak password';
                        strengthText.style.color = '#dc3545';
                    } else if (strength < 5) {
                        strengthBar.className = 'strength-fill strength-medium';
                        strengthText.textContent = 'Medium password';
                        strengthText.style.color = '#ffc107';
                    } else {
                        strengthBar.className = 'strength-fill strength-strong';
                        strengthText.textContent = 'Strong password';
                        strengthText.style.color = '#28a745';
                    }
                }

                // Simple form submission with loading state
                document.getElementById('registerForm').addEventListener('submit', function () {
                    submitBtn.disabled = true;
                    loadingSpinner.style.display = 'inline-block';
                    btnText.textContent = 'Processing...';
                });
            });
        </script>
    </body>
</html>