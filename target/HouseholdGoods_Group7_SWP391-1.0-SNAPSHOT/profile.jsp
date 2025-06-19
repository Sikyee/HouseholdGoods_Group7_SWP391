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
    String message = (String) request.getAttribute("message");
    String messageType = (String) request.getAttribute("messageType");
%>

<html>
    <head>
        <title>User Profile</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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
                max-width: 1000px;
                margin: 0 auto;
            }

            .header {
                text-align: center;
                margin-bottom: 30px;
                color: #333;
            }

            .header h1 {
                font-size: 2rem;
                margin-bottom: 10px;
            }

            .card {
                background: white;
                border-radius: 8px;
                padding: 25px;
                margin-bottom: 25px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                border: 1px solid #ddd;
            }

            .card-header {
                display: flex;
                align-items: center;
                margin-bottom: 20px;
                padding-bottom: 10px;
                border-bottom: 1px solid #eee;
            }

            .card-header i {
                font-size: 1.2rem;
                margin-right: 10px;
                color: #666;
            }

            .card-header h3 {
                font-size: 1.3rem;
                color: #333;
            }

            .form-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 15px;
                margin-bottom: 20px;
            }

            .form-group {
                margin-bottom: 15px;
            }

            .form-group label {
                display: block;
                margin-bottom: 5px;
                font-weight: 600;
                color: #555;
            }

            .form-group input,
            .form-group select {
                width: 100%;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 1rem;
            }

            .form-group input:focus,
            .form-group select:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
            }

            .btn {
                background-color: #007bff;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 4px;
                font-size: 1rem;
                cursor: pointer;
                text-decoration: none;
                display: inline-block;
            }

            .btn:hover {
                background-color: #0056b3;
            }

            .btn-danger {
                background-color: #dc3545;
            }

            .btn-danger:hover {
                background-color: #c82333;
            }

            .btn-small {
                padding: 6px 12px;
                font-size: 0.9rem;
                margin-right: 5px;
            }

            .alert {
                padding: 12px 15px;
                margin-bottom: 20px;
                border-radius: 4px;
                border: 1px solid transparent;
            }

            .alert-success {
                background-color: #d4edda;
                border-color: #c3e6cb;
                color: #155724;
            }

            .alert-error {
                background-color: #f8d7da;
                border-color: #f5c6cb;
                color: #721c24;
            }

            .address-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 15px;
            }

            .address-card {
                background: #f8f9fa;
                border-radius: 6px;
                padding: 15px;
                border: 1px solid #dee2e6;
            }

            .address-card.default {
                background: #fff3cd;
                border-color: #ffeeba;
            }

            .address-content i {
                color: #666;
                margin-right: 8px;
            }

            .default-badge {
                display: inline-block;
                background-color: #ffc107;
                color: #212529;
                padding: 2px 8px;
                border-radius: 12px;
                font-size: 0.8rem;
                font-weight: 600;
                margin-left: 8px;
            }

            .empty-state {
                text-align: center;
                padding: 30px;
                color: #666;
                background: #f8f9fa;
                border-radius: 6px;
                border: 1px dashed #dee2e6;
            }

            .empty-state i {
                font-size: 2rem;
                margin-bottom: 10px;
                color: #ccc;
            }

            @media (max-width: 768px) {
                .form-grid {
                    grid-template-columns: 1fr;
                }

                .address-grid {
                    grid-template-columns: 1fr;
                }

                .card {
                    padding: 15px;
                }
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1><i class="fas fa-user-circle"></i> User Profile</h1>
                <p>Manage your personal information and preferences</p>
            </div>

            <!-- Display success/error message -->
            <% if (message != null && messageType != null) {%>
            <script>
                Swal.fire({
                    icon: '<%= "success".equals(messageType) ? "success" : "error"%>',
                    title: '<%= message%>',
                    timer: 2000,
                    showConfirmButton: false
                });
            </script>
            <% }%>


            <!-- Profile Update Form -->
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-edit"></i>
                    <h3>Personal Information</h3>
                </div>

                <form method="post" action="profile">
                    <div class="form-grid">
                        <div class="form-group">
                            <label><i class="fas fa-user"></i> Full Name</label>
                            <input type="text" name="fullName" value="<%= user != null ? user.getFullName() : ""%>" required />
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-envelope"></i> Email Address</label>
                            <input type="email" name="email" value="<%= user != null ? user.getEmail() : ""%>" required />
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-phone"></i> Phone Number</label>
                            <input type="text" name="phone" value="<%= user != null ? user.getPhone() : ""%>" required />
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-calendar"></i> Date of Birth</label>
                            <input type="date" name="dob" value="<%= (user != null && user.getDob() != null) ? user.getDob().toString() : ""%>" />
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-venus-mars"></i> Gender</label>
                            <select name="gender">
                                <option value="">-- Select Gender --</option>
                                <option value="Male" <%= user != null && "Male".equals(user.getGender()) ? "selected" : ""%>>Male</option>
                                <option value="Female" <%= user != null && "Female".equals(user.getGender()) ? "selected" : ""%>>Female</option>
                                <option value="Other" <%= user != null && "Other".equals(user.getGender()) ? "selected" : ""%>>Other</option>
                            </select>
                        </div>
                    </div>

                    <button type="submit" class="btn">
                        <i class="fas fa-save"></i> Update Profile
                    </button>
                </form>
            </div>

            <!-- Address Management Section -->
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-map-marker-alt"></i>
                    <h3>Address Management</h3>
                </div>

                <%
                    if (addressList != null && !addressList.isEmpty()) {
                %>
                <div class="address-grid">
                    <%
                        for (Address addr : addressList) {
                            String cssClass = "address-card";
                            if (addr.isDefault()) {
                                cssClass += " default";
                            }
                    %>
                    <div class="<%= cssClass%>">
                        <div class="address-content">
                            <i class="fas fa-home"></i>
                            <%= addr.getAddressDetail() != null ? addr.getAddressDetail() : "Address details not available"%>

                            <% if (addr.isDefault()) { %>
                            <span class="default-badge">
                                <i class="fas fa-star"></i> Default
                            </span>
                            <% }%>

                            <div style="margin-top: 10px;">
                                <a href="manage-address?action=edit&addressID=<%= addr.getAddressID()%>" class="btn btn-small">
                                    <i class="fas fa-edit"></i> Edit
                                </a>

                                <a href="manage-address?action=delete&addressID=<%= addr.getAddressID()%>"
                                   onclick="return confirm('Are you sure you want to delete this address?');"
                                   class="btn btn-small btn-danger">
                                    <i class="fas fa-trash-alt"></i> Delete
                                </a>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    %>
                </div>
                <%
                } else {
                %>
                <div class="empty-state">
                    <i class="fas fa-map-marker-alt"></i>
                    <p>
                        <% if (addressList == null) { %>
                        No address data loaded. Please check your backend servlet.
                        <% } else { %>
                        No addresses found. Add your first address to get started.
                        <% } %>
                    </p>
                </div>
                <%
                    }
                %>

                <div style="margin-top: 20px;">
                    <a href="manage-address" class="btn">
                        <i class="fas fa-plus"></i> Add New Address
                    </a>
                </div>
            </div>
        </div>
        <!-- Change Password Section -->
        <div class="card">
            <div class="card-header">
                <i class="fas fa-key"></i>
                <h3>Change Password</h3>
            </div>

            <form id="changePasswordForm" method="post" action="change-password">
                <div class="form-grid">
                    <div class="form-group">
                        <label>Current Password</label>
                        <input type="password" name="currentPassword" id="currentPassword" required />
                    </div>
                    <div class="form-group">
                        <label>New Password</label>
                        <input type="password" name="newPassword" id="newPassword" required />
                    </div>
                    <div class="form-group">
                        <label>Confirm New Password</label>
                        <input type="password" name="confirmPassword" id="confirmPassword" required />
                    </div>
                </div>

                <button type="button" class="btn" onclick="confirmChangePassword()">
                    <i class="fas fa-save"></i> Change Password
                </button>
            </form>
        </div>

        <script>
            function confirmChangePassword() {
                const current = document.getElementById("currentPassword").value.trim();
                const newPass = document.getElementById("newPassword").value.trim();
                const confirmPass = document.getElementById("confirmPassword").value.trim();

                if (!current || !newPass || !confirmPass) {
                    Swal.fire('Missing fields', 'Please fill in all fields.', 'warning');
                    return;
                }

                if (newPass.length < 6) {
                    Swal.fire('Weak password', 'Password must be at least 6 characters.', 'warning');
                    return;
                }

                if (newPass === current) {
                    Swal.fire('Invalid', 'New password must be different from current password.', 'warning');
                    return;
                }

                if (newPass !== confirmPass) {
                    Swal.fire('Mismatch', 'New passwords do not match.', 'error');
                    return;
                }

                // Check password strength
                const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
                if (!strongRegex.test(newPass)) {
                    Swal.fire('Weak password', 'Password must include uppercase, lowercase, number, and special character.', 'warning');
                    return;
                }

                Swal.fire({
                    title: 'Are you sure?',
                    text: 'Do you really want to change your password?',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, change it',
                    cancelButtonText: 'Cancel'
                }).then((result) => {
                    if (result.isConfirmed) {
                        document.getElementById("changePasswordForm").submit();
                    }
                });
            }
        </script>
    </body>
</html>