<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Forgot Password</title>
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
            .forgot-password-container {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 2rem 15px;
            }
            .card-wrapper {
                max-width: 420px;
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
            .forgot-password-icon {
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
        </style>
    </head>
    <body>
        <div class="forgot-password-container">
            <div class="card-wrapper">
                <div class="card">
                    <div class="card-header">
                        <div class="forgot-password-icon">🔑</div>
                        <h4 class="page-title">Forgot Password</h4>
                        <p class="page-subtitle">Enter your email to reset password</p>
                    </div>
                    <div class="card-body">
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

                        <form action="forgot-password" method="post" id="forgotPasswordForm">
                            <div class="mb-3">
                                <label for="email" class="form-label">
                                    <i class="fas fa-envelope me-2"></i>Email Address
                                </label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       required placeholder="Enter your email address"
                                       value="${param.email}">
                                <div class="form-text">
                                    We'll send you a verification code to reset your password.
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-paper-plane me-2"></i>
                                    Send Verification Code
                                </button>
                                <a href="login.jsp" class="btn btn-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>
                                    Back to Login
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Help text -->
                <div class="text-center mt-3">
                    <small style="color: #8b7355;">
                        Remember your password? <a href="login.jsp" style="color: #a67c52; font-weight: 500;">Sign in</a>
                    </small>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.getElementById('forgotPasswordForm').addEventListener('submit', function (e) {
                const email = document.getElementById('email').value.trim();

                if (!email) {
                    e.preventDefault();
                    alert('Please enter your email address.');
                    return;
                }

                // Basic email validation
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(email)) {
                    e.preventDefault();
                    alert('Please enter a valid email address.');
                    return;
                }
            });
        </script>
    </body>
</html>