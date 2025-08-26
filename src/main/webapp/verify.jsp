<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Email Verification - Household Goods</title>
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
            .verify-box {
                background-color: #fff8f2;
                padding: 40px 30px;
                border-radius: 12px;
                box-shadow: 0px 8px 20px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 450px;
            }
            .verify-title {
                font-size: 28px;
                font-weight: bold;
                color: #6e4b3a;
                margin-bottom: 20px;
            }
            .btn-verify {
                background-color: #a67c52;
                color: white;
                border: none;
                padding: 12px;
                font-weight: 600;
                transition: all 0.3s ease;
            }
            .btn-verify:hover {
                background-color: #8b6744;
                color: white;
            }
            .btn-resend {
                background-color: #6c757d;
                color: white;
                border: none;
                padding: 10px 20px;
                font-size: 14px;
                transition: all 0.3s ease;
            }
            .btn-resend:hover {
                background-color: #5a6268;
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
                font-size: 18px;
                text-align: center;
                letter-spacing: 2px;
                font-weight: 600;
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
            .verification-info {
                background-color: #e8f4fd;
                border: 1px solid #b3d4fc;
                border-radius: 8px;
                padding: 15px;
                margin: 20px 0;
                color: #0c5460;
            }
            .verification-info .icon-info {
                color: #0c5460;
                margin-right: 8px;
            }
            .email-display {
                font-weight: 600;
                color: #a67c52;
            }
            .countdown {
                font-size: 14px;
                color: #6c757d;
                margin-top: 10px;
                text-align: center;
            }
            .attempts-left {
                font-size: 12px;
                color: #dc3545;
                text-align: center;
                margin-top: 5px;
            }
            @media (max-width: 576px) {
                .verify-box {
                    margin: 10px;
                    padding: 30px 20px;
                }
            }
        </style>
    </head>
    <body>
        <div class="verify-box">
            <div class="text-center">
                <div class="icon mb-2">🛋️</div>
                <div class="verify-title">Email Verification</div>
                <p class="text-muted mb-4">Please enter the verification code sent to your email</p>
            </div>

            <!-- Verification Info -->
            <div class="verification-info">
                <i class="fas fa-envelope icon-info"></i>
                <strong>Check your inbox!</strong><br>
                We've sent a 6-digit verification code to:<br>
                <span class="email-display">
                    <% 
                        String email = (String) session.getAttribute("verificationEmail");
                        if (email != null) {
                            out.print(email);
                        } else {
                            out.print("your email address");
                        }
                    %>
                </span>
            </div>

            <!-- Display Messages -->
            <% String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
            if (error != null) {%>
            <div class="alert alert-danger text-center">
                <i class="fas fa-exclamation-triangle me-2"></i><%= error%>
            </div>
            <% }
            if (success != null) {%>
            <div class="alert alert-success text-center">
                <i class="fas fa-check-circle me-2"></i><%= success%>
            </div>
            <% }%>

            <!-- Verification Form -->
            <form action="${pageContext.request.contextPath}/register" method="post" id="verifyForm">
                <input type="hidden" name="action" value="verify"/>
                
                <div class="mb-3">
                    <label class="form-label" for="code">
                        <i class="fas fa-key me-1"></i>Verification Code
                    </label>
                    <input type="text" 
                           id="code" 
                           name="code" 
                           class="form-control" 
                           required 
                           maxlength="6"
                           placeholder="000000"
                           autocomplete="one-time-code">
                    <div class="form-text text-center">Enter the 6-digit code from your email</div>
                </div>

                <div class="d-grid mb-3">
                    <button type="submit" class="btn btn-verify">
                        <i class="fas fa-check me-2"></i>Verify Email
                    </button>
                </div>

                

                <!-- Attempts Left -->
                <% 
                    Integer attempts = (Integer) session.getAttribute("verificationAttempts");
                    if (attempts != null && attempts > 0) {
                        int remaining = 3 - attempts;
                        if (remaining > 0) {
                %>
                <div class="attempts-left">
                    <i class="fas fa-exclamation-triangle me-1"></i>
                    <%= remaining %> attempt(s) remaining
                </div>
                <% 
                        }
                    }
                %>
            </form>

            <!-- Resend Code -->
            <div class="text-center mt-4">
                <p class="mb-2">Didn't receive the code?</p>
                <form action="${pageContext.request.contextPath}/register" method="post" class="d-inline">
                    <input type="hidden" name="action" value="resend"/>
                    <button type="submit" class="btn btn-resend btn-sm" id="resendBtn">
                        <i class="fas fa-paper-plane me-1"></i>Resend Code
                    </button>
                </form>
            </div>

            <!-- Navigation Links -->
            <div class="text-center mt-4">
                <small>Want to use a different email? 
                    <a href="${pageContext.request.contextPath}/register">Register again</a>
                </small>
            </div>
            <div class="text-center mt-2">
                <a href="homePage.jsp" class="btn btn-outline-secondary btn-sm">⬅ Back to Home</a>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const codeInput = document.getElementById('code');
                const timerElement = document.getElementById('timer');
                const resendBtn = document.getElementById('resendBtn');
                
                // Auto-focus on code input
                codeInput.focus();
                
                // Format input - only allow numbers
                codeInput.addEventListener('input', function(e) {
                    let value = e.target.value.replace(/[^0-9]/g, '');
                    if (value.length > 6) {
                        value = value.substring(0, 6);
                    }
                    e.target.value = value;
                });

                // Auto-submit when 6 digits are entered
                codeInput.addEventListener('input', function(e) {
                    if (e.target.value.length === 6) {
                        // Small delay to show the complete code
                        setTimeout(() => {
                            document.getElementById('verifyForm').submit();
                        }, 500);
                    }
                });

                // Countdown timer (5 minutes)
                let timeLeft = 300; // 5 minutes in seconds
                
                function updateTimer() {
                    const minutes = Math.floor(timeLeft / 60);
                    const seconds = timeLeft % 60;
                    timerElement.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
                    
                    if (timeLeft <= 0) {
                        timerElement.parentElement.innerHTML = '<span style="color: #dc3545;"><i class="fas fa-clock me-1"></i>Code has expired</span>';
                        resendBtn.style.display = 'inline-block';
                        return;
                    }
                    
                    timeLeft--;
                    setTimeout(updateTimer, 1000);
                }
                
                // Start countdown
                updateTimer();
                
                // Resend button cooldown
                let resendCooldown = 0;
                resendBtn.addEventListener('click', function(e) {
                    if (resendCooldown > 0) {
                        e.preventDefault();
                        return;
                    }
                    
                    // Start cooldown (30 seconds)
                    resendCooldown = 30;
                    const originalText = this.innerHTML;
                    
                    const updateResendBtn = () => {
                        if (resendCooldown <= 0) {
                            this.innerHTML = originalText;
                            this.disabled = false;
                            return;
                        }
                        
                        this.innerHTML = `<i class="fas fa-clock me-1"></i>Resend (${resendCooldown}s)`;
                        this.disabled = true;
                        resendCooldown--;
                        setTimeout(updateResendBtn, 1000);
                    };
                    
                    updateResendBtn();
                });
            });
        </script>
    </body>
</html>