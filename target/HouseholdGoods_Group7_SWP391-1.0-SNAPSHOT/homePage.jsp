<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Household Goods - Premium Home Essentials</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
        }

        .hero-section {
            background-color: #6c757d;
            color: white;
            padding: 100px 0;
            text-align: center;
        }

        .hero-title {
            font-size: 3rem;
            margin-bottom: 1rem;
        }

        .hero-subtitle {
            font-size: 1.25rem;
            margin-bottom: 2rem;
        }

        .products-section {
            padding: 60px 0;
        }

        .section-title {
            text-align: center;
            margin-bottom: 3rem;
            font-size: 2.5rem;
        }

        .product-card {
            margin-bottom: 2rem;
            border: 1px solid #dee2e6;
        }

        .product-card img {
            height: 250px;
            object-fit: cover;
        }

        .price {
            font-size: 1.25rem;
            font-weight: bold;
            color: #007bff;
        }
    </style>
</head>
<body>
    <%@ include file="header.jsp" %>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <h1 class="hero-title">Premium Household Goods</h1>
            <p class="hero-subtitle">Discover quality, comfort, and style for your home</p>
            <button class="btn btn-primary btn-lg">
                <i class="fas fa-shopping-bag me-2"></i>Shop Now
            </button>
        </div>
    </section>

    <!-- Carousel -->
    <div class="container my-5">
        <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
            <div class="carousel-indicators">
                <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
                <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
                <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="2"></button>
            </div>
            <div class="carousel-inner">
                <div class="carousel-item active">
                    <img src="https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80" class="d-block w-100" alt="Modern Kitchen" style="height: 400px; object-fit: cover;">
                </div>
                <div class="carousel-item">
                    <img src="https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80" class="d-block w-100" alt="Living Room" style="height: 400px; object-fit: cover;">
                </div>
                <div class="carousel-item">
                    <img src="https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80" class="d-block w-100" alt="Bedroom" style="height: 400px; object-fit: cover;">
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
            <div class="row">
                <!-- 4 sản phẩm mẫu -->
                <div class="col-lg-3 col-md-6">
                    <div class="card product-card h-100">
                        <img src="https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Kitchen Set">
                        <div class="card-body">
                            <h5 class="card-title">Premium Kitchen Set</h5>
                            <p class="card-text">Modern design and premium quality materials.</p>
                            <p class="price">$299.99</p>
                        </div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-6">
                    <div class="card product-card h-100">
                        <img src="https://images.unsplash.com/photo-1555041469-a586c61ea9bc?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Living Room Decor">
                        <div class="card-body">
                            <h5 class="card-title">Living Room Decor</h5>
                            <p class="card-text">Enhance your space with style and comfort.</p>
                            <p class="price">$149.99</p>
                        </div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-6">
                    <div class="card product-card h-100">
                        <img src="https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Bedroom Essentials">
                        <div class="card-body">
                            <h5 class="card-title">Bedroom Essentials</h5>
                            <p class="card-text">Luxurious accessories for restful nights.</p>
                            <p class="price">$199.99</p>
                        </div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-6">
                    <div class="card product-card h-100">
                        <img src="https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" class="card-img-top" alt="Storage Solutions">
                        <div class="card-body">
                            <h5 class="card-title">Storage Solutions</h5>
                            <p class="card-text">Keep your home organized with smart storage.</p>
                            <p class="price">$89.99</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <%@ include file="footer.jsp" %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Basic search functionality
        const searchInput = document.querySelector('input[type="search"]');
        if (searchInput) {
            searchInput.addEventListener('input', function () {
                const searchTerm = this.value.toLowerCase();
                const productCards = document.querySelectorAll('.product-card');

                productCards.forEach(card => {
                    const title = card.querySelector('.card-title').textContent.toLowerCase();
                    const description = card.querySelector('.card-text').textContent.toLowerCase();

                    if (title.includes(searchTerm) || description.includes(searchTerm) || searchTerm === '') {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                });
            });
        }
    </script>
</body>
</html>
