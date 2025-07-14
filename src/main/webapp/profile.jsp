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
    String message = (String) request.getAttribute("message");
    String messageType = (String) request.getAttribute("messageType");
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

            .home-btn {
                background-color: #28a745;
                color: white;
                padding: 10px 20px;
                text-decoration: none;
                border-radius: 4px;
                margin-bottom: 20px;
                display: inline-block;
            }

            .home-btn:hover {
                background-color: #218838;
            }

            .card {
                background: white;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
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

            .btn {
                background-color: #007bff;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 4px;
                cursor: pointer;
                text-decoration: none;
                display: inline-block;
                margin-right: 10px;
            }

            .btn:hover {
                background-color: #0056b3;
            }

            .btn-success {
                background-color: #28a745;
            }

            .btn-success:hover {
                background-color: #218838;
            }

            .btn-danger {
                background-color: #dc3545;
            }

            .btn-danger:hover {
                background-color: #c82333;
            }

            .btn-secondary {
                background-color: #6c757d;
            }

            .btn-secondary:hover {
                background-color: #5a6268;
            }

            .btn-small {
                padding: 6px 12px;
                font-size: 12px;
            }

            .alert {
                padding: 12px;
                margin-bottom: 20px;
                border-radius: 4px;
            }

            .alert.success {
                background-color: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }

            .alert.error {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
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
        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>

        <div class="container">
            <div class="header">
                <h1>User Profile</h1>
            </div>

            <!-- Display Messages -->
            <% if (message != null) {%>
            <div class="alert <%= "success".equals(messageType) ? "success" : "error"%>">
                <%= message%>
            </div>
            <% }%>

            <!-- Profile Update Form -->
            <div class="card">
                <h3>Personal Information</h3>
                <form method="post" action="profile">
                    <div class="form-group">
                        <label>Full Name <span class="required">*</span></label>
                        <input type="text" name="fullName" value="<%= user != null ? user.getFullName() : ""%>" required />
                    </div>

                    <div class="form-group">
                        <label>Email Address <span class="required">*</span></label>
                        <input type="email" name="email" value="<%= user != null ? user.getEmail() : ""%>" required />
                    </div>

                    <div class="form-group">
                        <label>Phone Number <span class="required">*</span></label>
                        <input type="text" name="phone" value="<%= user != null ? user.getPhone() : ""%>" required />
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
                           onclick="return confirm('Delete this address?');" class="btn btn-small btn-danger">Delete</a>
                    </div>

                    <!-- Edit Form for this address -->
                    <% if (editAddress != null && editAddress.getAddressID() == addr.getAddressID()) {%>
                    <div style="background: #fff; padding: 20px; border-radius: 6px; margin-top: 15px; border: 2px solid #2196f3;">
                        <h4 style="color: #2196f3; margin-bottom: 15px;">Edit Address</h4>
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
            function toggleAddressForm() {
                const form = document.getElementById('addressForm');
                form.classList.toggle('show');
            }

            // Auto-hide add form when editing
            <% if (editAddress != null) { %>
            document.addEventListener('DOMContentLoaded', function () {
                const form = document.getElementById('addressForm');
                if (form) {
                    form.classList.remove('show');
                }
            });
            <% }%>
        </script>
    </body>
</html>