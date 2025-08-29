<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User, java.util.List, Model.Address" %>

<%
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

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
        <title>Address Management</title>
        <style>
            /* CSS Scoping - All styles wrapped in .address-page */
            .address-page * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            .address-page {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                color: #333;
                padding: 20px;
                min-height: 100vh;
            }

            .address-page .container {
                max-width: 1000px;
                margin: auto;
            }

            .address-page .page-header {
                text-align: center;
                margin-bottom: 30px;
                background: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .address-page .page-header h1 {
                font-size: 28px;
                margin-bottom: 5px;
                color: #2c3e50;
            }

            .address-page .page-header p {
                font-size: 14px;
                color: #7f8c8d;
            }

            .address-page .breadcrumb {
                background: white;
                padding: 15px 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .address-page .breadcrumb a {
                color: #3498db;
                text-decoration: none;
                margin-right: 10px;
            }

            .address-page .breadcrumb a:hover {
                text-decoration: underline;
            }

            .address-page .breadcrumb span {
                color: #7f8c8d;
            }

            .address-page .address-card {
                background-color: #fff;
                padding: 25px;
                border-radius: 8px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }

            .address-page .card-title {
                font-size: 22px;
                margin-bottom: 20px;
                color: #2c3e50;
                border-left: 4px solid #3498db;
                padding-left: 15px;
            }

            .address-page .form-group {
                margin-bottom: 18px;
            }

            .address-page label {
                display: block;
                margin-bottom: 8px;
                font-weight: 600;
                color: #34495e;
            }

            .address-page input, .address-page select, .address-page textarea {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                transition: border-color 0.3s ease;
            }

            .address-page input:focus, .address-page textarea:focus {
                outline: none;
                border-color: #3498db;
                box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
            }

            .address-page .btn {
                display: inline-block;
                padding: 12px 20px;
                background-color: #3498db;
                color: #fff;
                border: none;
                border-radius: 6px;
                text-decoration: none;
                cursor: pointer;
                font-size: 14px;
                font-weight: 500;
                transition: all 0.3s ease;
                margin-right: 10px;
                margin-bottom: 10px;
            }

            .address-page .btn:hover {
                background-color: #2980b9;
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            }

            .address-page .btn-secondary {
                background-color: #95a5a6;
            }

            .address-page .btn-secondary:hover {
                background-color: #7f8c8d;
            }

            .address-page .btn-danger {
                background-color: #e74c3c;
            }

            .address-page .btn-danger:hover {
                background-color: #c0392b;
            }

            .address-page .btn-success {
                background-color: #27ae60;
            }

            .address-page .btn-success:hover {
                background-color: #1e8449;
            }

            .address-page .btn-small {
                font-size: 12px;
                padding: 8px 16px;
            }

            .address-page .address-item {
                border: 1px solid #e8e8e8;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 15px;
                background-color: #fafafa;
                transition: all 0.3s ease;
            }

            .address-page .address-item:hover {
                box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                transform: translateY(-2px);
            }

            .address-page .address-item.default {
                border-left: 4px solid #f39c12;
                background-color: #fefbf3;
            }

            .address-page .address-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 12px;
                flex-wrap: wrap;
            }

            .address-page .address-name {
                font-weight: bold;
                font-size: 18px;
                color: #2c3e50;
            }

            .address-page .default-badge {
                font-size: 12px;
                color: #fff;
                background-color: #f39c12;
                padding: 4px 12px;
                border-radius: 15px;
                font-weight: 500;
            }

            .address-page .address-info {
                font-size: 14px;
                color: #555;
                margin-bottom: 15px;
                line-height: 1.5;
            }

            .address-page .address-info strong {
                color: #2c3e50;
            }

            .address-page .address-actions {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
            }

            .address-page .empty-state {
                text-align: center;
                color: #777;
                padding: 40px;
                background-color: #fff;
                border: 2px dashed #ddd;
                border-radius: 8px;
                margin-bottom: 20px;
            }

            .address-page .empty-state-icon {
                font-size: 4rem;
                margin-bottom: 20px;
                opacity: 0.5;
            }

            .address-page .add-address-form {
                margin-top: 20px;
                padding: 25px;
                border: 1px solid #e8e8e8;
                background-color: #f8f9fa;
                border-radius: 8px;
                display: none;
                animation: slideDown 0.3s ease;
            }

            .address-page .add-address-form.show {
                display: block;
            }

            .address-page .add-address-form h4 {
                color: #3498db;
                margin-bottom: 20px;
                font-size: 18px;
            }

            .address-page .error-message {
                font-size: 12px;
                color: #e74c3c;
                display: none;
                margin-top: 5px;
            }

            .address-page .error-message.show {
                display: block;
            }

            .address-page input.error, .address-page textarea.error {
                border-color: #e74c3c;
                background-color: #fdf2f2;
            }

            .address-page .required {
                color: #e74c3c;
            }

            .address-page .form-helper {
                font-size: 12px;
                color: #7f8c8d;
                margin-top: 5px;
                font-style: italic;
            }

            /* Modal styles */
            .address-page .modal {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                visibility: hidden;
                opacity: 0;
                transition: all 0.3s ease;
                z-index: 1055;
            }

            .address-page .modal.show {
                visibility: visible;
                opacity: 1;
            }

            .address-page .modal-content {
                background-color: #fff;
                padding: 30px;
                border-radius: 12px;
                max-width: 450px;
                width: 90%;
                text-align: center;
                box-shadow: 0 10px 30px rgba(0,0,0,0.3);
                transform: scale(0.9);
                transition: transform 0.3s ease;
            }

            .address-page .modal.show .modal-content {
                transform: scale(1);
            }

            .address-page .modal-title {
                font-size: 20px;
                margin-bottom: 15px;
                font-weight: bold;
                color: #2c3e50;
            }

            .address-page .modal-message {
                font-size: 14px;
                color: #7f8c8d;
                margin-bottom: 25px;
                line-height: 1.5;
            }

            .address-page .modal-actions {
                display: flex;
                justify-content: center;
                gap: 15px;
            }

            .address-page .modal-icon {
                font-size: 3rem;
                margin-bottom: 15px;
            }

            .address-page .stats-bar {
                background: white;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .address-page .stats-item {
                text-align: center;
            }

            .address-page .stats-number {
                font-size: 24px;
                font-weight: bold;
                color: #3498db;
            }

            .address-page .stats-label {
                font-size: 12px;
                color: #7f8c8d;
                margin-top: 5px;
            }

            @keyframes slideDown {
                from {
                    opacity: 0;
                    transform: translateY(-20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            @media (max-width: 768px) {
                .address-page .container {
                    padding: 0 10px;
                }

                .address-page .address-actions {
                    flex-direction: column;
                }

                .address-page .btn {
                    width: 100%;
                    text-align: center;
                    margin-right: 0;
                }

                .address-page .modal-actions {
                    flex-direction: column;
                }

                .address-page .address-header {
                    flex-direction: column;
                    align-items: flex-start;
                    gap: 10px;
                }

                .address-page .stats-bar {
                    flex-direction: column;
                    gap: 15px;
                }
            }
        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>

        <div class="address-page">
            <div class="container">
                <!-- Breadcrumb -->
                <div class="breadcrumb">
                    <a href="user-profile">Profile</a> > <span>Address Management</span>
                </div>

                <!-- Page Header -->
                <div class="page-header">
                    <h1>Address Management</h1>
                    <p>Manage your delivery addresses for convenient shopping</p>
                </div>

                <!-- Stats Bar -->
                <div class="stats-bar">
                    <div class="stats-item">
                        <div class="stats-number"><%= addressList != null ? addressList.size() : 0%></div>
                        <div class="stats-label">Total Addresses</div>
                    </div>
                    <div class="stats-item">
                        <div class="stats-number">
                            <%
                                int defaultCount = 0;
                                if (addressList != null) {
                                    for (Address addr : addressList) {
                                        if (addr.isDefault()) {
                                            defaultCount++;
                                        }
                                    }
                                }
                            %>
                            <%= defaultCount%>
                        </div>
                        <div class="stats-label">Default Address</div>
                    </div>
                    <div class="stats-item">
                        <div class="stats-number">10</div>
                        <div class="stats-label">Max Allowed</div>
                    </div>
                </div>

                <!-- Address Management -->
                <div class="address-card">
                    <h2 class="card-title">Your Addresses</h2>

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
                                <% if (addr.getAddressType() != null && !addr.getAddressType().trim().isEmpty()) {%>
                                <br><small style="color: #7f8c8d;">Type: <%= addr.getAddressType()%></small>
                                <% }%>
                            </div>

                            <div class="address-actions">
                                <a href="address?action=edit&addressID=<%= addr.getAddressID()%>" 
                                   class="btn btn-small">Edit</a>
                                <% if (!addr.isDefault()) {%>
                                <a href="address?action=set-default&addressID=<%= addr.getAddressID()%>" 
                                   class="btn btn-small btn-success">Set Default</a>
                                <% }%>
                                <a href="address?action=delete&addressID=<%= addr.getAddressID()%>" 
                                   onclick="return confirmDelete(this, 'Delete this address permanently?');" 
                                   class="btn btn-small btn-danger">Delete</a>
                            </div>

                            <!-- Edit Form -->
                            <% if (editAddress != null && editAddress.getAddressID() == addr.getAddressID()) {%>
                            <div class="add-address-form show" style="margin-top: 25px; background: rgba(52, 152, 219, 0.05); border-color: #3498db;">
                                <h4>Edit Address</h4>
                                <form method="post" action="address">
                                    <input type="hidden" name="action" value="update-address">
                                    <input type="hidden" name="addressID" value="<%= editAddress.getAddressID()%>">

                                    <div class="form-group">
                                        <label>Address Name <span class="required">*</span></label>
                                        <input type="text" name="addressName" 
                                               value="<%= editAddress.getAddressName() != null ? editAddress.getAddressName() : ""%>" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Recipient Name <span class="required">*</span></label>
                                        <input type="text" name="recipientName" 
                                               value="<%= editAddress.getRecipientName() != null ? editAddress.getRecipientName() : ""%>" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Phone <span class="required">*</span></label>
                                        <input type="tel" name="phone" 
                                               value="<%= editAddress.getPhone() != null ? editAddress.getPhone() : ""%>" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Address Detail <span class="required">*</span></label>
                                        <textarea name="addressDetail" rows="4" required><%= editAddress.getAddressDetail() != null ? editAddress.getAddressDetail() : ""%></textarea>
                                        <div class="form-helper">Include full address: street, district, city/province</div>
                                    </div>

                                    <div class="form-group">
                                        <label>Address Type</label>
                                        <input type="text" name="addressType" 
                                               value="<%= editAddress.getAddressType() != null ? editAddress.getAddressType() : ""%>" 
                                               placeholder="e.g., Home, Office, etc.">
                                    </div>

                                    <div class="form-group">
                                        <label>
                                            <input type="checkbox" name="isDefault" value="1" 
                                                   <%= editAddress.isDefault() ? "checked" : ""%> 
                                                   style="width: auto; margin-right: 8px;">
                                            Set as default address
                                        </label>
                                    </div>

                                    <div style="display: flex; gap: 15px; margin-top: 25px;">
                                        <button type="submit" class="btn btn-success">Update Address</button>
                                        <a href="address" class="btn btn-secondary">Cancel</a>
                                    </div>
                                </form>
                            </div>
                            <% } %>
                        </div>
                        <% } %>
                        <% } else { %>
                        <div class="empty-state">
                            <div class="empty-state-icon">📍</div>
                            <h3 style="margin-bottom: 10px;">No addresses found</h3>
                            <p>Add your first address to get started with deliveries</p>
                        </div>
                        <% } %>
                    </div>

                    <% if (editAddress == null) { %>
                    <button type="button" class="btn" onclick="toggleAddressForm()">Add New Address</button>
                    <% }%>

                    <!-- Add Address Form -->
                    <div id="addressForm" class="add-address-form">
                        <h4>Add New Address</h4>
                        <form method="post" action="address" onsubmit="return validateAddressForm()">
                            <input type="hidden" name="action" value="add-address">

                            <div class="form-group">
                                <label>Address Name <span class="required">*</span></label>
                                <input type="text" name="addressName" id="addressName"
                                       value="<%= request.getAttribute("preservedAddressName") != null ? request.getAttribute("preservedAddressName") : ""%>" 
                                       required placeholder="e.g., Home Address, Office">
                                <div class="error-message" id="addressNameError">Please enter an address name</div>
                            </div>

                            <div class="form-group">
                                <label>Recipient Name <span class="required">*</span></label>
                                <input type="text" name="recipientName" id="recipientName"
                                       value="<%= request.getAttribute("preservedRecipientName") != null ? request.getAttribute("preservedRecipientName") : ""%>" 
                                       required placeholder="Full name of recipient">
                                <div class="error-message" id="recipientNameError">Please enter recipient name</div>
                            </div>

                            <div class="form-group">
                                <label>Phone <span class="required">*</span></label>
                                <input type="tel" name="phone" id="phone"
                                       value="<%= request.getAttribute("preservedPhone") != null ? request.getAttribute("preservedPhone") : ""%>" 
                                       required placeholder="e.g., 0901234567">
                                <div class="error-message" id="phoneError">Please enter a valid phone number</div>
                            </div>

                            <div class="form-group">
                                <label>Address Detail <span class="required">*</span></label>
                                <textarea name="addressDetail" id="addressDetail" rows="4" required 
                                          placeholder="Street, ward, district, city/province..."><%= request.getAttribute("preservedAddressDetail") != null ? request.getAttribute("preservedAddressDetail") : ""%></textarea>
                                <div class="form-helper">Include full address: street, district, city/province</div>
                                <div class="error-message" id="addressDetailError">Please enter address detail</div>
                            </div>

                            <div class="form-group">
                                <label for="addressType">Address Type</label>
                                <select name="addressType" id="addressType" class="form-control">
                                    <option value="" disabled selected>Select address type</option>
                                    <option value="Home">Home</option>
                                    <option value="Office">Office</option>
                                    <option value="Home Friend">Friend's House</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>
                                    <input type="checkbox" name="isDefault" value="1" id="isDefault"
                                           <%= request.getAttribute("preservedIsDefault") != null && (Boolean) request.getAttribute("preservedIsDefault") ? "checked" : ""%>
                                           style="width: auto; margin-right: 8px;">
                                    Set as default address
                                </label>
                            </div>

                            <div style="display: flex; gap: 15px; margin-top: 25px;">
                                <button type="submit" class="btn">Add Address</button>
                                <button type="button" class="btn btn-secondary" onclick="toggleAddressForm()">Cancel</button>
                            </div>
                        </form>
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
                        <button class="btn btn-danger" id="confirmYes">Yes, Delete</button>
                        <button class="btn btn-secondary" onclick="closeConfirmModal()">Cancel</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            // Form validation
            function validateAddressForm() {
                let isValid = true;

                // Reset all error states
                clearErrors();

                // Validate address name
                const addressName = document.getElementById('addressName');
                if (!addressName.value.trim()) {
                    showError('addressName', 'addressNameError', 'Please enter an address name');
                    isValid = false;
                }

                // Validate recipient name
                const recipientName = document.getElementById('recipientName');
                if (!recipientName.value.trim()) {
                    showError('recipientName', 'recipientNameError', 'Please enter recipient name');
                    isValid = false;
                }

                // Validate phone
                const phone = document.getElementById('phone');
                const phoneRegex = /^[0-9+\-\s()]{10,15}$/;
                if (!phone.value.trim()) {
                    showError('phone', 'phoneError', 'Please enter a phone number');
                    isValid = false;
                } else if (!phoneRegex.test(phone.value.trim())) {
                    showError('phone', 'phoneError', 'Please enter a valid phone number');
                    isValid = false;
                }

                // Validate address detail
                const addressDetail = document.getElementById('addressDetail');
                if (!addressDetail.value.trim()) {
                    showError('addressDetail', 'addressDetailError', 'Please enter address detail');
                    isValid = false;
                }

                return isValid;
            }

            function showError(fieldId, errorId, message) {
                const field = document.getElementById(fieldId);
                const errorElement = document.getElementById(errorId);

                field.classList.add('error');
                errorElement.textContent = message;
                errorElement.classList.add('show');
            }

            function clearErrors() {
                const errorElements = document.querySelectorAll('.error-message');
                const fieldElements = document.querySelectorAll('input, textarea');

                errorElements.forEach(el => {
                    el.classList.remove('show');
                });

                fieldElements.forEach(el => {
                    el.classList.remove('error');
                });
            }

            // Modal handling
            function showModal(message, type = 'success') {
                const modal = document.getElementById('messageModal');
                const icon = document.getElementById('modalIcon');
                const title = document.getElementById('modalTitle');
                const messageEl = document.getElementById('modalMessage');

                switch (type) {
                    case 'success':
                        icon.textContent = '✅';
                        icon.style.color = '#27ae60';
                        title.textContent = 'Success';
                        break;
                    case 'error':
                        icon.textContent = '❌';
                        icon.style.color = '#e74c3c';
                        title.textContent = 'Error';
                        break;
                    case 'warning':
                        icon.textContent = '⚠️';
                        icon.style.color = '#f39c12';
                        title.textContent = 'Warning';
                        break;
                    case 'info':
                        icon.textContent = 'ℹ️';
                        icon.style.color = '#3498db';
                        title.textContent = 'Information';
                        break;
                }

                messageEl.innerHTML = message;
                modal.classList.add('show');

                setTimeout(closeModal, 5000);
            }

            function closeModal() {
                document.getElementById('messageModal').classList.remove('show');
            }

            function closeConfirmModal() {
                document.getElementById('confirmModal').classList.remove('show');
            }

            function confirmDelete(element, message = 'Are you sure?') {
                const modal = document.getElementById('confirmModal');
                const messageEl = document.getElementById('confirmMessage');
                const confirmYes = document.getElementById('confirmYes');

                messageEl.textContent = message;

                confirmYes.onclick = () => {
                    closeConfirmModal();
                    window.location.href = element.href;
                };

                modal.classList.add('show');
                return false;
            }

            function toggleAddressForm() {
                const form = document.getElementById('addressForm');
                const isVisible = form.classList.contains('show');

                if (isVisible) {
                    form.classList.remove('show');
                    clearErrors(); // Clear any validation errors when closing
                } else {
                    form.classList.add('show');
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
                String cleanMessage = null;
                String cleanMessageType = "success";

                if (message != null) {
                    String msgStr = message.toString().trim();
                    if (!msgStr.isEmpty() && !msgStr.equals("null")) {
                        cleanMessage = msgStr;
                    }
                }

                if (messageType != null) {
                    String typeStr = messageType.toString().trim();
                    if (!typeStr.isEmpty() && !typeStr.equals("null")) {
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

            // Auto-show form if there are preserved values (form submission failed)
            <%
                boolean hasPreservedData = request.getAttribute("preservedAddressName") != null
                        || request.getAttribute("preservedRecipientName") != null
                        || request.getAttribute("preservedPhone") != null
                        || request.getAttribute("preservedAddressDetail") != null;
            %>
            <% if (hasPreservedData && editAddress == null) { %>
            window.addEventListener('DOMContentLoaded', function () {
                document.getElementById('addressForm').classList.add('show');
            });
            <% }%>

            // Keyboard shortcuts
            document.addEventListener('keydown', function (e) {
                if (e.key === 'Escape') {
                    closeModal();
                    closeConfirmModal();
                }
            });
        </script>
    </body>
</html>
