<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User, java.util.List, Model.Address" %>

<%
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) request.getAttribute("user");
    List<Address> addressList = (List<Address>) request.getAttribute("addressList");
    Address editAddress = (Address) request.getAttribute("editAddress");

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
                max-width: 1000px;
                margin: auto;
            }

            .profile-page .profile-header {
                text-align: center;
                margin-bottom: 20px;
            }

            .profile-page .profile-header h1 {
                font-size: 28px;
                margin-bottom: 5px;
            }

            .profile-page .profile-header p {
                font-size: 14px;
                color: #666;
            }

            .profile-page .profile-grid {
                display: flex;
                flex-wrap: wrap;
                gap: 20px;
            }

            .profile-page .profile-card {
                background-color: #fff;
                padding: 20px;
                border: 1px solid #ddd;
                flex: 1 1 100%;
                border-radius: 5px;
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

            .profile-page input, .profile-page select, .profile-page textarea {
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
            }

            .profile-page .btn:hover {
                background-color: #555;
            }

            .profile-page .btn-secondary {
                background-color: #aaa;
            }

            .profile-page .btn-secondary:hover {
                background-color: #888;
            }

            .profile-page .btn-danger {
                background-color: #c0392b;
            }

            .profile-page .btn-danger:hover {
                background-color: #a93226;
            }

            .profile-page .btn-success {
                background-color: #27ae60;
            }

            .profile-page .btn-success:hover {
                background-color: #1e8449;
            }

            .profile-page .btn-small {
                font-size: 14px;
                padding: 6px 12px;
            }

            .profile-page .address-item {
                border: 1px solid #ddd;
                padding: 15px;
                border-radius: 5px;
                margin-bottom: 10px;
                background-color: #fafafa;
            }

            .profile-page .address-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 10px;
            }

            .profile-page .address-name {
                font-weight: bold;
                font-size: 16px;
            }

            .profile-page .default-badge {
                font-size: 12px;
                color: #fff;
                background-color: #f39c12;
                padding: 2px 8px;
                border-radius: 10px;
            }

            .profile-page .address-info {
                font-size: 14px;
                color: #555;
                margin-bottom: 10px;
            }

            .profile-page .address-actions {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
            }

            .profile-page .empty-state {
                text-align: center;
                color: #777;
                padding: 30px;
                background-color: #fff;
                border: 1px dashed #ccc;
                border-radius: 5px;
            }

            .profile-page .add-address-form {
                margin-top: 15px;
                padding: 15px;
                border: 1px solid #bbb;
                background-color: #f9f9f9;
                border-radius: 5px;
                display: none;
            }

            .profile-page .add-address-form.show {
                display: block;
            }

            .profile-page .error-message {
                font-size: 12px;
                color: red;
                display: none;
            }

            .profile-page .error-message.show {
                display: block;
            }

            .profile-page input.error, .profile-page textarea.error {
                border-color: red;
                background-color: #ffeaea;
            }

            .profile-page .required {
                color: red;
            }

            .profile-page .form-helper {
                font-size: 12px;
                color: #666;
                margin-top: 5px;
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
                .profile-page .profile-grid {
                    flex-direction: column;
                }

                .profile-page .btn {
                    width: 100%;
                    text-align: center;
                }

                .profile-page .address-actions {
                    flex-direction: column;
                }

                .profile-page .modal-actions {
                    flex-direction: column;
                }

                .profile-page .alert,
                .profile-page .notification {
                    z-index: 1050 !important;
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
                    <h1>👤 User Profile</h1>
                    <p>Manage your personal information and addresses</p>
                </div>

                <!-- Profile Grid -->
                <div class="profile-grid">
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

                    <!-- Address Management -->
                    <div class="profile-card">
                        <h2 class="card-title">Address Management</h2>

                        <div class="address-list">
                            <% if (addressList != null && !addressList.isEmpty()) { %>
                            <% for (Address addr : addressList) {%>
                            <div class="address-item <%= addr.isDefault() ? "default" : ""%>">
                                <div class="address-header">
                                    <span class="address-name"><%= addr.getAddressName()%></span>
                                    <% if (addr.isDefault()) { %>
                                    <span class="default-badge">Default</span>
                                    <% }%>
                                </div>

                                <div class="address-info">
                                    <strong><%= addr.getRecipientName()%></strong> - <%= addr.getPhone()%><br>
                                    <%= addr.getAddressDetail()%>
                                </div>

                                <div class="address-actions">
                                    <a href="profile?action=edit&addressID=<%= addr.getAddressID()%>" class="btn btn-small">Edit</a>
                                    <% if (!addr.isDefault()) {%>
                                    <a href="profile?action=set-default&addressID=<%= addr.getAddressID()%>" 
                                       class="btn btn-small btn-success">Set Default</a>
                                    <% }%>
                                    <a href="profile?action=delete&addressID=<%= addr.getAddressID()%>" 
                                       onclick="return confirmDelete(this, 'Delete this address?');" 
                                       class="btn btn-small btn-danger">Delete</a>
                                </div>

                                <!-- Edit Form -->
                                <% if (editAddress != null && editAddress.getAddressID() == addr.getAddressID()) {%>
                                <div class="add-address-form show" style="margin-top: 20px; background: rgba(46, 204, 113, 0.1); border-color: #2ecc71;">
                                    <h4 style="color: #27ae60; margin-bottom: 20px;">✏️ Edit Address</h4>
                                    <form method="post" action="profile">
                                        <input type="hidden" name="action" value="update-address">
                                        <input type="hidden" name="addressID" value="<%= editAddress.getAddressID()%>">

                                        <div class="form-group">
                                            <label>Address Name <span class="required">*</span></label>
                                            <input type="text" name="addressName" value="<%= editAddress.getAddressName() != null ? editAddress.getAddressName() : ""%>" required>
                                        </div>

                                        <div class="form-group">
                                            <label>Recipient Name <span class="required">*</span></label>
                                            <input type="text" name="recipientName" value="<%= editAddress.getRecipientName() != null ? editAddress.getRecipientName() : ""%>" required>
                                        </div>

                                        <div class="form-group">
                                            <label>Phone <span class="required">*</span></label>
                                            <input type="tel" name="phone" value="<%= editAddress.getPhone() != null ? editAddress.getPhone() : ""%>" required>
                                        </div>

                                        <div class="form-group">
                                            <label>Address Detail <span class="required">*</span></label>
                                            <textarea name="addressDetail" rows="4" required><%= editAddress.getAddressDetail() != null ? editAddress.getAddressDetail() : ""%></textarea>
                                            <div class="form-helper">Include full address: street, district, city/province</div>
                                        </div>

                                        <div class="form-group">
                                            <label>Address Type</label>
                                            <input type="text" name="addressType" value="<%= editAddress.getAddressType() != null ? editAddress.getAddressType() : ""%>" placeholder="e.g., Home, Office">
                                        </div>

                                        <div class="form-group">
                                            <label>
                                                <input type="checkbox" name="isDefault" value="1" <%= editAddress.isDefault() ? "checked" : ""%> style="width: auto; margin-right: 8px;">
                                                Set as default address
                                            </label>
                                        </div>

                                        <div style="display: flex; gap: 10px;">
                                            <button type="submit" class="btn btn-success">💾 Update</button>
                                            <a href="profile" class="btn btn-secondary">❌ Cancel</a>
                                        </div>
                                    </form>
                                </div>
                                <% } %>
                            </div>
                            <% } %>
                            <% } else { %>
                            <div class="empty-state">
                                <div style="font-size: 3rem; margin-bottom: 15px;">📍</div>
                                <p><strong>No addresses found</strong></p>
                                <p>Add your first address to get started</p>
                            </div>
                            <% } %>
                        </div>

                        <% if (editAddress == null) { %>
                        <button type="button" class="btn" onclick="toggleAddressForm()">Add New Address</button>
                        <% }%>

                        <!-- Add Address Form -->
                        <div id="addressForm" class="add-address-form">
                            <h4 style="color: #667eea; margin-bottom: 20px;">Add New Address</h4>
                            <form method="post" action="profile">
                                <input type="hidden" name="action" value="add-address">

                                <div class="form-group">
                                    <label>Address Name <span class="required">*</span></label>
                                    <input type="text" name="addressName" 
                                           value="<%= request.getAttribute("preservedAddressName") != null ? request.getAttribute("preservedAddressName") : ""%>" required>
                                </div>

                                <div class="form-group">
                                    <label>Recipient Name <span class="required">*</span></label>
                                    <input type="text" name="recipientName" 
                                           value="<%= request.getAttribute("preservedRecipientName") != null ? request.getAttribute("preservedRecipientName") : ""%>" required>
                                </div>

                                <div class="form-group">
                                    <label>Phone <span class="required">*</span></label>
                                    <input type="tel" name="phone" 
                                           value="<%= request.getAttribute("preservedPhone") != null ? request.getAttribute("preservedPhone") : ""%>" required>
                                </div>

                                <div class="form-group">
                                    <label>Address Detail <span class="required">*</span></label>
                                    <textarea name="addressDetail" rows="4" required><%= request.getAttribute("preservedAddressDetail") != null ? request.getAttribute("preservedAddressDetail") : ""%></textarea>
                                    <div class="form-helper">Include full address: street, district, city/province</div>
                                </div>

                                <div class="form-group">
                                    <label>Address Type</label>
                                    <input type="text" name="addressType" 
                                           value="<%= request.getAttribute("preservedAddressType") != null ? request.getAttribute("preservedAddressType") : ""%>" 
                                           placeholder="e.g., Home, Office">
                                </div>

                                <div class="form-group">
                                    <label>
                                        <input type="checkbox" name="isDefault" value="1" 
                                               <%= request.getAttribute("preservedIsDefault") != null && (Boolean) request.getAttribute("preservedIsDefault") ? "checked" : ""%>
                                               style="width: auto; margin-right: 8px;">
                                        Set as default address
                                    </label>
                                </div>

                                <div style="display: flex; gap: 10px;">
                                    <button type="submit" class="btn">Add Address</button>
                                    <button type="button" class="btn btn-secondary" onclick="toggleAddressForm()">Cancel</button>
                                </div>
                            </form>
                        </div>
                    </div>
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

            <!-- Confirmation Modal -->
            <div class="modal" id="confirmModal">
                <div class="modal-content">
                    <div class="modal-icon">⚠️</div>
                    <div class="modal-title">Confirm Action</div>
                    <div class="modal-message" id="confirmMessage">Are you sure?</div>
                    <div class="modal-actions">
                        <button class="btn btn-danger" id="confirmYes">Yes</button>
                        <button class="btn btn-secondary" onclick="closeConfirmModal()">Cancel</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- JavaScript for handling notifications and UI interaction -->
        <script>
            // Enhanced modal handling
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

            function closeConfirmModal() {
                document.getElementById('confirmModal').classList.remove('show');
            }

            // Enhanced confirm delete
            function confirmDelete(element, message = 'Are you sure?') {
                const modal = document.getElementById('confirmModal');
                const messageEl = document.getElementById('confirmMessage');
                const confirmYes = document.getElementById('confirmYes');

                messageEl.textContent = message;

                confirmYes.onclick = () => {
                    closeConfirmModal();
                    // Redirect to delete URL
                    window.location.href = element.href;
                };

                modal.classList.add('show');
                return false;
            }

            // Enhanced profile form handling
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

            // Toggle address form
            function toggleAddressForm() {
                const form = document.getElementById('addressForm');
                const isVisible = form.classList.contains('show');

                if (isVisible) {
                    form.classList.remove('show');
                } else {
                    form.classList.add('show');
                    // Focus on first input
                    setTimeout(() => {
                        const firstInput = form.querySelector('input[name="addressName"]');
                        if (firstInput) {
                            firstInput.focus();
                        }
                    }, 300);
                }
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

            // Add keyboard shortcuts for better UX
            document.addEventListener('keydown', function (e) {
                // Escape key closes modals
                if (e.key === 'Escape') {
                    closeModal();
                    closeConfirmModal();
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