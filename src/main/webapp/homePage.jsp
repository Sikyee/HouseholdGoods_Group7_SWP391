<%@page import="Model.Product"%>
<%@page import="DAO.PromotionDAO"%>
<%@page import="Model.Promotion"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Lấy danh sách sản phẩm
    List<Product> products = (List<Product>) request.getAttribute("productList");
    if (products == null) {
        products = (List<Product>) request.getAttribute("products");
    }
    String search = request.getParameter("search");
    String context = request.getContextPath();

    // ===== Load promotions đang active và nhóm theo brandID =====
    Map<Integer, List<Promotion>> promosByBrand = new HashMap<>();
    try {
        PromotionDAO promoDao = new PromotionDAO();
        List<Promotion> promos = promoDao.getAllPromotions(); // WHERE isActive=1
        for (Promotion pr : promos) {
            Integer bid = pr.getBrandID(); // Promotion.brandID (đã map từ NVARCHAR)
            if (bid != null) {
                promosByBrand.computeIfAbsent(bid, k -> new java.util.ArrayList<>()).add(pr);
            }
        }
    } catch (Exception e) {
        request.setAttribute("promoError", e.getMessage());
    }
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
            .hero-title { font-size: 3rem; }
            .hero-subtitle { font-size: 1.25rem; }
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
            .price-old {
                text-decoration: line-through;
                color: #6c757d;
                margin: 0;
            }
            .price-new {
                font-size: 1.25rem;
                font-weight: bold;
                color: #dc3545;
            }
            .promo-badge {
                font-size: 0.85rem;
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
            .btn-add { background-color: #198754; color: white; }
            .btn-add:hover { background-color: #157347; }
            .btn-buy { background-color: #dc3545; color: white; }
            .btn-buy:hover { background-color: #bb2d3b; }
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

            <%
                String promoError = (String) request.getAttribute("promoError");
                if (promoError != null) {
            %>
            <div class="alert alert-warning text-center w-75 mx-auto">
                Promotion data failed to load: <%= promoError %>
            </div>
            <% } %>

            <div class="row">
                <% if (products != null && !products.isEmpty()) {
                       for (Product p : products) {

                           long price = p.getPrice();
                           long bestPrice = price;
                           Promotion bestPromo = null;

                           // Lưu ý: thay getBrandID() bằng tên getter đúng của bạn nếu khác
                           Integer productBrandId = null;
                           try { productBrandId = (Integer) Product.class.getMethod("getBrandID").invoke(p); }
                           catch (Exception ignore) {
                               try { productBrandId = (Integer) Product.class.getMethod("getBrandId").invoke(p); } catch (Exception e) { /* no-op */ }
                           }

                           List<Promotion> brandPromos = (productBrandId == null) ? null : promosByBrand.get(productBrandId);
                           if (brandPromos != null) {
                               for (Promotion pr : brandPromos) {
                                   long discounted = price;
                                   if ("percentage".equalsIgnoreCase(pr.getDiscountType())) {
                                       discounted = Math.round(price * (100.0 - pr.getDiscountValue()) / 100.0);
                                   } else if ("fixed".equalsIgnoreCase(pr.getDiscountType())) {
                                       discounted = Math.max(0L, price - pr.getDiscountValue());
                                   }
                                   if (discounted < bestPrice) {
                                       bestPrice = discounted;
                                       bestPromo = pr;
                                   }
                               }
                           }
                %>
                <div class="col-lg-3 col-md-6 p-2">
                    <div class="card product-card h-100">
                        <img src="/HouseholdGoods_Group7_SWP391/images/<%= p.getImage()%>" class="card-img-top" alt="<%= p.getProductName()%>">

   <!--wishlist-->             
        <form action="<%= request.getContextPath() %>/Wishlist" method="post" class="wishlist-form">
            <input type="hidden" name="action" value="add"/>
            <input type="hidden" name="productID" value="<%= p.getProductID() %>"/>
            <button type="submit" class="wishlist-btn">♡️</button>
        </form>

                        <div class="card-body">
                            <a class="card-title" href="<%= context%>/Product?action=productDetail&id=<%= p.getProductID()%>"><%= p.getProductName()%></a>
                            <p class="card-text"><%= p.getDescription()%></p>

                            <% if (bestPromo != null && bestPrice < price) {
                                   String promoLabel;
                                   if ("percentage".equalsIgnoreCase(bestPromo.getDiscountType())) {
                                       promoLabel = "-" + bestPromo.getDiscountValue() + "%";
                                   } else {
                                       promoLabel = "-" + String.format("%,d", bestPromo.getDiscountValue()) + "₫";
                                   }
                            %>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="price-old"><%= String.format("%,d", price) %>₫</span>
                                    <span class="price-new"><%= String.format("%,d", bestPrice) %>₫</span>
                                    <span class="badge bg-danger promo-badge"><%= promoLabel %></span>
                                </div>
                            <% } else { %>
                                <p class="price"><%= String.format("%,d", price)%>₫</p>
                            <% } %>
                        </div>

                        <!--Rating trung binh-->
                        <% Double avgRating = p.getAverageRating(); %>
<% if (avgRating != null && avgRating > 0) { %>
    <span class="text-warning">
        <% for (int i = 1; i <= 5; i++) { %>
            <% if (i <= avgRating) { %>
                <i class="fas fa-star"></i>
            <% } else if (i - avgRating < 1) { %>
                <i class="fas fa-star-half-alt"></i>
            <% } else { %>
                <i class="far fa-star"></i>
            <% } %>
        <% } %>
    </span>
    <small>(<%= String.format("%.1f", avgRating) %>/5)</small>
<% } else { %>
    <small class="text-muted">No rating yet</small>
<% } %>
<% System.out.println("ProductID: " + p.getProductID() + ", Average Rating: " + avgRating); %>

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

        <!-- Pagination -->
        <div class="d-flex justify-content-center mt-4">
            <nav>
                <ul class="pagination">
                    <%
                        int currentPage = (int) request.getAttribute("currentPage");
                        int totalPages = (int) request.getAttribute("totalPages");

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

<!--css cua Wishlist-->
 <style> 
  .product-card {
    border: 1px solid #dee2e6;
    padding: 10px;
    border-radius: 10px;
    background-color: #ffffff;
    position: relative; /* cần cho icon overlay */
}

.wishlist-form {
    position: absolute;
    top: 10px;
    left: 10px;
    z-index: 10;
}

.wishlist-btn {
    border: 2px solid red;      /* viền đỏ */
    background-color: black;     /* nền đen */
    color: red;                  /* trái tim đỏ */
    font-size: 28px;
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    border-radius: 6px;          /* bo góc nhẹ */
    display: none;               /* ẩn mặc định */
}

.product-card:hover .wishlist-btn {
    display: flex;               /* hiện khi hover */
}

</style>

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
                session.removeAttribute("successMessage");
            }
        %>

    </body>
</html>
