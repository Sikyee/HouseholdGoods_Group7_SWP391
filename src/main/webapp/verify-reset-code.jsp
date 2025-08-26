<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Verify Reset Code</title>
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
            .verify-container {
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
                text-align: center;
                font-size: 18px;
                font-weight: 600;
                letter-spacing: 2px;
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
            .verify-icon {
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
            .email-display {
                background-color: #f8f9fa;
                padding: 10px;
                border-radius: 6px;
                margin-bottom: 20px;
                text-align: center;
                color: #6e4b3a;
                font-weight: 500;
            }
            .code-input {
                max-width: 200px;
                margin: 0 auto;
            }
            .resend-link {
                color: #a67c52;
                text-decoration: none;
                font-weight: 500;
            }
            .resend-link:hover {
                color: #8b6744;
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <div class="verify-container">
            <div class="card-wrapper">
                <div class="card">
                    <div class="card-header">
                        <div class="verify-icon">📧</div>
                        <h4 class="page-title">Verify Your Code</h4>
                        <p class="page-subtitle">Enter the 6-digit code sent to your email</p>
                    </div>
                    <div class="card-body">
                        <!-- Display email -->
                        <c:if test="${not empty email}">
                            <div class="email-display">
                                <i class="fas fa-envelope me-2"></i>
                                Code sent to: <strong>${email}</strong>
                            </div>
                        </c:if>

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

                        <form action="forgot-password" method="post" id="verifyCodeForm">
                            <input type="hidden" name="action" value="verify">

                            <div class="mb-3">
                                <label for="code" class="form-label text-center d-block">
                                    <i class="fas fa-key me-2"></i>Verification Code
                                </label>
                                <div class="code-input">
                                    <input type="text" class="form-control" id="code" name="code" 
                                           required placeholder="000000" maxlength="6" minlength="6"
                                           pattern="[0-9]{6}" title="Please enter a 6-digit code">
                                </div>
                                <div class="form-text text-center mt-2">
                                    The code will expire in 5 minutes.
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-check me-2"></i>
                                    Verify Code
                                </button>
                                <a href="forgot-password" class="btn btn-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>
                                    Back to Email
                                </a>
                            </div>
                        </form>

                        <!-- Resend option -->
                        <div class="text-center mt-4">
                            <small style="color: #8b7355;">
                                Didn't receive the code? 
                                <a href="forgot-password" class="resend-link">Resend Code</a>
                            </small>
                        </div>
                    </div>
                </div>

                <!-- Help text -->
                <div class="text-center mt-3">
                    <small style="color: #8b7355;">
                        Check your spam folder if you don't see the email.
                    </small>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.getElementById('verifyCodeForm').addEventListener('submit', function (e) {
                const code = document.getElementById('code').value.trim();

                if (!code) {
                    e.preventDefault();
                    alert('Please enter the verification code.');
                    return;
                }

                if (code.length !== 6 || !/^\d{6}$/.test(code)) {
                    e.preventDefault();
                    alert('Please enter a valid 6-digit code.');
                    return;
                }
            });

            // Auto-format code input
            document.getElementById('code').addEventListener('input', function (e) {
                let value = e.target.value.replace(/\D/g, ''); // Remove non-digits
                if (value.length > 6) {
                    value = value.substring(0, 6); // Limit to 6 digits
                }
                e.target.value = value;
            });

            // Auto-focus on code input
            document.getElementById('code').focus();
        </script>
    </body>
</html>