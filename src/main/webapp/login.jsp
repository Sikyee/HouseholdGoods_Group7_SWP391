<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Login - Household Goods</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                background: linear-gradient(to right, #e8d4b4, #f9f3ea);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                font-family: 'Segoe UI', sans-serif;
            }
            .login-box {
                background-color: #fff8f2;
                padding: 40px 30px;
                border-radius: 12px;
                box-shadow: 0px 8px 20px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 420px;
            }
            .login-title {
                font-size: 28px;
                font-weight: bold;
                color: #6e4b3a;
                margin-bottom: 20px;
            }
            .btn-login {
                background-color: #a67c52;
                color: white;
                border: none;
            }
            .btn-login:hover {
                background-color: #8b6744;
            }
            .form-label {
                font-weight: 500;
                color: #5e412f;
            }
            .icon {
                font-size: 40px;
                color: #a67c52;
            }
            .forgot-password-link {
                color: #a67c52;
                text-decoration: none;
                font-size: 0.9em;
                transition: all 0.3s ease;
            }
            .forgot-password-link:hover {
                color: #8b6744;
                text-decoration: underline;
            }
            .password-input-group {
                position: relative;
            }
            .password-toggle {
                position: absolute;
                right: 10px;
                top: 50%;
                transform: translateY(-50%);
                background: none;
                border: none;
                color: #a67c52;
                cursor: pointer;
                padding: 5px;
            }
            .password-toggle:hover {
                color: #8b6744;
            }
            .divider {
                border-top: 1px solid #e0d0c0;
                margin: 20px 0 15px 0;
            }
        </style>
    </head>
    <body>
        <div class="login-box">
            <div class="text-center">
                <div class="icon mb-2">🛋️</div>
                <div class="login-title">Household Goods Login</div>
            </div>

            <%-- Display success message from session (e.g. after registration or password reset) --%>
            <%
                String successMsg = (String) session.getAttribute("success");
                if (successMsg != null) {
            %>
            <div class="alert alert-success text-center">
                <i class="fas fa-check-circle me-2"></i>
                <%= successMsg%>
            </div>
            <%
                    session.removeAttribute("success");
                }
            %>

            <%-- Display success message from request (e.g. from forgot password flow) --%>
            <%
                String requestSuccess = (String) request.getAttribute("success");
                if (requestSuccess != null) {
            %>
            <div class="alert alert-success text-center">
                <i class="fas fa-check-circle me-2"></i>
                <%= requestSuccess%>
            </div>
            <%
                }
            %>

            <form action="login" method="post">
                <div class="mb-3">
                    <label class="form-label">
                        <i class="fas fa-user me-2"></i>Username
                    </label>
                    <input type="text" name="username" class="form-control" required 
                           placeholder="Enter your username" value="${param.username}"/>
                </div>

                <div class="mb-2">
                    <label class="form-label">
                        <i class="fas fa-lock me-2"></i>Password
                    </label>
                    <div class="password-input-group">
                        <input type="password" name="password" id="password" class="form-control" 
                               required placeholder="Enter your password" style="padding-right: 45px;"/>
                        <button type="button" class="password-toggle" onclick="togglePassword()" title="Show/Hide Password">
                            <i class="fas fa-eye" id="password-toggle-icon"></i>
                        </button>
                    </div>
                </div>

                <!-- Forgot Password Link -->
                <div class="text-end mb-3">
                    <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password-link">
                        <i class="fas fa-key me-1"></i>Forgot Password?
                    </a>
                </div>

                <div class="d-grid mb-3">
                    <button type="submit" class="btn btn-login">
                        <i class="fas fa-sign-in-alt me-2"></i>Login
                    </button>
                </div>

                <%-- Display login error if exists --%>
                <%
                    String err = (String) request.getAttribute("error");
                    if (err != null) {
                %>
                <div class="alert alert-danger text-center">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    <%= err%>
                </div>
                <%
                    }
                %>

                <div class="divider"></div>

                <div class="text-center mt-3">
                    <small>Don't have an account? 
                        <a href="${pageContext.request.contextPath}/register" style="color: #a67c52; font-weight: 500;">
                            <i class="fas fa-user-plus me-1"></i>Register
                        </a>
                    </small>
                </div>

                <div class="text-center mt-3">
                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-sm">⬅ Back to Home</a>
                    </div>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                            // Toggle password visibility
                            function togglePassword() {
                                const passwordField = document.getElementById('password');
                                const toggleIcon = document.getElementById('password-toggle-icon');

                                if (passwordField.type === 'password') {
                                    passwordField.type = 'text';
                                    toggleIcon.className = 'fas fa-eye-slash';
                                } else {
                                    passwordField.type = 'password';
                                    toggleIcon.className = 'fas fa-eye';
                                }
                            }

                            // Auto-dismiss success messages after 5 seconds
                            window.addEventListener('DOMContentLoaded', function () {
                                const successAlerts = document.querySelectorAll('.alert-success');
                                successAlerts.forEach(function (alert) {
                                    setTimeout(function () {
                                        alert.style.transition = 'opacity 0.5s ease-out';
                                        alert.style.opacity = '0';
                                        setTimeout(function () {
                                            alert.remove();
                                        }, 500);
                                    }, 5000);
                                });
                            });
        </script>
    </body>
</html>