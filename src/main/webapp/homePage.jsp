<%@page import="Model.Product"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    List<Product> products = (List<Product>) request.getAttribute("productList");
    if (products == null) {
        products = (List<Product>) request.getAttribute("products");
    }
    String search = request.getParameter("search");
    String context = request.getContextPath();
%>
<!DOCTYPE html>
<html>
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
            .card-body a {
                text-decoration: none;
                font-weight: bold;
                font-size: 18px;
            }
            .hero-title {
                font-size: 3rem;
            }
            .hero-subtitle {
                font-size: 1.25rem;
            }
            .section-title {
                text-align: center;
                margin-top: 3rem;
                margin-bottom: 2rem;
                font-size: 2rem;
                font-weight: bold;
            }
            .product-card {
                border: 1px solid #dee2e6;
                padding: 10px;
                border-radius: 10px;
                background-color: #ffffff;
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
            .btn-layout {
                display: flex;
                gap: 10px;
                padding: 10px 15px;
                justify-content: center;
            }
            .btn-layout button {
                width: 100%;
                padding: 10px;
                font-weight: bold;
                border: none;
                border-radius: 8px;
            }
            .btn-add {
                background-color: #198754;
                color: white;
            }
            .btn-add:hover {
                background-color: #157347;
            }
            .btn-buy {
                background-color: #dc3545;
                color: white;
            }
            .btn-buy:hover {
                background-color: #bb2d3b;
            }
        </style>
    </head>
    <body>

        <%@ include file="header.jsp" %>

        <!-- Hero -->
        <section class="hero-section">
            <div class="container">
                <h1 class="hero-title">Premium Household Goods</h1>
                <p class="hero-subtitle">Discover quality, comfort, and style for your home</p>
                <a href="#products" class="btn btn-light btn-lg">
                    <i class="fas fa-shopping-bag me-2"></i>Shop Now
                </a>
            </div>
        </section>

        <!-- Carousel -->
        <div class="container my-5">
            <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
                <div class="carousel-inner">
                    <div class="carousel-item active">
                        <img src="https://images.unsplash.com/photo-1586023492125-27b2c045efd7" class="d-block w-100" style="height: 400px; object-fit: cover;" alt="Kitchen">
                    </div>
                    <div class="carousel-item">
                        <img src="https://images.unsplash.com/photo-1484154218962-a197022b5858" class="d-block w-100" style="height: 400px; object-fit: cover;" alt="Living Room">
                    </div>
                    <div class="carousel-item">
                        <img src="https://images.unsplash.com/photo-1616486338812-3dadae4b4ace" class="d-block w-100" style="height: 400px; object-fit: cover;" alt="Bedroom">
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

        <!-- Search form -->
        <form action="search" method="get" class="input-group mb-4 w-50 mx-auto">
            <input type="text" name="search" class="form-control" placeholder="Search products..." value="<%= (search != null) ? search : ""%>">
            <button type="submit" class="btn btn-outline-dark">Search</button>
        </form>
        <!-- Filter form -->
        <form action="filter" method="get" class="row g-2 mb-4 justify-content-center">
            <div class="col-auto">
                <input type="number" name="min" class="form-control form-control-sm" style="width: 140px;" placeholder="Min price">
            </div>
            <div class="col-auto">
                <input type="number" name="max" class="form-control form-control-sm" style="width: 140px;" placeholder="Max price">
            </div>
            <div class="col-auto">
                <select name="brand" class="form-select form-select-sm" style="width: 160px;">
                    <option value="">All Brands</option>
                    <option value="1">Panasonic</option>
                    <option value="2">Samsung</option>
                    <option value="3">LG</option>
                </select>
            </div>
            <div class="col-auto">
                <select name="category" class="form-select form-select-sm" style="width: 180px;">
                    <option value="">All Categories</option>
                    <option value="1">Refrigeration</option>
                    <option value="2">Kitchen Appliances</option>
                    <option value="3">Bedroom Appliances</option>
                </select>
            </div>
            <div class="col-auto">
                <button type="submit" class="btn btn-primary btn-sm">
                    <i class="fa fa-filter me-1"></i>Filter
                </button>
            </div>
        </form>

        <!-- Products Section -->
        <div class="container" id="products">
            <h2 class="section-title">Featured Products</h2>
            <div class="row">
                <% if (products != null && !products.isEmpty()) {
                        for (Product p : products) {%>
                <div class="col-lg-3 col-md-6 p-2">
                    <div class="card product-card h-100">
                        <img src="/HouseholdGoods_Group7_SWP391/images/<%= p.getImage()%>" class="card-img-top" alt="<%= p.getProductName()%>">
                        <div class="card-body">
                            <a class="card-title" href="<%= context%>/Product?action=productDetail&id=<%= p.getProductID()%>"><%= p.getProductName()%></a>
                            <p class="card-text"><%= p.getDescription()%></p>
                            <p class="price"><%= String.format("%,d", p.getPrice())%>₫</p>
                        </div>
                        <div class="btn-layout">
                            <form action="<%= context%>/Cart" method="get" class="d-inline">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productID" value="<%= p.getProductID()%>">
                                <input type="hidden" name="quantity" value="1">
                                <button type="submit" class="btn-add">Add to cart</button>
                            </form>
                            <form action="<%= context%>/Checkout" method="get" class="d-inline">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productID" value="<%= p.getProductID()%>">
                                <input type="hidden" name="quantity" value="1">
                                <button type="submit" class="btn-buy">Buy now</button>
                            </form>
                        </div>
                    </div>
                </div>
                <% }
                } else { %>
                <div class="text-center mt-5">
                    <p class="text-danger fs-5">No products found.</p>
                </div>
                <% }%>
            </div>
        </div>
        <div class="d-flex justify-content-center mt-4">
            <nav>
                <ul class="pagination">
                    <%
                        Integer currentPageAttr = (Integer) request.getAttribute("currentPage");
                        Integer totalPagesAttr = (Integer) request.getAttribute("totalPages");

                        int currentPage = (currentPageAttr != null) ? currentPageAttr : 1;
                        int totalPages = (totalPagesAttr != null) ? totalPagesAttr : 1;

                        // Nút Previous
                        if (currentPage > 1) {%>
                    <li class="page-item">
                        <a class="page-link" href="?page=<%= currentPage - 1%>">Previous</a>
                    </li>
                    <% } else { %>
                    <li class="page-item disabled"><span class="page-link">Previous</span></li>
                        <% }

                            // Các số trang
                            for (int i = 1; i <= totalPages; i++) {
                                if (i == currentPage) {%>
                    <li class="page-item active"><span class="page-link"><%= i%></span></li>
                        <%      } else {%>
                    <li class="page-item">
                        <a class="page-link" href="?page=<%= i%>"><%= i%></a>
                    </li>
                    <%      }
                        }

                        // Nút Next
                        if (currentPage < totalPages) {%>
                    <li class="page-item">
                        <a class="page-link" href="?page=<%= currentPage + 1%>">Next</a>
                    </li>
                    <% } else { %>
                    <li class="page-item disabled"><span class="page-link">Next</span></li>
                        <% } %>
                </ul>
            </nav>
        </div>

        <%@ include file="footer.jsp" %>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <%
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
        %>
        <script>
            window.alert("<%= successMessage%>");
        </script>
        <%
                session.removeAttribute("successMessage"); // Xóa để không lặp lại
            }
        %>

    </body>
</html>
