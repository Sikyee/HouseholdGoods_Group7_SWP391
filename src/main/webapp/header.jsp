<%@page import="Model.Cart"%>
<%@page import="Model.User"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Arial', sans-serif;
                background-color: #f8f9fa;
                padding-top: 70px;
            }

            .navbar {
                background-color: #343a40 !important;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                border-bottom: 2px solid #007bff;
            }

            .navbar-brand {
                font-weight: 600;
                font-size: 1.4rem;
                color: #ffffff !important;
            }

            .navbar-brand i {
                color: #007bff;
                margin-right: 8px;
            }

            .form-control {
                border-radius: 20px;
                border: 1px solid #ced4da;
                padding: 8px 15px;
            }

            .form-control:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
            }

            .btn-outline-light {
                border-radius: 20px;
                border: 1px solid rgba(255, 255, 255, 0.5);
                padding: 8px 15px;
            }

            .btn-outline-light:hover {
                background-color: rgba(255, 255, 255, 0.1);
                border-color: #007bff;
                color: #007bff !important;
            }

            .nav-link {
                color: rgba(255, 255, 255, 0.9) !important;
                padding: 8px 12px !important;
                border-radius: 15px;
                transition: all 0.3s ease;
            }

            .nav-link:hover {
                color: #007bff !important;
                background-color: rgba(255, 255, 255, 0.1);
            }

            .nav-link i {
                font-size: 1.1rem;
            }

            .btn-sm {
                border-radius: 20px;
                padding: 6px 15px;
                font-size: 0.875rem;
                font-weight: 500;
            }

            .btn-primary {
                background-color: #007bff;
                border-color: #007bff;
            }

            .btn-primary:hover {
                background-color: #0056b3;
                border-color: #0056b3;
            }

            .btn-danger {
                background-color: #dc3545;
                border-color: #dc3545;
            }

            .btn-danger:hover {
                background-color: #c82333;
                border-color: #c82333;
            }

            .navbar-toggler {
                border: none;
                padding: 4px 8px;
            }

            .navbar-toggler:focus {
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
            }

            /* Responsive */
            @media (max-width: 991px) {
                .navbar-nav {
                    background-color: rgba(52, 58, 64, 0.95);
                    border-radius: 10px;
                    padding: 10px;
                    margin-top: 10px;
                }

                .d-flex {
                    margin: 10px 0;
                }
            }

            /* Simple hover effects */
            .btn {
                transition: all 0.3s ease;
            }

            .btn:hover {
                transform: translateY(-1px);
            }

            .navbar-brand:hover {
                color: #007bff !important;
            }

            .nav-link {
                position: relative;
            }

            .product-quantity {
                color: white;
                border-radius: 100px;
                bottom: 33px;
                right: 220px;
                background-color: red;
                padding: 0px 5px;
                font-size: 10px;
                text-align: center;
                position: absolute;
            }

            /* Profile dropdown CSS */
            .profile-dropdown {
                position: relative;
            }

            .profile-dropdown .dropdown-menu {
                display: none;
                position: absolute;
                top: 100%;
                right: 0;
                background: white;
                border: none;
                border-radius: 8px;
                box-shadow: 0 4px 15px rgba(0,0,0,0.15);
                min-width: 180px;
                z-index: 1000;
                padding: 8px 0;
            }

            .profile-dropdown .dropdown-menu.show {
                display: block;
            }

            .profile-dropdown .dropdown-item {
                color: #333;
                padding: 10px 16px;
                text-decoration: none;
                display: flex;
                align-items: center;
                font-size: 0.875rem;
                transition: all 0.2s ease;
                border: none;
                background: none;
                width: 100%;
                text-align: left;
            }

            .profile-dropdown .dropdown-item:hover {
                background-color: #f8f9fa;
                color: #333;
            }

            .profile-dropdown .dropdown-item i {
                margin-right: 8px;
                width: 16px;
                text-align: center;
            }

            /* Divider between menu items */
            .dropdown-divider {
                height: 0;
                margin: 4px 0;
                overflow: hidden;
                border-top: 1px solid #e9ecef;
            }

            /* Special styling for logout button */
            .profile-dropdown .dropdown-item.logout-btn {
                color: #dc3545;
                border-top: 1px solid #e9ecef;
                margin-top: 4px;
                padding-top: 12px;
            }

            .profile-dropdown .dropdown-item.logout-btn:hover {
                background-color: #f8d7da;
                color: #721c24;
            }
        </style>
    </head>
    <body>
        <%
            Integer cartQuantity = (Integer) session.getAttribute("cartQuantity");
            User loggedInUser = (User) session.getAttribute("user");
        %>
        <nav class="navbar navbar-expand-lg navbar-dark fixed-top">
            <div class="container-fluid">
                <a class="navbar-brand" href="<%= request.getContextPath()%>/">
                    <i class="fas fa-home"></i>Household Goods
                </a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNav">
                    <!-- Search Bar -->
                    <div class="d-flex ms-auto me-3">
                        <input class="form-control me-2" type="search" placeholder="Search products..." aria-label="Search">
                        <button class="btn btn-outline-light" type="submit">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>

                    <!-- Navigation Links -->
                    <ul class="navbar-nav d-flex flex-row align-items-center">
                        <!-- Cart -->
                        <li class="nav-item me-3 cart">
                            <a class="nav-link" href="<%= request.getContextPath()%>/Cart" title="Shopping Cart">
                                <i class="fas fa-shopping-cart"></i>
                            </a>
                            <span class="product-quantity">
                                <%= (cartQuantity != null) ? cartQuantity : 0%>
                            </span>
                        </li>

                        <!-- Profile Menu -->
                        <% if (loggedInUser != null) {%>
                        <li class="nav-item me-3 profile-dropdown">
                            <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button" onclick="toggleProfileDropdown()">
                                <i class="fas fa-user me-1"></i><%= loggedInUser.getFullName()%>
                            </button>
                            <div class="dropdown-menu" id="profileDropdownMenu">
                                <a class="dropdown-item" href="<%= request.getContextPath()%>/profile">
                                    <i class="fas fa-user"></i>My Profile
                                </a>
                                <a class="dropdown-item" href="<%= request.getContextPath()%>/ViewFeedbackOrders">
                                    <i class="fas fa-pen me-1"></i>My Feedback

                                </a>



                                <a class="dropdown-item" href="<%= request.getContextPath()%>/orders">
                                    <i class="fas fa-shopping-bag"></i>My Orders
                                </a>

                                <a class="dropdown-item" href="<%= request.getContextPath()%>/changePassword">
                                    <i class="fas fa-key"></i>Change Password
                                </a>
                                <a class="dropdown-item logout-btn" href="<%= request.getContextPath()%>/logout">
                                    <i class="fas fa-sign-out-alt"></i>Logout
                                </a>
                            </div>
                        </li>
                        <% } else {%>
                        <!-- Login -->
                        <li class="nav-item">
                            <a href="<%= request.getContextPath()%>/login" class="btn btn-primary btn-sm">
                                <i class="fas fa-sign-in-alt me-1"></i>Login
                            </a>
                        </li>
                        <% }%>
                    </ul>
                </div>
            </div>
        </nav>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Highlight active page
            document.addEventListener('DOMContentLoaded', function () {
                const currentPath = location.pathname;
                const navLinks = document.querySelectorAll('.nav-link, .btn');

                navLinks.forEach(link => {
                    const href = link.getAttribute('href');
                    if (href && currentPath.includes(href)) {
                        link.classList.add('active');
                    }
                });
            });

            // Simple search functionality
            const searchInput = document.querySelector('input[type="search"]');
            if (searchInput) {
                searchInput.addEventListener('keypress', function (e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        const searchTerm = this.value.trim();
                        if (searchTerm) {
                            // Redirect to search page or handle search
                            window.location.href = '<%= request.getContextPath()%>/search?q=' + encodeURIComponent(searchTerm);
                        }
                    }
                });
            }

            // Profile dropdown function
            function toggleProfileDropdown() {
                const dropdown = document.getElementById('profileDropdownMenu');
                dropdown.classList.toggle('show');
            }

            // Close dropdown when clicking outside
            document.addEventListener('click', function (e) {
                const profileDropdown = document.querySelector('.profile-dropdown');
                const dropdown = document.getElementById('profileDropdownMenu');

                if (!profileDropdown.contains(e.target)) {
                    dropdown.classList.remove('show');
                }
            });

            // Close dropdown when clicking on a menu item
            document.querySelectorAll('.dropdown-item').forEach(item => {
                item.addEventListener('click', function() {
                    const dropdown = document.getElementById('profileDropdownMenu');
                    dropdown.classList.remove('show');
                });
            });
        </script>
    </body>
</html>
