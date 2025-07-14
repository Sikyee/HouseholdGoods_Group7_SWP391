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
        </style>
    </head>
    <body>
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
                        <li class="nav-item me-3">
                            <a class="nav-link" href="<%= request.getContextPath()%>/Cart" title="">
                                <i class="fas fa-shopping-cart"></i>
                            </a>
                        </li>

                        <!-- Profile -->
                        <li class="nav-item me-3">
                            <a href="<%= request.getContextPath()%>/profile" class="btn btn-outline-light btn-sm">
                                <i class="fas fa-user me-1"></i>Profile
                            </a>
                        </li>

                        <!-- Login/Logout -->
                        <% if (session.getAttribute("user") == null) {%>
                        <li class="nav-item">
                            <a href="<%= request.getContextPath()%>/login" class="btn btn-primary btn-sm">
                                <i class="fas fa-sign-in-alt me-1"></i>Login
                            </a>
                        </li>
                        <% } else {%>
                        <li class="nav-item">
                            <a href="<%= request.getContextPath()%>/logout" class="btn btn-danger btn-sm">
                                <i class="fas fa-sign-out-alt me-1"></i>Logout
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
        </script>
    </body>
</html>