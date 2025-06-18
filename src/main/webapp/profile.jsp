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
        <title>User Profile - Your Digital Identity</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                padding: 20px;
                position: relative;
                overflow-x: hidden;
            }

            /* Animated background particles */
            body::before {
                content: '';
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
                    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
                    radial-gradient(circle at 40% 40%, rgba(120, 200, 255, 0.3) 0%, transparent 50%);
                z-index: -1;
                animation: backgroundShift 20s ease-in-out infinite;
            }

            @keyframes backgroundShift {
                0%, 100% {
                    transform: scale(1) rotate(0deg);
                }
                50% {
                    transform: scale(1.1) rotate(180deg);
                }
            }

            .container {
                max-width: 1200px;
                margin: 0 auto;
                position: relative;
                z-index: 1;
            }

            .profile-header {
                text-align: center;
                margin-bottom: 40px;
                color: white;
                position: relative;
            }

            .profile-header h1 {
                font-size: 3rem;
                font-weight: 700;
                margin-bottom: 10px;
                text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
                animation: titleGlow 2s ease-in-out infinite alternate;
            }

            @keyframes titleGlow {
                from {
                    text-shadow: 2px 2px 4px rgba(0,0,0,0.3), 0 0 20px rgba(255,255,255,0.3);
                }
                to {
                    text-shadow: 2px 2px 4px rgba(0,0,0,0.3), 0 0 30px rgba(255,255,255,0.5);
                }
            }

            .profile-header p {
                font-size: 1.2rem;
                opacity: 0.9;
            }

            .card {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(20px);
                border-radius: 20px;
                padding: 30px;
                margin-bottom: 30px;
                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                border: 1px solid rgba(255,255,255,0.2);
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            .card::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
                transition: left 0.5s ease;
            }

            .card:hover::before {
                left: 100%;
            }

            .card:hover {
                transform: translateY(-5px);
                box-shadow: 0 30px 60px rgba(0,0,0,0.15);
            }

            .card-header {
                display: flex;
                align-items: center;
                margin-bottom: 25px;
                padding-bottom: 15px;
                border-bottom: 2px solid #f0f0f0;
            }

            .card-header i {
                font-size: 1.5rem;
                margin-right: 15px;
                color: #667eea;
                animation: iconPulse 2s ease-in-out infinite;
            }

            @keyframes iconPulse {
                0%, 100% {
                    transform: scale(1);
                }
                50% {
                    transform: scale(1.1);
                }
            }

            .card-header h3 {
                font-size: 1.5rem;
                color: #333;
                font-weight: 600;
            }

            .form-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 20px;
                margin-bottom: 25px;
            }

            .form-group {
                position: relative;
            }

            .form-group label {
                display: block;
                margin-bottom: 8px;
                font-weight: 600;
                color: #555;
                font-size: 0.9rem;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .form-group input,
            .form-group select {
                width: 100%;
                padding: 15px;
                border: 2px solid #e0e0e0;
                border-radius: 12px;
                font-size: 1rem;
                transition: all 0.3s ease;
                background: rgba(255,255,255,0.9);
            }

            .form-group input:focus,
            .form-group select:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
                transform: translateY(-2px);
            }

            .btn {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                padding: 15px 30px;
                border-radius: 25px;
                font-size: 1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                text-transform: uppercase;
                letter-spacing: 1px;
                position: relative;
                overflow: hidden;
                text-decoration: none;
                display: inline-block;
            }

            .btn::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
                transition: left 0.5s ease;
            }

            .btn:hover::before {
                left: 100%;
            }

            .btn:hover {
                transform: translateY(-3px);
                box-shadow: 0 15px 30px rgba(102, 126, 234, 0.4);
            }

            .btn:active {
                transform: translateY(-1px);
            }

            .btn-danger {
                background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
            }

            .btn-danger:hover {
                box-shadow: 0 15px 30px rgba(244, 67, 54, 0.4);
            }

            .btn-small {
                padding: 8px 16px;
                font-size: 0.85rem;
                margin-right: 10px;
            }

            .alert {
                padding: 15px 20px;
                margin-bottom: 25px;
                border-radius: 12px;
                font-weight: 500;
                position: relative;
                overflow: hidden;
                animation: slideIn 0.5s ease;
            }

            @keyframes slideIn {
                from {
                    transform: translateX(-100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }

            .alert-success {
                background: linear-gradient(135deg, #4CAF50, #45a049);
                color: white;
                border-left: 5px solid #2e7d32;
            }

            .alert-error {
                background: linear-gradient(135deg, #f44336, #d32f2f);
                color: white;
                border-left: 5px solid #c62828;
            }

            .address-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 20px;
            }

            .address-card {
                background: linear-gradient(135deg, #f8f9ff, #e8eaff);
                border-radius: 15px;
                padding: 20px;
                border: 2px solid #e0e7ff;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            .address-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                width: 4px;
                height: 100%;
                background: linear-gradient(to bottom, #667eea, #764ba2);
                transition: width 0.3s ease;
            }

            .address-card:hover::before {
                width: 8px;
            }

            .address-card:hover {
                transform: translateX(5px);
                box-shadow: 0 10px 30px rgba(102, 126, 234, 0.2);
            }

            .address-card.default {
                background: linear-gradient(135deg, #fff3e0, #ffe0b2);
                border-color: #ffb74d;
            }

            .address-card.default::before {
                background: linear-gradient(to bottom, #ff9800, #f57c00);
            }

            .address-content {
                position: relative;
                z-index: 1;
            }

            .address-content i {
                color: #667eea;
                margin-right: 10px;
                font-size: 1.2rem;
            }

            .default-badge {
                display: inline-block;
                background: linear-gradient(135deg, #ff9800, #f57c00);
                color: white;
                padding: 4px 12px;
                border-radius: 20px;
                font-size: 0.8rem;
                font-weight: 600;
                margin-left: 10px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .empty-state {
                text-align: center;
                padding: 40px;
                color: #666;
                font-style: italic;
                background: rgba(255, 255, 255, 0.95);
                border-radius: 15px;
                border: 2px dashed #ddd;
            }

            .floating-action {
                position: fixed;
                bottom: 30px;
                right: 30px;
                width: 60px;
                height: 60px;
                border-radius: 50%;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                border: none;
                font-size: 1.5rem;
                cursor: pointer;
                box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
                transition: all 0.3s ease;
                z-index: 1000;
            }

            .floating-action:hover {
                transform: scale(1.1) rotate(15deg);
                box-shadow: 0 15px 40px rgba(102, 126, 234, 0.6);
            }

            /* Responsive design */
            @media (max-width: 768px) {
                .profile-header h1 {
                    font-size: 2rem;
                }

                .form-grid {
                    grid-template-columns: 1fr;
                }

                .address-grid {
                    grid-template-columns: 1fr;
                }

                .card {
                    padding: 20px;
                }

                .floating-action {
                    bottom: 20px;
                    right: 20px;
                }
            }

            /* Loading animation for form submission */
            .btn.loading::after {
                content: '';
                position: absolute;
                top: 50%;
                left: 50%;
                width: 20px;
                height: 20px;
                margin: -10px 0 0 -10px;
                border: 2px solid transparent;
                border-top: 2px solid white;
                border-radius: 50%;
                animation: spin 1s linear infinite;
            }

            @keyframes spin {
                to {
                    transform: rotate(360deg);
                }
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="profile-header">
                <h1><i class="fas fa-user-circle"></i> User Profile</h1>
                <p>Manage your personal information and preferences</p>
            </div>

            <!-- Display success/error message -->
            <%
                if (message != null) {
            %>
            <div class="alert <%= "success".equals(messageType) ? "alert-success" : "alert-error"%>">
                <i class="fas <%= "success".equals(messageType) ? "fa-check-circle" : "fa-exclamation-triangle"%>"></i>
                <%= message%>
            </div>
            <%
                }
            %>

            <!-- Profile Update Form -->
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-edit"></i>
                    <h3>Personal Information</h3>
                </div>

                <form method="post" action="profile" id="profileForm">
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

                    <button type="submit" class="btn" id="updateBtn">
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

                <!-- Debug Information (remove in production) -->
                <%-- 
                <div style="background: #f0f0f0; padding: 10px; margin-bottom: 15px; border-radius: 5px; font-size: 0.9rem;">
                    <strong>Debug Info:</strong><br>
                    addressList is null: <%= (addressList == null) %><br>
                    addressList size: <%= (addressList != null) ? addressList.size() : "N/A" %><br>
                    User ID: <%= (user != null) ? user.getUserID() : "N/A" %>
                </div>
                --%>

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

                            <div style="margin-top: 15px;">
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
                    <i class="fas fa-map-marker-alt" style="font-size: 3rem; color: #ccc; margin-bottom: 15px;"></i>
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

                <div style="margin-top: 25px;">
                    <a href="manage-address" class="btn">
                        <i class="fas fa-plus"></i> Add New Address
                    </a>
                </div>
            </div>
        </div>

        <!-- Floating Action Button -->
        <button class="floating-action" onclick="scrollToTop()" title="Back to Top">
            <i class="fas fa-arrow-up"></i>
        </button>

        <script>
            // Form submission animation
            document.getElementById('profileForm').addEventListener('submit', function () {
                const btn = document.getElementById('updateBtn');
                btn.classList.add('loading');
                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating...';
            });

            // Smooth scroll to top
            function scrollToTop() {
                window.scrollTo({
                    top: 0,
                    behavior: 'smooth'
                });
            }

            // Show floating button when scrolled down
            window.addEventListener('scroll', function () {
                const floatingBtn = document.querySelector('.floating-action');
                if (window.scrollY > 300) {
                    floatingBtn.style.opacity = '1';
                    floatingBtn.style.transform = 'scale(1)';
                } else {
                    floatingBtn.style.opacity = '0.7';
                    floatingBtn.style.transform = 'scale(0.8)';
                }
            });

            // Add ripple effect to buttons
            document.querySelectorAll('.btn').forEach(button => {
                button.addEventListener('click', function (e) {
                    let ripple = document.createElement('span');
                    let rect = this.getBoundingClientRect();
                    let size = Math.max(rect.width, rect.height);
                    let x = e.clientX - rect.left - size / 2;
                    let y = e.clientY - rect.top - size / 2;

                    ripple.style.cssText = `
                        position: absolute;
                        width: ${size}px;
                        height: ${size}px;
                        left: ${x}px;
                        top: ${y}px;
                        background: rgba(255,255,255,0.3);
                        border-radius: 50%;
                        transform: scale(0);
                        animation: ripple 0.6s ease-out;
                        pointer-events: none;
                    `;

                    this.appendChild(ripple);

                    setTimeout(() => {
                        ripple.remove();
                    }, 600);
                });
            });

            // Add ripple animation CSS
            const style = document.createElement('style');
            style.textContent = `
                @keyframes ripple {
                    to {
                        transform: scale(2);
                        opacity: 0;
                    }
                }
            `;
            document.head.appendChild(style);
        </script>
    </body>
</html>