<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Household Goods - Premium Home Essentials</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Playfair+Display:wght@400;500;600;700&display=swap" rel="stylesheet">
        <style>
            :root {
                --primary-color: #2563eb;
                --primary-dark: #1d4ed8;
                --secondary-color: #f59e0b;
                --accent-color: #8b5cf6;
                --dark-bg: #0f172a;
                --dark-card: #1e293b;
                --light-bg: #f8fafc;
                --text-light: #64748b;
                --text-dark: #0f172a;
                --gradient-1: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                --gradient-2: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
                --gradient-3: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
                --glass-bg: rgba(255, 255, 255, 0.1);
                --glass-border: rgba(255, 255, 255, 0.2);
            }

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Inter', sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                background-attachment: fixed;
                min-height: 100vh;
                overflow-x: hidden;
            }

            /* Animated Background */
            .animated-bg {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                z-index: -1;
                background: linear-gradient(45deg, #667eea, #764ba2, #f093fb, #f5576c);
                background-size: 400% 400%;
                animation: gradientShift 15s ease infinite;
            }

            @keyframes gradientShift {
                0% {
                    background-position: 0% 50%;
                }
                50% {
                    background-position: 100% 50%;
                }
                100% {
                    background-position: 0% 50%;
                }
            }

            /* Floating Particles */
            .particles {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                pointer-events: none;
                z-index: -1;
            }

            .particle {
                position: absolute;
                width: 4px;
                height: 4px;
                background: rgba(255, 255, 255, 0.6);
                border-radius: 50%;
                animation: float 6s ease-in-out infinite;
            }

            @keyframes float {
                0%, 100% {
                    transform: translateY(0px) rotate(0deg);
                    opacity: 0;
                }
                10% {
                    opacity: 1;
                }
                90% {
                    opacity: 1;
                }
                100% {
                    transform: translateY(-100vh) rotate(360deg);
                    opacity: 0;
                }
            }

            /* Header Styles */
            .navbar {
                background: rgba(15, 23, 42, 0.95) !important;
                backdrop-filter: blur(20px);
                border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                transition: all 0.3s ease;
            }

            .navbar-brand {
                font-family: 'Playfair Display', serif !important;
                font-weight: 700 !important;
                font-size: 2rem !important;
                background: linear-gradient(135deg, #667eea, #f093fb);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                text-shadow: none !important;
            }

            .navbar-nav .nav-link {
                color: rgba(255, 255, 255, 0.9) !important;
                font-weight: 500;
                transition: all 0.3s ease;
                position: relative;
                margin: 0 10px;
            }

            .navbar-nav .nav-link:hover {
                color: #f093fb !important;
                transform: translateY(-2px);
            }

            .navbar-nav .nav-link::after {
                content: '';
                position: absolute;
                bottom: -5px;
                left: 50%;
                transform: translateX(-50%);
                width: 0;
                height: 2px;
                background: linear-gradient(135deg, #667eea, #f093fb);
                transition: width 0.3s ease;
            }

            .navbar-nav .nav-link:hover::after {
                width: 100%;
            }

            /* Search Bar */
            .search-container {
                position: relative;
                overflow: hidden;
            }

            .form-control {
                background: rgba(255, 255, 255, 0.1) !important;
                border: 1px solid rgba(255, 255, 255, 0.2) !important;
                color: white !important;
                backdrop-filter: blur(10px);
                transition: all 0.3s ease;
            }

            .form-control:focus {
                background: rgba(255, 255, 255, 0.15) !important;
                border-color: #f093fb !important;
                box-shadow: 0 0 20px rgba(240, 147, 251, 0.3) !important;
                color: white !important;
            }

            .form-control::placeholder {
                color: rgba(255, 255, 255, 0.7) !important;
            }

            /* Buttons */
            .btn-modern {
                background: linear-gradient(135deg, #667eea, #764ba2);
                border: none;
                color: white;
                font-weight: 600;
                padding: 12px 24px;
                border-radius: 25px;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            .btn-modern::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(135deg, #f093fb, #f5576c);
                transition: left 0.3s ease;
                z-index: -1;
            }

            .btn-modern:hover::before {
                left: 0;
            }

            .btn-modern:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                color: white;
            }

            .btn-profile {
                background: linear-gradient(135deg, #4facfe, #00f2fe);
                border: none;
                color: white;
                font-weight: 600;
                padding: 8px 16px;
                border-radius: 20px;
                transition: all 0.3s ease;
                font-size: 0.85rem;
            }

            .btn-profile:hover {
                transform: translateY(-2px) scale(1.05);
                box-shadow: 0 8px 20px rgba(79, 172, 254, 0.4);
                color: white;
            }

            /* Hero Section */
            .hero-section {
                position: relative;
                height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                overflow: hidden;
            }

            .hero-content {
                text-align: center;
                color: white;
                z-index: 2;
                animation: fadeInUp 1s ease;
            }

            .hero-title {
                font-family: 'Playfair Display', serif;
                font-size: 4rem;
                font-weight: 700;
                margin-bottom: 1rem;
                background: linear-gradient(135deg, #ffffff, #f093fb);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }

            .hero-subtitle {
                font-size: 1.5rem;
                font-weight: 300;
                margin-bottom: 2rem;
                opacity: 0.9;
            }

            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(50px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* Carousel Enhancements */
            .carousel {
                border-radius: 20px;
                overflow: hidden;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                margin: 2rem 0;
            }

            .carousel-item img {
                height: 70vh;
                object-fit: cover;
                filter: brightness(0.8);
                transition: all 0.5s ease;
            }

            .carousel-item:hover img {
                filter: brightness(1);
                transform: scale(1.05);
            }

            /* Products Section */
            .products-section {
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(20px);
                border-radius: 30px;
                margin: 4rem 0;
                padding: 3rem;
                border: 1px solid rgba(255, 255, 255, 0.2);
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
            }

            .section-title {
                font-family: 'Playfair Display', serif;
                font-size: 3rem;
                font-weight: 600;
                text-align: center;
                margin-bottom: 3rem;
                color: white;
                position: relative;
            }

            .section-title::after {
                content: '';
                position: absolute;
                bottom: -10px;
                left: 50%;
                transform: translateX(-50%);
                width: 100px;
                height: 4px;
                background: linear-gradient(135deg, #667eea, #f093fb);
                border-radius: 2px;
            }

            /* Product Cards */
            .product-card {
                background: rgba(255, 255, 255, 0.95);
                border: none;
                border-radius: 20px;
                overflow: hidden;
                transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
                position: relative;
            }

            .product-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: linear-gradient(135deg, transparent, rgba(240, 147, 251, 0.1));
                opacity: 0;
                transition: opacity 0.3s ease;
                z-index: 1;
            }

            .product-card:hover::before {
                opacity: 1;
            }

            .product-card:hover {
                transform: translateY(-15px) scale(1.02);
                box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
            }

            .product-card img {
                height: 250px;
                object-fit: cover;
                transition: all 0.4s ease;
            }

            .product-card:hover img {
                transform: scale(1.1);
            }

            .card-body {
                padding: 1.5rem;
                position: relative;
                z-index: 2;
            }

            .card-title {
                font-weight: 600;
                font-size: 1.2rem;
                color: var(--text-dark);
                margin-bottom: 0.5rem;
            }

            .card-text {
                color: var(--text-light);
                line-height: 1.6;
            }

            .price {
                font-size: 1.3rem;
                font-weight: 700;
                background: linear-gradient(135deg, #667eea, #764ba2);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }

            /* Footer */
            footer {
                background: rgba(15, 23, 42, 0.95);
                backdrop-filter: blur(20px);
                border-top: 1px solid rgba(255, 255, 255, 0.1);
                margin-top: 4rem;
            }

            .footer-section {
                padding: 3rem 0;
            }

            .footer-title {
                font-family: 'Playfair Display', serif;
                font-size: 1.5rem;
                font-weight: 600;
                color: white;
                margin-bottom: 1.5rem;
                position: relative;
            }

            .footer-title::after {
                content: '';
                position: absolute;
                bottom: -8px;
                left: 0;
                width: 50px;
                height: 3px;
                background: linear-gradient(135deg, #667eea, #f093fb);
                border-radius: 2px;
            }

            .footer-link {
                color: rgba(255, 255, 255, 0.7);
                text-decoration: none;
                transition: all 0.3s ease;
                display: inline-block;
            }

            .footer-link:hover {
                color: #f093fb;
                transform: translateX(5px);
            }

            .footer-icon {
                color: #f093fb;
                margin-right: 10px;
                font-size: 1.1rem;
            }

            /* Responsive Design */
            @media (max-width: 768px) {
                .hero-title {
                    font-size: 2.5rem;
                }

                .hero-subtitle {
                    font-size: 1.2rem;
                }

                .section-title {
                    font-size: 2rem;
                }

                .products-section {
                    padding: 2rem 1rem;
                }
            }

            /* Loading Animation */
            .loading-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: linear-gradient(135deg, #667eea, #764ba2);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 9999;
                transition: opacity 0.5s ease;
            }

            .loader {
                width: 60px;
                height: 60px;
                border: 4px solid rgba(255, 255, 255, 0.3);
                border-top: 4px solid white;
                border-radius: 50%;
                animation: spin 1s linear infinite;
            }

            @keyframes spin {
                0% {
                    transform: rotate(0deg);
                }
                100% {
                    transform: rotate(360deg);
                }
            }

            /* Scroll to top button */
            .scroll-to-top {
                position: fixed;
                bottom: 30px;
                right: 30px;
                width: 50px;
                height: 50px;
                background: linear-gradient(135deg, #667eea, #f093fb);
                color: white;
                border: none;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.2rem;
                cursor: pointer;
                transition: all 0.3s ease;
                opacity: 0;
                visibility: hidden;
                z-index: 1000;
            }

            .scroll-to-top.visible {
                opacity: 1;
                visibility: visible;
            }

            .scroll-to-top:hover {
                transform: translateY(-5px) scale(1.1);
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
            }
        </style>
    </head>
    <body>
        <!-- Loading Overlay -->
        <div class="loading-overlay" id="loadingOverlay">
            <div class="loader"></div>
        </div>

        <!-- Animated Background -->
        <div class="animated-bg"></div>

        <!-- Floating Particles -->
        <div class="particles" id="particles"></div>

        <!-- Header -->
        <nav class="navbar navbar-expand-lg navbar-dark fixed-top">
            <div class="container-fluid">
                <a class="navbar-brand" href="#"><i class="fas fa-home me-2"></i>Household Goods</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNav">
                    <div class="search-container d-flex ms-auto me-3">
                        <input class="form-control me-2" type="search" placeholder="Search premium products..." aria-label="Search">
                        <button class="btn btn-modern" type="submit">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                    <ul class="navbar-nav d-flex flex-row align-items-center">
                        <!-- Link ??n trang khuy?n mãi -->
                        <li class="nav-item me-3">
                            <a class="nav-link" href="<%= request.getContextPath()%>/PromotionController?action=list">
                                <i class="fas fa-tags"></i>
                            </a>
                        </li>

                        <!-- Link ??n trang qu?n lý danh m?c -->
                        <li class="nav-item me-3">
                            <a class="nav-link" href="<%= request.getContextPath()%>/Category">
                                <i class="fas fa-th-large"></i>
                            </a>
                        </li>

                        <!-- Link ??n trang h? s? ng??i dùng -->
                        <li class="nav-item me-3">
                            <a href="<%= request.getContextPath()%>/profile" class="btn btn-profile">
                                <i class="fas fa-user me-1"></i>Profile
                            </a>
                        <li class="nav-item">
                            <a href="<%= request.getContextPath()%>/login" class="btn btn-modern">
                                <i class="fas fa-sign-in-alt me-1"></i>Login
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- Hero Section -->
        <section class="hero-section">
            <div class="hero-content">
                <h1 class="hero-title">Premium Household Goods</h1>
                <p class="hero-subtitle">Discover quality, comfort, and style for your home</p>
                <button class="btn btn-modern btn-lg">
                    <i class="fas fa-shopping-bag me-2"></i>Shop Now
                </button>
            </div>
        </section>

        <!-- Carousel Section -->
        <div class="container">
            <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
                <div class="carousel-indicators">
                    <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
                    <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
                    <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="2"></button>
                </div>
                <div class="carousel-inner">
                    <div class="carousel-item active">
                        <img src="https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80" class="d-block w-100" alt="Modern Kitchen">
                    </div>
                    <div class="carousel-item">
                        <img src="https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80" class="d-block w-100" alt="Living Room">
                    </div>
                    <div class="carousel-item">
                        <img src="https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80" class="d-block w-100" alt="Bedroom">
                    </div>
                </div>
                <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
                    <span class="carousel-control-prev-icon"></span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
                    <span class="carousel-control-next-icon"></span>
                </button>
            </div>
        </div>

        <!-- Products Section -->
        <div class="container">
            <section class="products-section">
                <h2 class="section-title">Featured Products</h2>
                <div class="row g-4">
                    <!-- Sample Products -->
                    <div class="col-lg-3 col-md-6">
                        <div class="card product-card h-100">
                            <img src="https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Kitchen Set">
                            <div class="card-body">
                                <h5 class="card-title">Premium Kitchen Set</h5>
                                <p class="card-text">Complete kitchen essentials with modern design and premium quality materials.</p>
                                <p class="price">$299.99</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-3 col-md-6">
                        <div class="card product-card h-100">
                            <img src="https://images.unsplash.com/photo-1555041469-a586c61ea9bc?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Living Room Decor">
                            <div class="card-body">
                                <h5 class="card-title">Living Room Decor</h5>
                                <p class="card-text">Elegant decorative items to enhance your living space with style and comfort.</p>
                                <p class="price">$149.99</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-3 col-md-6">
                        <div class="card product-card h-100">
                            <img src="https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Bedroom Essentials">
                            <div class="card-body">
                                <h5 class="card-title">Bedroom Essentials</h5>
                                <p class="card-text">Comfortable and luxurious bedroom accessories for the perfect night's sleep.</p>
                                <p class="price">$199.99</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-3 col-md-6">
                        <div class="card product-card h-100">
                            <img src="https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Storage Solutions">
                            <div class="card-body">
                                <h5 class="card-title">Storage Solutions</h5>
                                <p class="card-text">Smart and stylish storage options to keep your home organized and clutter-free.</p>
                                <p class="price">$89.99</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <!-- Footer -->
        <footer class="footer">
            <div class="container footer-section">
                <div class="row">
                    <div class="col-lg-4 col-md-6 mb-4">
                        <h5 class="footer-title">About Household Goods</h5>
                        <p class="text-white-50 mb-3">Premium quality home essentials at affordable prices</p>
                        <div class="mb-2">
                            <i class="fas fa-map-marker-alt footer-icon"></i>
                            <span class="text-white-50">203 Tran Hung Dao, District 5, Ho Chi Minh City</span>
                        </div>
                        <div class="mb-2">
                            <i class="fas fa-phone footer-icon"></i>
                            <span class="text-white-50">0708330289</span>
                        </div>
                        <div class="mb-2">
                            <i class="fas fa-envelope footer-icon"></i>
                            <span class="text-white-50">householdgoods@gmail.com</span>
                        </div>
                    </div>

                    <div class="col-lg-4 col-md-6 mb-4">
                        <h5 class="footer-title">Quick Links</h5>
                        <ul class="list-unstyled">
                            <li class="mb-2"><a href="#" class="footer-link">Home</a></li>
                            <li class="mb-2"><a href="#" class="footer-link">Products</a></li>
                            <li class="mb-2"><a href="#" class="footer-link">Accessories</a></li>
                            <li class="mb-2"><a href="#" class="footer-link">Brands</a></li>
                            <li class="mb-2"><a href="#" class="footer-link">About Us</a></li>
                        </ul>
                    </div>

                    <div class="col-lg-4 col-md-6 mb-4">
                        <h5 class="footer-title">Policies</h5>
                        <ul class="list-unstyled">
                            <li class="mb-2"><a href="#" class="footer-link">Bestseller Products</a></li>
                            <li class="mb-2"><a href="#" class="footer-link">Promotional Products</a></li>
                            <li class="mb-2"><a href="#" class="footer-link">Privacy Policy</a></li>
                            <li class="mb-2"><a href="#" class="footer-link">Terms of Service</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </footer>

        <!-- Scroll to Top Button -->
        <button class="scroll-to-top" id="scrollToTop">
            <i class="fas fa-arrow-up"></i>
        </button>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Loading animation
            window.addEventListener('load', function () {
                const loadingOverlay = document.getElementById('loadingOverlay');
                setTimeout(() => {
                    loadingOverlay.style.opacity = '0';
                    setTimeout(() => {
                        loadingOverlay.style.display = 'none';
                    }, 500);
                }, 1000);
            });

            // Floating particles
            function createParticle() {
                const particle = document.createElement('div');
                particle.classList.add('particle');
                particle.style.left = Math.random() * 100 + 'vw';
                particle.style.animationDuration = Math.random() * 6 + 4 + 's';
                particle.style.animationDelay = Math.random() * 2 + 's';
                document.getElementById('particles').appendChild(particle);

                setTimeout(() => {
                    particle.remove();
                }, 10000);
            }

            // Create particles periodically
            setInterval(createParticle, 500);

            // Scroll to top functionality
            const scrollToTopBtn = document.getElementById('scrollToTop');

            window.addEventListener('scroll', function () {
                if (window.pageYOffset > 300) {
                    scrollToTopBtn.classList.add('visible');
                } else {
                    scrollToTopBtn.classList.remove('visible');
                }
            });

            scrollToTopBtn.addEventListener('click', function () {
                window.scrollTo({
                    top: 0,
                    behavior: 'smooth'
                });
            });

            // Navbar scroll effect
            window.addEventListener('scroll', function () {
                const navbar = document.querySelector('.navbar');
                if (window.scrollY > 50) {
                    navbar.style.background = 'rgba(15, 23, 42, 0.98)';
                } else {
                    navbar.style.background = 'rgba(15, 23, 42, 0.95)';
                }
            });

            // Product card animation on scroll
            const observerOptions = {
                threshold: 0.1,
                rootMargin: '0px 0px -50px 0px'
            };

            const observer = new IntersectionObserver(function (entries) {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateY(0)';
                    }
                });
            }, observerOptions);

            // Observe all product cards
            document.addEventListener('DOMContentLoaded', function () {
                const productCards = document.querySelectorAll('.product-card');
                productCards.forEach(card => {
                    card.style.opacity = '0';
                    card.style.transform = 'translateY(30px)';
                    card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                    observer.observe(card);
                });
            });

            // Smooth scrolling for anchor links
            document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                anchor.addEventListener('click', function (e) {
                    e.preventDefault();
                    const target = document.querySelector(this.getAttribute('href'));
                    if (target) {
                        target.scrollIntoView({
                            behavior: 'smooth',
                            block: 'start'
                        });
                    }
                });
            });

            // Add ripple effect to buttons
            function createRipple(event) {
                const button = event.currentTarget;
                const circle = document.createElement('span');
                const diameter = Math.max(button.clientWidth, button.clientHeight);
                const radius = diameter / 2;

                circle.style.width = circle.style.height = `${diameter}px`;
                circle.style.left = `${event.clientX - button.offsetLeft - radius}px`;
                circle.style.top = `${event.clientY - button.offsetTop - radius}px`;
                circle.classList.add('ripple');

                const ripple = button.getElementsByClassName('ripple')[0];
                if (ripple) {
                    ripple.remove();
                }

                button.appendChild(circle);
            }

            // Add ripple effect CSS
            const rippleStyle = document.createElement('style');
            rippleStyle.textContent = `
                .ripple {
                    position: absolute;
                    border-radius: 50%;
                    background-color: rgba(255, 255, 255, 0.6);
                    transform: scale(0);
                    animation: ripple-animation 0.6s linear;
                    pointer-events: none;
                }
            
                @keyframes ripple-animation {
                    to {
                        transform: scale(4);
                        opacity: 0;
                    }
                }
            `;
            document.head.appendChild(rippleStyle);

            // Apply ripple effect to buttons
            document.querySelectorAll('.btn-modern, .btn-profile').forEach(button => {
                button.addEventListener('click', createRipple);
                button.style.position = 'relative';
                button.style.overflow = 'hidden';
            });

            // Parallax effect for hero section
            window.addEventListener('scroll', function () {
                const scrolled = window.pageYOffset;
                const heroSection = document.querySelector('.hero-section');
                if (heroSection) {
                    heroSection.style.transform = `translateY(${scrolled * 0.5}px)`;
                }
            });

            // Dynamic text animation
            function typeWriter(element, text, speed = 100) {
                let i = 0;
                element.innerHTML = '';

                function type() {
                    if (i < text.length) {
                        element.innerHTML += text.charAt(i);
                        i++;
                        setTimeout(type, speed);
                    }
                }

                type();
            }

            // Initialize animations when page loads
            window.addEventListener('load', function () {
                setTimeout(() => {
                    const heroTitle = document.querySelector('.hero-title');
                    const heroSubtitle = document.querySelector('.hero-subtitle');

                    if (heroTitle && heroSubtitle) {
                        typeWriter(heroTitle, 'Premium Household Goods', 80);
                        setTimeout(() => {
                            typeWriter(heroSubtitle, 'Discover quality, comfort, and style for your home', 50);
                        }, 2000);
                    }
                }, 1500);
            });

            // Add mouse follow effect
            let mouseX = 0;
            let mouseY = 0;
            let cursorX = 0;
            let cursorY = 0;

            document.addEventListener('mousemove', function (e) {
                mouseX = e.clientX;
                mouseY = e.clientY;
            });

            // Custom cursor
            const cursor = document.createElement('div');
            cursor.style.cssText = `
                position: fixed;
                width: 20px;
                height: 20px;
                background: linear-gradient(135deg, #667eea, #f093fb);
                border-radius: 50%;
                pointer-events: none;
                z-index: 9999;
                mix-blend-mode: difference;
                transition: transform 0.1s ease;
            `;
            document.body.appendChild(cursor);

            function updateCursor() {
                cursorX += (mouseX - cursorX) * 0.2;
                cursorY += (mouseY - cursorY) * 0.2;
                cursor.style.left = cursorX - 10 + 'px';
                cursor.style.top = cursorY - 10 + 'px';
                requestAnimationFrame(updateCursor);
            }
            updateCursor();

            // Add hover effects to interactive elements
            document.querySelectorAll('a, button, .product-card').forEach(element => {
                element.addEventListener('mouseenter', function () {
                    cursor.style.transform = 'scale(2)';
                });
                element.addEventListener('mouseleave', function () {
                    cursor.style.transform = 'scale(1)';
                });
            });

            // Add loading states for interactive elements
            document.querySelectorAll('.btn-modern').forEach(button => {
                button.addEventListener('click', function (e) {
                    if (!this.classList.contains('loading')) {
                        this.classList.add('loading');
                        const originalText = this.innerHTML;
                        this.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Loading...';

                        setTimeout(() => {
                            this.classList.remove('loading');
                            this.innerHTML = originalText;
                        }, 2000);
                    }
                });
            });

            // Add search functionality
            const searchInput = document.querySelector('input[type="search"]');
            if (searchInput) {
                searchInput.addEventListener('input', function () {
                    const searchTerm = this.value.toLowerCase();
                    const productCards = document.querySelectorAll('.product-card');

                    productCards.forEach(card => {
                        const title = card.querySelector('.card-title').textContent.toLowerCase();
                        const description = card.querySelector('.card-text').textContent.toLowerCase();

                        if (title.includes(searchTerm) || description.includes(searchTerm)) {
                            card.style.display = 'block';
                            card.style.opacity = '1';
                        } else if (searchTerm !== '') {
                            card.style.opacity = '0.3';
                        } else {
                            card.style.opacity = '1';
                        }
                    });
                });
            }
        </script>
    </body>
</html>