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
                padding-top: 70px; /* Add padding to account for fixed navbar */
            }

            .navbar {
                background: linear-gradient(135deg, #343a40 0%, #495057 100%) !important;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                border-bottom: 3px solid #007bff;
                backdrop-filter: blur(10px);
                transition: all 0.3s ease;
            }

            .navbar:hover {
                box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            }

            .navbar-brand {
                font-weight: bold;
                font-size: 1.5rem;
                color: #ffffff !important;
                text-shadow: 0 2px 4px rgba(0,0,0,0.3);
                transition: all 0.3s ease;
            }

            .navbar-brand:hover {
                color: #007bff !important;
                transform: scale(1.05);
            }

            .navbar-brand i {
                color: #007bff;
                filter: drop-shadow(0 0 5px rgba(0, 123, 255, 0.3));
            }

            .form-control {
                border-radius: 25px;
                border: 2px solid transparent;
                background: rgba(255, 255, 255, 0.9);
                backdrop-filter: blur(10px);
                transition: all 0.3s ease;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }

            .form-control:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25), 0 4px 15px rgba(0,0,0,0.1);
                background: rgba(255, 255, 255, 1);
                transform: translateY(-1px);
            }

            .btn-outline-light {
                border-radius: 25px;
                border: 2px solid rgba(255, 255, 255, 0.8);
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(10px);
                transition: all 0.3s ease;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }

            .btn-outline-light:hover {
                background: rgba(255, 255, 255, 0.2);
                border-color: #007bff;
                color: #007bff !important;
                transform: translateY(-2px);
                box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            }

            .nav-link {
                color: rgba(255, 255, 255, 0.9) !important;
                transition: all 0.3s ease;
                border-radius: 20px;
                padding: 8px 12px !important;
                position: relative;
                overflow: hidden;
            }

            .nav-link::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
                transition: left 0.5s;
            }

            .nav-link:hover::before {
                left: 100%;
            }

            .nav-link:hover {
                color: #007bff !important;
                background: rgba(255, 255, 255, 0.1);
                transform: translateY(-2px);
                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            }

            .nav-link i {
                font-size: 1.2rem;
                transition: all 0.3s ease;
            }

            .nav-link:hover i {
                transform: scale(1.2);
                filter: drop-shadow(0 0 5px rgba(0, 123, 255, 0.5));
            }

            .btn-primary.btn-sm, .btn-danger.btn-sm {
                border-radius: 25px;
                padding: 6px 16px;
                font-weight: 500;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            .btn-outline-light.btn-sm {
                background: rgba(255, 255, 255, 0.1);
                border: 2px solid rgba(255, 255, 255, 0.3);
                backdrop-filter: blur(10px);
            }

            .btn-outline-light.btn-sm:hover {
                background: rgba(255, 255, 255, 0.2);
                border-color: rgba(255, 255, 255, 0.6);
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(0,0,0,0.15);
            }

            .btn-primary.btn-sm {
                background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
                border: none;
                box-shadow: 0 4px 15px rgba(0, 123, 255, 0.3);
            }

            .btn-primary.btn-sm:hover {
                background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
                transform: translateY(-2px);
                box-shadow: 0 6px 25px rgba(0, 123, 255, 0.4);
            }

            .btn-danger.btn-sm {
                background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
                border: none;
                box-shadow: 0 4px 15px rgba(220, 53, 69, 0.3);
            }

            .btn-danger.btn-sm:hover {
                background: linear-gradient(135deg, #c82333 0%, #a71e2a 100%);
                transform: translateY(-2px);
                box-shadow: 0 6px 25px rgba(220, 53, 69, 0.4);
            }

            .btn-primary.btn-sm::before, .btn-outline-light.btn-sm::before, .btn-danger.btn-sm::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
                transition: left 0.5s;
            }

            .btn-primary.btn-sm:hover::before, .btn-outline-light.btn-sm:hover::before, .btn-danger.btn-sm:hover::before {
                left: 100%;
            }

            .navbar-toggler {
                border: none;
                padding: 4px 8px;
                transition: all 0.3s ease;
            }

            .navbar-toggler:focus {
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
            }

            .navbar-toggler:hover {
                background: rgba(255, 255, 255, 0.1);
                transform: scale(1.1);
            }

            /* Responsive adjustments */
            @media (max-width: 991px) {
                .navbar-nav {
                    background: rgba(52, 58, 64, 0.95);
                    backdrop-filter: blur(20px);
                    border-radius: 15px;
                    padding: 15px;
                    margin-top: 10px;
                    box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                }

                .d-flex {
                    margin: 15px 0;
                }
            }

            /* Animation for navbar items */
            .navbar-nav .nav-item {
                animation: slideInFromTop 0.5s ease-out;
                animation-fill-mode: both;
            }

            .navbar-nav .nav-item:nth-child(1) { animation-delay: 0.1s; }
            .navbar-nav .nav-item:nth-child(2) { animation-delay: 0.2s; }
            .navbar-nav .nav-item:nth-child(3) { animation-delay: 0.3s; }
            .navbar-nav .nav-item:nth-child(4) { animation-delay: 0.4s; }
            .navbar-nav .nav-item:nth-child(5) { animation-delay: 0.5s; }

            @keyframes slideInFromTop {
                from {
                    opacity: 0;
                    transform: translateY(-20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* Glowing effect for active elements */
            .nav-link.active, .btn.active {
                box-shadow: 0 0 20px rgba(0, 123, 255, 0.5);
                background: rgba(0, 123, 255, 0.1);
            }

            /* Pulse animation for promotion icon */
            .fa-tags {
                animation: pulse 2s infinite;
            }

            @keyframes pulse {
                0% { transform: scale(1); }
                50% { transform: scale(1.1); }
                100% { transform: scale(1); }
            }
        </style>

        <nav class="navbar navbar-expand-lg navbar-dark fixed-top">
            <div class="container-fluid">
                <a class="navbar-brand" href="<%= request.getContextPath()%>/"><i class="fas fa-home me-2"></i>Household Goods</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <div class="d-flex ms-auto me-3">
                        <input class="form-control me-2" type="search" placeholder="Search products..." aria-label="Search">
                        <button class="btn btn-outline-light" type="submit">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                    <ul class="navbar-nav d-flex flex-row align-items-center">
                        <li class="nav-item me-3">
                            <a class="nav-link" href="<%= request.getContextPath()%>/ProductController?action=list" title="Products">
                                <i class="fas fa-box"></i>
                            </a>
                        </li>
                        <li class="nav-item me-3">
                            <a class="nav-link" href="<%= request.getContextPath()%>/PromotionController?action=list" title="Promotions">
                                <i class="fas fa-tags"></i>
                            </a>
                        </li>
                        <li class="nav-item me-3">
                            <a class="nav-link" href="<%= request.getContextPath()%>/Cart" title="">
                                <i class="fas fa-shopping-cart"></i>
                            </a>
                        </li>
                        <li class="nav-item me-3">
                            <a href="<%= request.getContextPath()%>/profile" class="btn btn-outline-light btn-sm">
                                <i class="fas fa-user me-1"></i>Profile
                            </a>
                        </li>
                        <% if (session.getAttribute("user") == null) { %>
                        <li class="nav-item">
                            <a href="<%= request.getContextPath()%>/login" class="btn btn-primary btn-sm">
                                <i class="fas fa-sign-in-alt me-1"></i>Login
                            </a>
                        </li>
                        <% } else { %>
                        <li class="nav-item">
                            <a href="<%= request.getContextPath()%>/logout" class="btn btn-danger btn-sm">
                                <i class="fas fa-sign-out-alt me-1"></i>Logout
                            </a>
                        </li>
                        <% } %>
                    </ul>
                </div>
            </div>
        </nav>
    </head>
    <body>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Add active class to current page
            document.addEventListener('DOMContentLoaded', function() {
                const currentLocation = location.pathname;
                const navLinks = document.querySelectorAll('.nav-link, .btn');
                
                navLinks.forEach(link => {
                    if(link.getAttribute('href') && currentLocation.includes(link.getAttribute('href'))) {
                        link.classList.add('active');
                    }
                });
            });

            // Search functionality
            const searchInput = document.querySelector('input[type="search"]');
            if (searchInput) {
                searchInput.addEventListener('input', function () {
                    const searchTerm = this.value.toLowerCase();
                    console.log('Searching for:', searchTerm);
                    // Add your search logic here
                });
            }
        </script>
    </body>
</html>
