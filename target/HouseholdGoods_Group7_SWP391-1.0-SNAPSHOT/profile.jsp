<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User" %>

<%
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) request.getAttribute("user");

    // Try to get from request first, then session
    String message = (String) request.getAttribute("message");
    String messageType = (String) request.getAttribute("messageType");

    // If not in request, try session
    if (message == null) {
        message = (String) session.getAttribute("message");
        messageType = (String) session.getAttribute("messageType");

        // Remove from session if found
        if (message != null) {
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>User Profile</title>
        <style>
            /* CSS Scoping - All styles wrapped in .profile-page */
            .profile-page * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            .profile-page {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                color: #333;
                padding: 20px;
            }

            .profile-page .container {
                max-width: 800px;
                margin: auto;
            }

            .profile-page .profile-header {
                text-align: center;
                margin-bottom: 30px;
            }

            .profile-page .profile-header h1 {
                font-size: 28px;
                margin-bottom: 5px;
            }

            .profile-page .profile-header p {
                font-size: 14px;
                color: #666;
            }

            .profile-page .profile-card {
                background-color: #fff;
                padding: 20px;
                border: 1px solid #ddd;
                border-radius: 5px;
                margin-bottom: 20px;
            }

            .profile-page .card-title {
                font-size: 20px;
                margin-bottom: 15px;
                border-left: 4px solid #333;
                padding-left: 10px;
            }

            .profile-page .form-group {
                margin-bottom: 15px;
            }

            .profile-page label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }

            .profile-page input, .profile-page select {
                width: 100%;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 4px;
            }

            .profile-page .btn {
                display: inline-block;
                padding: 8px 16px;
                background-color: #333;
                color: #fff;
                border: none;
                border-radius: 4px;
                text-decoration: none;
                cursor: pointer;
                margin-right: 10px;
                margin-bottom: 10px;
            }

            .profile-page .btn:hover {
                background-color: #555;
            }

            .profile-page .btn-secondary {
                background-color: #6c757d;
            }

            .profile-page .btn-secondary:hover {
                background-color: #5a6268;
            }

            .profile-page .error-message {
                font-size: 12px;
                color: red;
                display: none;
            }

            .profile-page .error-message.show {
                display: block;
            }

            .profile-page input.error {
                border-color: red;
                background-color: #ffeaea;
            }

            .profile-page .required {
                color: red;
            }

            .profile-page .quick-links {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 15px;
                margin-bottom: 30px;
            }

            .profile-page .quick-link {
                display: flex;
                align-items: center;
                padding: 15px 20px;
                background-color: #fff;
                border: 1px solid #ddd;
                border-radius: 8px;
                text-decoration: none;
                color: #333;
                transition: all 0.3s;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .profile-page .quick-link:hover {
                background-color: #f8f9fa;
                border-color: #007bff;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                color: #007bff;
            }

            .profile-page .quick-link .icon {
                font-size: 24px;
                margin-right: 15px;
                width: 40px;
                text-align: center;
            }

            .profile-page .quick-link .content h3 {
                font-size: 16px;
                margin-bottom: 5px;
                font-weight: 600;
            }

            .profile-page .quick-link .content p {
                font-size: 12px;
                color: #666;
                margin: 0;
            }

            /* Modal styles */
            .profile-page .modal {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.4);
                display: flex;
                justify-content: center;
                align-items: center;
                visibility: hidden;
                opacity: 0;
                transition: 0.3s;
                z-index: 1055;
            }

            .profile-page .modal.show {
                visibility: visible;
                opacity: 1;
            }

            .profile-page .modal-content {
                background-color: #fff;
                padding: 20px;
                border-radius: 5px;
                max-width: 400px;
                width: 90%;
                text-align: center;
            }

            .profile-page .modal-title {
                font-size: 18px;
                margin-bottom: 10px;
                font-weight: bold;
            }

            .profile-page .modal-message {
                font-size: 14px;
                color: #666;
                margin-bottom: 15px;
            }

            .profile-page .modal-actions {
                display: flex;
                justify-content: center;
                gap: 10px;
            }

            .profile-page .modal-icon {
                font-size: 24px;
                margin-bottom: 10px;
                color: #333;
            }

            @media (max-width: 768px) {
                .profile-page .quick-links {
                    grid-template-columns: 1fr;
                }

                .profile-page .btn {
                    width: 100%;
                    text-align: center;
                    margin-bottom: 10px;
                    margin-right: 0;
                }

                .profile-page .modal-actions {
                    flex-direction: column;
                }
            }
        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>

        <div class="profile-page">
            <div class="container">
                <!-- Profile Header -->
                <div class="profile-header">
                    <h1>User Profile</h1>
                    <p>Manage your personal information and account settings</p>
                </div>

                <!-- Quick Links -->
                <div class="quick-links">
                    <a href="address" class="quick-link">
                        <div class="icon">📍</div>
                        <div class="content">
                            <h3>My Addresses</h3>
                            <p>Manage delivery addresses</p>
                        </div>
                    </a>

                    <a href="order" class="quick-link">
                        <div class="icon">📦</div>
                        <div class="content">
                            <h3>My Orders</h3>
                            <p>View order history</p>
                        </div>
                    </a>

                    <a href="changePassword" class="quick-link">
                        <div class="icon">🔐</div>
                        <div class="content">
                            <h3>Change Password</h3>
                            <p>Update your password</p>
                        </div>
                    </a>

                    <a href="ViewFeedbackOrders" class="quick-link">
                        <div class="icon">✍️</div>
                        <div class="content">
                            <h3>My Feedback</h3>
                            <p>View and manage reviews</p>
                        </div>
                    </a>
                </div>

                <!-- Personal Information -->
                <div class="profile-card">
                    <h2 class="card-title">Personal Information</h2>
                    <form id="profileForm" method="post" action="profile">
                        <div class="form-group">
                            <label>Full Name <span class="required">*</span></label>
                            <input type="text" id="fullName" name="fullName" 
                                   value="<%= request.getAttribute("preservedFullName") != null ? request.getAttribute("preservedFullName") : (user != null ? user.getFullName() : "")%>" required>
                            <div class="error-message" id="fullNameError">Full name is required</div>
                        </div>

                        <div class="form-group">
                            <label>Email Address <span class="required">*</span></label>
                            <input type="email" id="email" name="email" 
                                   value="<%= request.getAttribute("preservedEmail") != null ? request.getAttribute("preservedEmail") : (user != null ? user.getEmail() : "")%>" required>
                            <div class="error-message" id="emailError">Valid email is required</div>
                        </div>

                        <div class="form-group">
                            <label>Phone Number <span class="required">*</span></label>
                            <input type="tel" id="phone" name="phone" 
                                   value="<%= request.getAttribute("preservedPhone") != null ? request.getAttribute("preservedPhone") : (user != null && user.getPhone() != null ? user.getPhone() : "")%>" 
                                   maxlength="15" required>
                            <div class="error-message" id="phoneError">Phone number is required</div>
                        </div>

                        <div class="form-group">
                            <label>Date of Birth <span class="required">*</span></label>
                            <input type="date" name="dob" 
                                   value="<%= request.getAttribute("preservedDob") != null ? request.getAttribute("preservedDob") : ((user != null && user.getDob() != null) ? user.getDob().toString() : "")%>" required>
                            <div class="error-message" id="dobError">Date of birth is required</div>
                        </div>

                        <div class="form-group">
                            <label>Gender <span class="required">*</span></label>
                            <select name="gender" id="gender" required>
                                <option value="">Select Gender</option>
                                <%
                                    String preservedGender = (String) request.getAttribute("preservedGender");
                                    String currentGender = preservedGender != null ? preservedGender : (user != null ? user.getGender() : "");
                                %>
                                <option value="Nam" <%= "Nam".equals(currentGender) ? "selected" : ""%>>Nam</option>
                                <option value="Nữ" <%= "Nữ".equals(currentGender) ? "selected" : ""%>>Nữ</option>
                                <option value="Khác" <%= "Khác".equals(currentGender) ? "selected" : ""%>>Khác</option>
                            </select>
                            <div class="error-message" id="genderError">Please select your gender</div>
                        </div>

                        <button type="submit" class="btn">Update Profile</button>
                    </form>
                </div>
            </div>

            <!-- Message Modal -->
            <div class="modal" id="messageModal">
                <div class="modal-content">
                    <div class="modal-icon" id="modalIcon"></div>
                    <div class="modal-title" id="modalTitle"></div>
                    <div class="modal-message" id="modalMessage"></div>
                    <div class="modal-actions">
                        <button class="btn" onclick="closeModal()">OK</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- JavaScript for handling notifications and UI interaction -->
        <script>
            // Modal handling
            function showModal(message, type = 'success') {
                const modal = document.getElementById('messageModal');
                const icon = document.getElementById('modalIcon');
                const title = document.getElementById('modalTitle');
                const messageEl = document.getElementById('modalMessage');

                if (type === 'success') {
                    icon.textContent = '✅';
                    icon.style.color = '#27ae60';
                    title.textContent = 'Success';
                } else if (type === 'error') {
                    icon.textContent = '❌';
                    icon.style.color = '#e74c3c';
                    title.textContent = 'Error';
                } else if (type === 'warning') {
                    icon.textContent = '⚠️';
                    icon.style.color = '#f39c12';
                    title.textContent = 'Warning';
                } else if (type === 'info') {
                    icon.textContent = 'ℹ️';
                    icon.style.color = '#3498db';
                    title.textContent = 'Information';
                }

                messageEl.innerHTML = message;
                modal.classList.add('show');

                // Auto close after 4 seconds
                setTimeout(closeModal, 4000);
            }

            function closeModal() {
                document.getElementById('messageModal').classList.remove('show');
            }

            // Profile form handling
            const profileForm = document.getElementById("profileForm");
            if (profileForm) {
                profileForm.addEventListener("submit", function (e) {
                    const fullName = document.getElementById("fullName").value.trim();
                    const email = document.getElementById("email").value.trim();
                    const phone = document.getElementById("phone").value.trim();
                    const gender = document.getElementById("gender").value;
                    let isValid = true;

                    // Validate full name
                    if (!fullName) {
                        document.getElementById("fullNameError").classList.add("show");
                        document.getElementById("fullName").classList.add("error");
                        isValid = false;
                    } else {
                        document.getElementById("fullNameError").classList.remove("show");
                        document.getElementById("fullName").classList.remove("error");
                    }

                    // Validate email
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!email || !emailRegex.test(email)) {
                        document.getElementById("emailError").classList.add("show");
                        document.getElementById("email").classList.add("error");
                        isValid = false;
                    } else {
                        document.getElementById("emailError").classList.remove("show");
                        document.getElementById("email").classList.remove("error");
                    }

                    // Validate phone
                    const phoneRegex = /^(\+84|84|0)(3|5|7|8|9)[0-9]{8}$/;
                    if (!phone || !phoneRegex.test(phone.replace(/\s/g, ''))) {
                        document.getElementById("phoneError").textContent = "Please enter a valid Vietnamese phone number";
                        document.getElementById("phoneError").classList.add("show");
                        document.getElementById("phone").classList.add("error");
                        isValid = false;
                    } else {
                        document.getElementById("phoneError").classList.remove("show");
                        document.getElementById("phone").classList.remove("error");
                    }

                    // Validate gender
                    if (!gender) {
                        document.getElementById("genderError").classList.add("show");
                        document.getElementById("gender").classList.add("error");
                        isValid = false;
                    } else {
                        document.getElementById("genderError").classList.remove("show");
                        document.getElementById("gender").classList.remove("error");
                    }

                    if (!isValid) {
                        e.preventDefault();
                        showModal('Please fix the errors in the form', 'error');
                        return false;
                    }

                    return true;
                });
            }

            // Display server messages
            <%
                // Server-side validation before sending to JavaScript
                String cleanMessage = null;
                String cleanMessageType = "success";

                if (message != null) {
                    String msgStr = message.toString().trim();
                    if (!msgStr.isEmpty()
                            && !msgStr.equals("null")
                            && !msgStr.equals("undefined")
                            && !msgStr.equals("false")
                            && !msgStr.equals("true")) {
                        cleanMessage = msgStr;
                    }
                }

                if (messageType != null) {
                    String typeStr = messageType.toString().trim();
                    if (!typeStr.isEmpty()
                            && !typeStr.equals("null")
                            && !typeStr.equals("undefined")) {
                        cleanMessageType = typeStr;
                    }
                }
            %>

            <% if (cleanMessage != null) {%>
            window.addEventListener('DOMContentLoaded', function () {
                const messageText = '<%= cleanMessage.replaceAll("'", "\\'").replaceAll("\"", "\\\"").replaceAll("<br>", "\\n")%>';
                const messageType = '<%= cleanMessageType%>';

                if (messageText && messageText.length > 0) {
                    showModal(messageText, messageType);
                }
            });
            <% }%>

            // Keyboard shortcuts
            document.addEventListener('keydown', function (e) {
                // Escape key closes modals
                if (e.key === 'Escape') {
                    closeModal();
                }

                // Ctrl+S to save profile (prevent default browser save)
                if (e.ctrlKey && e.key === 's') {
                    e.preventDefault();
                    const profileForm = document.getElementById('profileForm');
                    if (profileForm) {
                        profileForm.submit();
                    }
                }
            });

            // Add notification for unsaved changes
            let formChanged = false;
            const formInputs = document.querySelectorAll('input, textarea, select');
            formInputs.forEach(input => {
                input.addEventListener('change', function () {
                    formChanged = true;
                });
            });

            window.addEventListener('beforeunload', function (e) {
                if (formChanged) {
                    e.preventDefault();
                    e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
                    return e.returnValue;
                }
            });

            // Clear formChanged flag on successful submission
            document.querySelectorAll('form').forEach(form => {
                form.addEventListener('submit', function () {
                    formChanged = false;
                });
            });
        </script>
    </body>
</html>
