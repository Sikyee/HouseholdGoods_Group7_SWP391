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

    // Lấy thông báo từ session
    String message = (String) session.getAttribute("message");
    String messageType = (String) session.getAttribute("messageType");

    // Tạo biến để lưu message cho JavaScript
    String jsMessage = null;
    String jsMessageType = null;

    if (message != null) {
        jsMessage = message;
        jsMessageType = messageType;
        // Xóa thông báo khỏi session sau khi đã lấy
        session.removeAttribute("message");
        session.removeAttribute("messageType");
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <title>User Profile</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: Arial, sans-serif;
                background-color: #f5f5f5;
                padding: 20px;
            }

            .container {
                max-width: 800px;
                margin: 0 auto;
            }

            .header {
                text-align: center;
                margin-bottom: 30px;
            }

            .header h1 {
                color: #333;
                margin-bottom: 10px;
            }

            .card {
                background: white;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                position: relative;
                z-index: 1;
            }

            .card h3 {
                color: #333;
                margin-bottom: 15px;
                padding-bottom: 10px;
                border-bottom: 1px solid #eee;
            }

            .form-group {
                margin-bottom: 15px;
            }

            .form-group label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
                color: #333;
            }

            .form-group input,
            .form-group select,
            .form-group textarea {
                width: 100%;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
            }

            /* Thêm style cho validation errors */
            .form-group input.error,
            .form-group select.error,
            .form-group textarea.error {
                border-color: #dc3545;
            }

            .error-message {
                color: #dc3545;
                font-size: 12px;
                margin-top: 5px;
                display: none;
            }

            .error-message.show {
                display: block;
            }

            /* Only target buttons inside main content, not navbar */
            .container .btn {
                background-color: #007bff !important;
                color: white !important;
                border: none;
                padding: 10px 20px;
                border-radius: 4px;
                cursor: pointer;
                text-decoration: none;
                display: inline-block;
                margin-right: 10px;
                margin-bottom: 10px;
                position: relative;
                z-index: 1000;
            }

            .container .btn:hover {
                background-color: #0056b3 !important;
            }

            .container .btn-success {
                background-color: #28a745 !important;
            }

            .container .btn-success:hover {
                background-color: #218838 !important;
            }

            .container .btn-danger {
                background-color: #dc3545 !important;
            }

            .container .btn-danger:hover {
                background-color: #c82333 !important;
            }

            .container .btn-secondary {
                background-color: #6c757d !important;
            }

            .container .btn-secondary:hover {
                background-color: #5a6268 !important;
            }

            .container .btn-small {
                padding: 6px 12px;
                font-size: 12px;
                z-index: 1001;
            }

            /* Pop-up Modal Styles */
            .modal-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
                opacity: 0;
                visibility: hidden;
                transition: opacity 0.3s ease, visibility 0.3s ease;
            }

            .modal-overlay.show {
                opacity: 1;
                visibility: visible;
            }

            .modal-content {
                background: white;
                border-radius: 8px;
                padding: 30px;
                max-width: 400px;
                width: 90%;
                text-align: center;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
                transform: scale(0.8);
                transition: transform 0.3s ease;
            }

            .modal-overlay.show .modal-content {
                transform: scale(1);
            }

            .modal-icon {
                font-size: 48px;
                margin-bottom: 20px;
            }

            .modal-icon.success {
                color: #28a745;
            }

            .modal-icon.error {
                color: #dc3545;
            }

            .modal-icon.warning {
                color: #ffc107;
            }

            .modal-title {
                font-size: 20px;
                font-weight: bold;
                margin-bottom: 10px;
                color: #333;
            }

            .modal-message {
                font-size: 16px;
                color: #666;
                margin-bottom: 25px;
                line-height: 1.5;
            }

            .modal-btn {
                background-color: #007bff;
                color: white;
                border: none;
                padding: 12px 24px;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                font-weight: bold;
                transition: background-color 0.3s ease;
            }

            .modal-btn:hover {
                background-color: #0056b3;
            }

            .modal-btn.success {
                background-color: #28a745;
            }

            .modal-btn.success:hover {
                background-color: #218838;
            }

            .modal-btn.error {
                background-color: #dc3545;
            }

            .modal-btn.error:hover {
                background-color: #c82333;
            }

            .modal-btn.warning {
                background-color: #ffc107;
                color: #212529;
            }

            .modal-btn.warning:hover {
                background-color: #e0a800;
            }

            /* Confirm Modal Styles */
            .confirm-modal {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 10000;
                opacity: 0;
                visibility: hidden;
                transition: opacity 0.3s ease, visibility 0.3s ease;
            }

            .confirm-modal.show {
                opacity: 1;
                visibility: visible;
            }

            .confirm-content {
                background: white;
                border-radius: 8px;
                padding: 30px;
                max-width: 400px;
                width: 90%;
                text-align: center;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
                transform: scale(0.8);
                transition: transform 0.3s ease;
            }

            .confirm-modal.show .confirm-content {
                transform: scale(1);
            }

            .confirm-buttons {
                display: flex;
                gap: 10px;
                justify-content: center;
                margin-top: 20px;
            }

            .confirm-btn {
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
                font-weight: bold;
                transition: background-color 0.3s ease;
            }

            .confirm-btn.danger {
                background-color: #dc3545;
                color: white;
            }

            .confirm-btn.danger:hover {
                background-color: #c82333;
            }

            .confirm-btn.secondary {
                background-color: #6c757d;
                color: white;
            }

            .confirm-btn.secondary:hover {
                background-color: #5a6268;
            }

            /* Thêm style cho confirm update modal */
            .confirm-btn.primary {
                background-color: #007bff;
                color: white;
            }

            .confirm-btn.primary:hover {
                background-color: #0056b3;
            }

            .address-item {
                background: #f8f9fa;
                padding: 15px;
                border-radius: 6px;
                margin-bottom: 10px;
                border: 1px solid #dee2e6;
            }

            .address-item.default {
                background: #fff3cd;
                border-color: #ffeeba;
            }

            .default-badge {
                background-color: #ffc107;
                color: #212529;
                padding: 2px 8px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: bold;
                margin-left: 10px;
            }

            .address-form {
                display: none;
                background: #e3f2fd;
                padding: 20px;
                border-radius: 6px;
                margin-top: 15px;
                position: relative;
                z-index: 10;
            }

            .address-form.show {
                display: block;
            }

            .required {
                color: red;
            }

            .empty-state {
                text-align: center;
                padding: 30px;
                color: #666;
                background: #f8f9fa;
                border-radius: 6px;
                border: 1px dashed #dee2e6;
            }

            .edit-form {
                background: #fff;
                padding: 20px;
                border-radius: 6px;
                margin-top: 15px;
                border: 2px solid #2196f3;
                position: relative;
                z-index: 10;
            }

            .edit-form h4 {
                color: #2196f3;
                margin-bottom: 15px;
            }
        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>

        <div class="container">
            <div class="header">
                <h1>User Profile</h1>
            </div>

            <!-- Pop-up Modal for Messages -->
            <div class="modal-overlay" id="messageModal">
                <div class="modal-content">
                    <div class="modal-icon" id="modalIcon">✓</div>
                    <div class="modal-title" id="modalTitle">Success</div>
                    <div class="modal-message" id="modalMessage">Operation completed successfully!</div>
                    <button class="modal-btn" id="modalBtn" onclick="closeModal()">OK</button>
                </div>
            </div>

            <!-- THÊM MỚI: Confirm Modal for Profile Update -->
            <div class="confirm-modal" id="confirmUpdateModal">
                <div class="confirm-content">
                    <div class="modal-icon" style="color: #007bff; font-size: 48px; margin-bottom: 20px;">❓</div>
                    <div class="modal-title">Confirm Update</div>
                    <div class="modal-message">Are you sure you want to update your profile information?</div>
                    <div class="confirm-buttons">
                        <button class="confirm-btn primary" id="confirmUpdateYes">Update</button>
                        <button class="confirm-btn secondary" onclick="closeConfirmUpdateModal()">Cancel</button>
                    </div>
                </div>
            </div>

            <!-- Confirm Modal for Delete -->
            <div class="confirm-modal" id="confirmModal">
                <div class="confirm-content">
                    <div class="modal-icon" style="color: #dc3545; font-size: 48px; margin-bottom: 20px;">⚠️</div>
                    <div class="modal-title">Confirm Delete</div>
                    <div class="modal-message" id="confirmMessage">Are you sure you want to delete this address?</div>
                    <div class="confirm-buttons">
                        <button class="confirm-btn danger" id="confirmYes">Delete</button>
                        <button class="confirm-btn secondary" onclick="closeConfirmModal()">Cancel</button>
                    </div>
                </div>
            </div>

            <!-- Profile Update Form -->
            <div class="card">
                <h3>Personal Information</h3>
                <!-- THAY ĐỔI: Thêm id và onsubmit để validation -->
                <form id="profileForm" method="post" action="profile" onsubmit="return validateAndConfirmUpdate(event)">
                    <div class="form-group">
                        <label>Full Name <span class="required">*</span></label>
                        <!-- THAY ĐỔI: Thêm id để validation -->
                        <input type="text" id="fullName" name="fullName" value="<%= user != null ? user.getFullName() : ""%>" required />
                        <!-- THÊM MỚI: Error message -->
                        <div class="error-message" id="fullNameError">Full name is required</div>
                    </div>

                    <div class="form-group">
                        <label>Email Address <span class="required">*</span></label>
                        <!-- THAY ĐỔI: Thêm id để validation -->
                        <input type="email" id="email" name="email" value="<%= user != null ? user.getEmail() : ""%>" required />
                        <!-- THÊM MỚI: Error message -->
                        <div class="error-message" id="emailError">Valid email address is required</div>
                    </div>

                    <div class="form-group">
                        <label>Phone Number <span class="required">*</span></label>
                        <!-- THAY ĐỔI: Thêm id để validation -->
                        <input type="text" id="phone" name="phone" value="<%= user != null ? user.getPhone() : ""%>" required />
                        <!-- THÊM MỚI: Error message -->
                        <div class="error-message" id="phoneError">Phone number is required</div>
                    </div>

                    <div class="form-group">
                        <label>Date of Birth</label>
                        <input type="date" name="dob" value="<%= (user != null && user.getDob() != null) ? user.getDob().toString() : ""%>" />
                    </div>

                    <div class="form-group">
                        <label>Gender</label>
                        <select name="gender">
                            <option value="">-- Select Gender --</option>
                            <option value="Male" <%= user != null && "Male".equals(user.getGender()) ? "selected" : ""%>>Male</option>
                            <option value="Female" <%= user != null && "Female".equals(user.getGender()) ? "selected" : ""%>>Female</option>
                            <option value="Other" <%= user != null && "Other".equals(user.getGender()) ? "selected" : ""%>>Other</option>
                        </select>
                    </div>

                    <button type="submit" class="btn">Update Profile</button>
                </form>
            </div>

            <!-- Address Management Section -->
            <div class="card">
                <h3>Address Management</h3>

                <!-- Existing Addresses -->
                <% if (addressList != null && !addressList.isEmpty()) { %>
                <% for (Address addr : addressList) {%>
                <div class="address-item <%= addr.isDefault() ? "default" : ""%>">
                    <strong><%= addr.getAddressName()%></strong>
                    <% if (addr.isDefault()) { %>
                    <span class="default-badge">Default</span>
                    <% }%>
                    <br>
                    <%= addr.getRecipientName()%> - <%= addr.getPhone()%>
                    <br>
                    <%= addr.getAddressDetail()%>
                    <br>
                    <div style="margin-top: 10px;">
                        <a href="profile?action=edit&addressID=<%= addr.getAddressID()%>" class="btn btn-small">Edit</a>
                        <% if (!addr.isDefault()) {%>
                        <a href="profile?action=set-default&addressID=<%= addr.getAddressID()%>" class="btn btn-small btn-success">Set Default</a>
                        <% }%>
                        <a href="profile?action=delete&addressID=<%= addr.getAddressID()%>" 
                           onclick="return confirmDelete(this, 'Are you sure you want to delete this address?');" class="btn btn-small btn-danger">Delete</a>
                    </div>

                    <!-- Edit Form for this address -->
                    <% if (editAddress != null && editAddress.getAddressID() == addr.getAddressID()) {%>
                    <div class="edit-form">
                        <h4>Edit Address</h4>
                        <form method="post" action="profile">
                            <input type="hidden" name="action" value="update-address" />
                            <input type="hidden" name="addressID" value="<%= editAddress.getAddressID()%>" />

                            <div class="form-group">
                                <label>Address Name <span class="required">*</span></label>
                                <input type="text" name="addressName" value="<%= editAddress.getAddressName() != null ? editAddress.getAddressName() : ""%>" required />
                            </div>

                            <div class="form-group">
                                <label>Recipient Name <span class="required">*</span></label>
                                <input type="text" name="recipientName" value="<%= editAddress.getRecipientName() != null ? editAddress.getRecipientName() : ""%>" required />
                            </div>

                            <div class="form-group">
                                <label>Phone <span class="required">*</span></label>
                                <input type="text" name="phone" value="<%= editAddress.getPhone() != null ? editAddress.getPhone() : ""%>" required />
                            </div>

                            <div class="form-group">
                                <label>Address Detail <span class="required">*</span></label>
                                <textarea name="addressDetail" required><%= editAddress.getAddressDetail() != null ? editAddress.getAddressDetail() : ""%></textarea>
                            </div>

                            <div class="form-group">
                                <label>
                                    <input type="checkbox" name="isDefault" value="1" <%= editAddress.isDefault() ? "checked" : ""%> style="width: auto; margin-right: 8px;" />
                                    Set as default address
                                </label>
                            </div>

                            <button type="submit" class="btn btn-success">Update Address</button>
                            <a href="profile" class="btn btn-secondary">Cancel</a>
                        </form>
                    </div>
                    <% } %>
                </div>
                <% } %>
                <% } else { %>
                <div class="empty-state">
                    <p>No addresses found. Add your first address below.</p>
                </div>
                <% }%>

                <!-- Add New Address Button -->
                <% if (editAddress == null) { %>
                <button type="button" class="btn" onclick="toggleAddressForm()">Add New Address</button>
                <% } %>

                <!-- Add Address Form -->
                <div id="addressForm" class="address-form">
                    <h4 style="color: #2196f3; margin-bottom: 15px;">Add New Address</h4>
                    <form method="post" action="profile">
                        <input type="hidden" name="action" value="add-address" />

                        <div class="form-group">
                            <label>Address Name <span class="required">*</span></label>
                            <input type="text" name="addressName" required />
                        </div>

                        <div class="form-group">
                            <label>Recipient Name <span class="required">*</span></label>
                            <input type="text" name="recipientName" required />
                        </div>

                        <div class="form-group">
                            <label>Phone <span class="required">*</span></label>
                            <input type="text" name="phone" required />
                        </div>

                        <div class="form-group">
                            <label>Address Detail <span class="required">*</span></label>
                            <textarea name="addressDetail" required></textarea>
                        </div>

                        <div class="form-group">
                            <label>
                                <input type="checkbox" name="isDefault" value="1" style="width: auto; margin-right: 8px;" />
                                Set as default address
                            </label>
                        </div>

                        <button type="submit" class="btn">Add Address</button>
                        <button type="button" class="btn btn-danger" onclick="toggleAddressForm()">Cancel</button>
                    </form>
                </div>
            </div>

            <!-- Change Password Section -->
            <div class="card">
                <h3>Change Password</h3>
                <form method="post" action="change-password">
                    <div class="form-group">
                        <label>Current Password <span class="required">*</span></label>
                        <input type="password" name="currentPassword" required />
                    </div>
                    <div class="form-group">
                        <label>New Password <span class="required">*</span></label>
                        <input type="password" name="newPassword" required />
                    </div>
                    <div class="form-group">
                        <label>Confirm New Password <span class="required">*</span></label>
                        <input type="password" name="confirmPassword" required />
                    </div>

                    <button type="submit" class="btn">Change Password</button>
                </form>
            </div>
        </div>

        <script>
            // Store message data for JavaScript
            <% if (jsMessage != null) {%>
                var pageMessage = '<%= jsMessage.replace("'", "\\'").replace("\"", "\\\"")%>';
                var pageMessageType = '<%= jsMessageType%>';
            <% } else { %>
            var pageMessage = null;
            var pageMessageType = null;
            <% } %>

            // Profile validation
            function validateProfileForm() {
                var fullName = document.getElementById('fullName').value.trim();
                var email = document.getElementById('email').value.trim();
                var phone = document.getElementById('phone').value.trim();
                var isValid = true;

                // Reset error states
                resetFieldError('fullName', 'fullNameError');
                resetFieldError('email', 'emailError');
                resetFieldError('phone', 'phoneError');

                // Validate full name
                if (!fullName) {
                    showFieldError('fullName', 'fullNameError');
                    isValid = false;
                }

                // Validate email
                if (!email) {
                    showFieldError('email', 'emailError');
                    isValid = false;
                } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                    showFieldError('email', 'emailError');
                    isValid = false;
                }

                // Validate phone
                if (!phone) {
                    showFieldError('phone', 'phoneError');
                    isValid = false;
                }

                return isValid;
            }

            // Add address validation
            function validateAddAddressForm() {
                var addressName = document.getElementById('addAddressName').value.trim();
                var recipientName = document.getElementById('addRecipientName').value.trim();
                var phone = document.getElementById('addAddressPhone').value.trim();
                var addressDetail = document.getElementById('addAddressDetail').value.trim();
                var isValid = true;

                // Reset error states
                resetFieldError('addAddressName', 'addAddressNameError');
                resetFieldError('addRecipientName', 'addRecipientNameError');
                resetFieldError('addAddressPhone', 'addAddressPhoneError');
                resetFieldError('addAddressDetail', 'addAddressDetailError');

                // Validate fields
                if (!addressName) {
                    showFieldError('addAddressName', 'addAddressNameError');
                    isValid = false;
                }

                if (!recipientName) {
                    showFieldError('addRecipientName', 'addRecipientNameError');
                    isValid = false;
                }

                if (!phone) {
                    showFieldError('addAddressPhone', 'addAddressPhoneError');
                    isValid = false;
                }

                if (!addressDetail) {
                    showFieldError('addAddressDetail', 'addAddressDetailError');
                    isValid = false;
                }

                return isValid;
            }

            // Edit address validation
            function validateEditAddressForm() {
                var addressName = document.getElementById('editAddressName').value.trim();
                var recipientName = document.getElementById('editRecipientName').value.trim();
                var phone = document.getElementById('editAddressPhone').value.trim();
                var addressDetail = document.getElementById('editAddressDetail').value.trim();
                var isValid = true;

                // Reset error states
                resetFieldError('editAddressName', 'editAddressNameError');
                resetFieldError('editRecipientName', 'editRecipientNameError');
                resetFieldError('editAddressPhone', 'editAddressPhoneError');
                resetFieldError('editAddressDetail', 'editAddressDetailError');

                // Validate fields
                if (!addressName) {
                    showFieldError('editAddressName', 'editAddressNameError');
                    isValid = false;
                }

                if (!recipientName) {
                    showFieldError('editRecipientName', 'editRecipientNameError');
                    isValid = false;
                }

                if (!phone) {
                    showFieldError('editAddressPhone', 'editAddressPhoneError');
                    isValid = false;
                }

                if (!addressDetail) {
                    showFieldError('editAddressDetail', 'editAddressDetailError');
                    isValid = false;
                }

                return isValid;
            }

            // Change password validation
            function validateChangePasswordForm() {
                var currentPassword = document.getElementById('currentPassword').value.trim();
                var newPassword = document.getElementById('newPassword').value.trim();
                var confirmPassword = document.getElementById('confirmPassword').value.trim();
                var isValid = true;

                // Reset error states
                resetFieldError('currentPassword', 'currentPasswordError');
                resetFieldError('newPassword', 'newPasswordError');
                resetFieldError('confirmPassword', 'confirmPasswordError');

                // Validate current password
                if (!currentPassword) {
                    showFieldError('currentPassword', 'currentPasswordError');
                    isValid = false;
                }

                // Validate new password
                if (!newPassword || newPassword.length < 6) {
                    showFieldError('newPassword', 'newPasswordError');
                    isValid = false;
                }

                // Validate confirm password
                if (!confirmPassword || confirmPassword !== newPassword) {
                    showFieldError('confirmPassword', 'confirmPasswordError');
                    isValid = false;
                }

                return isValid;
            }

            // Helper functions
            function showFieldError(fieldId, errorId) {
                document.getElementById(fieldId).classList.add('error');
                document.getElementById(errorId).classList.add('show');
            }

            function resetFieldError(fieldId, errorId) {
                document.getElementById(fieldId).classList.remove('error');
                document.getElementById(errorId).classList.remove('show');
            }

            // Validation and confirmation functions
            function validateAndConfirmUpdate(event) {
                event.preventDefault();
                if (validateProfileForm()) {
                    showConfirmUpdateModal();
                }
                return false;
            }

            function validateAndConfirmAddAddress(event) {
                event.preventDefault();
                if (validateAddAddressForm()) {
                    showConfirmAddModal();
                }
                return false;
            }

            function validateAndConfirmUpdateAddress(event) {
                event.preventDefault();
                if (validateEditAddressForm()) {
                    showConfirmUpdateAddressModal();
                }
                return false;
            }

            function validateAndConfirmChangePassword(event) {
                event.preventDefault();
                if (validateChangePasswordForm()) {
                    showConfirmChangePasswordModal();
                }
                return false;
            }

            // Modal functions
            function showConfirmUpdateModal() {
                var modal = document.getElementById('confirmUpdateModal');
                var confirmYes = document.getElementById('confirmUpdateYes');
                confirmYes.onclick = function () {
                    closeConfirmUpdateModal();
                    document.getElementById('profileForm').submit();
                };
                modal.classList.add('show');
            }

            function showConfirmAddModal() {
                var modal = document.getElementById('confirmAddModal');
                var confirmYes = document.getElementById('confirmAddYes');
                confirmYes.onclick = function () {
                    closeConfirmAddModal();
                    document.getElementById('addAddressForm').submit();
                };
                modal.classList.add('show');
            }

            function showConfirmUpdateAddressModal() {
                var modal = document.getElementById('confirmUpdateAddressModal');
                var confirmYes = document.getElementById('confirmUpdateAddressYes');
                confirmYes.onclick = function () {
                    closeConfirmUpdateAddressModal();
                    document.getElementById('editAddressForm').submit();
                };
                modal.classList.add('show');
            }

            function showConfirmChangePasswordModal() {
                var modal = document.getElementById('confirmChangePasswordModal');
                var confirmYes = document.getElementById('confirmChangePasswordYes');
                confirmYes.onclick = function () {
                    closeConfirmChangePasswordModal();
                    document.getElementById('changePasswordForm').submit();
                };
                modal.classList.add('show');
            }

            function closeConfirmUpdateModal() {
                document.getElementById('confirmUpdateModal').classList.remove('show');
            }

            function closeConfirmAddModal() {
                document.getElementById('confirmAddModal').classList.remove('show');
            }

            function closeConfirmUpdateAddressModal() {
                document.getElementById('confirmUpdateAddressModal').classList.remove('show');
            }

            function closeConfirmChangePasswordModal() {
                document.getElementById('confirmChangePasswordModal').classList.remove('show');
            }

            function closeConfirmSetDefaultModal() {
                document.getElementById('confirmSetDefaultModal').classList.remove('show');
            }

            function toggleAddressForm() {
                const form = document.getElementById('addressForm');
                form.classList.toggle('show');
            }

            function closeModal() {
                const modal = document.getElementById('messageModal');
                modal.classList.remove('show');
            }

            function closeConfirmModal() {
                const modal = document.getElementById('confirmModal');
                modal.classList.remove('show');
            }

            function confirmDelete(element, message) {
                const modal = document.getElementById('confirmModal');
                const confirmMessage = document.getElementById('confirmMessage');
                const confirmYes = document.getElementById('confirmYes');

                confirmMessage.textContent = message;
                confirmYes.onclick = function () {
                    closeConfirmModal();
                    window.location.href = element.href;
                };

                modal.classList.add('show');
                return false;
            }

            function confirmSetDefault(element, message) {
                const modal = document.getElementById('confirmSetDefaultModal');
                const confirmMessage = document.getElementById('confirmMessage');
                const confirmYes = document.getElementById('confirmSetDefaultYes');

                confirmMessage.textContent = message;
                confirmYes.onclick = function () {
                    closeConfirmSetDefaultModal();
                    window.location.href = element.href;
                };

                modal.classList.add('show');
                return false;
            }

            // Updated showModal function with new timing logic
            function showModal(message, type) {
                const modal = document.getElementById('messageModal');
                const icon = document.getElementById('modalIcon');
                const title = document.getElementById('modalTitle');
                const messageEl = document.getElementById('modalMessage');
                const btn = document.getElementById('modalBtn');

                // Set content based on type
                if (type === 'success') {
                    icon.innerHTML = '✓';
                    icon.className = 'modal-icon success';
                    title.textContent = 'Success';
                    btn.className = 'modal-btn success';
                } else if (type === 'warning') {
                    icon.innerHTML = '⚠';
                    icon.className = 'modal-icon warning';
                    title.textContent = 'Warning';
                    btn.className = 'modal-btn warning';
                } else {
                    icon.innerHTML = '✕';
                    icon.className = 'modal-icon error';
                    title.textContent = 'Error';
                    btn.className = 'modal-btn error';
                }

                messageEl.textContent = message;

                // Show modal with different timing based on type
                if (type === 'success') {
                    // Success notification: Show after 1 second, auto-hide after 3 seconds
                    setTimeout(function () {
                        modal.classList.add('show');

                        // Auto-hide after 3 seconds for success
                        setTimeout(function () {
                            if (modal.classList.contains('show')) {
                                closeModal();
                            }
                        }, 3000);
                    }, 1000);
                } else {
                    // Error/Warning notifications: Show immediately, auto-hide after 1 minute
                    modal.classList.add('show');

                    // Auto-hide after 1 minute (60000ms) for error/warning
                    setTimeout(function () {
                        if (modal.classList.contains('show')) {
                            closeModal();
                        }
                    }, 60000);
                }
            }

            // Initialize page
            document.addEventListener('DOMContentLoaded', function () {
                // Auto-hide add form when editing
            <% if (editAddress != null) { %>
                const form = document.getElementById('addressForm');
                if (form) {
                    form.classList.remove('show');
                }
            <% }%>

                // Show modal if there's a message
                if (pageMessage && pageMessageType) {
                    showModal(pageMessage, pageMessageType);
                }
            });

            // Close modals when clicking outside
            document.addEventListener('click', function (event) {
                const modals = [
                    'messageModal',
                    'confirmModal',
                    'confirmUpdateModal',
                    'confirmAddModal',
                    'confirmUpdateAddressModal',
                    'confirmSetDefaultModal',
                    'confirmChangePasswordModal'
                ];

                modals.forEach(function (modalId) {
                    const modal = document.getElementById(modalId);
                    if (event.target === modal) {
                        modal.classList.remove('show');
                    }
                });
            });

            // Close modals with Escape key
            document.addEventListener('keydown', function (event) {
                if (event.key === 'Escape') {
                    const modals = [
                        'messageModal',
                        'confirmModal',
                        'confirmUpdateModal',
                        'confirmAddModal',
                        'confirmUpdateAddressModal',
                        'confirmSetDefaultModal',
                        'confirmChangePasswordModal'
                    ];

                    modals.forEach(function (modalId) {
                        const modal = document.getElementById(modalId);
                        modal.classList.remove('show');
                    });
                }
            });
        </script>
    </body>
</html>