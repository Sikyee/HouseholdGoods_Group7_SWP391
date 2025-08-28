<%@page import="Model.ReplyFeedback"%>
<%@page import="Model.Feedback"%>
<%@page import="Model.Attribute"%>
<%@page import="java.util.List"%>
<%@page import="Model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Product Detail</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f8f9fa;
                color: #333;
            }

            /* Container chính chia layout trên dưới */
            .main-container {
                display: flex;
                flex-direction: column;
                min-height: 100vh;
                max-width: 1200px;
                margin: 0 auto;
            }

            /* Phần trên: ảnh + info sản phẩm */
            .product-detail-row {
                display: flex;
                justify-content: center;
                align-items: flex-start;
                background-color: #fff;
                padding: 40px;
                border-radius: 16px;
                box-shadow: 0 6px 24px rgba(0,0,0,0.08);
                flex: 1 1 50%; /* chiếm nửa trên */
                margin-bottom: 20px;
            }

            /* Ảnh sản phẩm */
            .product-image {
                width: 80%;
                max-width: 500px;
                border-radius: 10px;
                object-fit: cover;
                box-shadow: 0 2px 12px rgba(0,0,0,0.1);
            }

            /* Thông tin sản phẩm */
            .product-info {
                width: 50%;
                padding-left: 30px;
            }

            .product-info h2 {
                font-size: 2rem;
                font-weight: bold;
                color: #222;
                margin-bottom: 15px;
            }

            .product-info p {
                font-size: 1rem;
                line-height: 1.6;
            }

            .price {
                font-size: 1.8rem;
                font-weight: bold;
                color: #e60023;
                margin: 15px 0;
            }

            .quantity-layout {
                display: flex;
                align-items: center;
                overflow: hidden;
                max-width: 150px;
            }

            .quantity-layout button {
                padding: 8px 12px;
                border: none;
                background-color: #f1f1f1;
                font-weight: bold;
                font-size: 16px;
                cursor: pointer;
                transition: background-color 0.2s;
            }

            .quantity-layout button:hover {
                background-color: #ddd;
            }

            .quantity-layout input {
                width: 50px;
                text-align: center;
                border: none;
                font-size: 16px;
                padding: 6px;
            }

            .form-layout {
                display: flex;
                flex-direction: row;
            }

            form button {
                margin-top: 20px;
                margin-right: 10px;
                padding: 10px 20px;
                font-size: 1rem;
                border-radius: 6px;
                border: none;
                cursor: pointer;
                transition: background-color 0.3s ease;
            }

            form .btn-primary {
                background-color: #e60023;
                color: #fff;
            }

            form .btn-primary:hover {
                background-color: #cc001f;
            }

            form .btn-outline-dark {
                background-color: #fff;
                border: 1px solid #333;
                color: #333;
            }

            form .btn-outline-dark:hover {
                background-color: #333;
                color: #fff;
            }

            hr {
                margin: 30px 0;
                border-top: 1px solid #ddd;
            }

            .tag {
                display: inline-block;
                background-color: #eee;
                padding: 6px 12px;
                margin-right: 10px;
                border-radius: 20px;
                font-size: 0.85rem;
                color: #555;
            }

            /* Phần Feedback chiếm nửa dưới màn hình */
            .feedback-section {
                flex: 1 1 50%; /* chiếm nửa dưới */
                background-color: #f9f9f9;
                padding: 20px;
                border-radius: 16px;
                box-shadow: 0 6px 24px rgba(0,0,0,0.05);
                overflow-y: auto;
                max-height: 50vh;
            }

            .feedback-section h3 {
                margin-bottom: 15px;
            }

            .feedback {
                background-color: #fff;
                padding: 15px;
                margin-bottom: 15px;
                border-radius: 12px;
                box-shadow: 0 2px 12px rgba(0,0,0,0.05);
            }

        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>
        <%
            Product product = (Product) request.getAttribute("productDetail");
            String context = request.getContextPath();
        %>
        <div class="container main-container mt-5">
            <!-- Phần trên: Product detail -->
            <div class="row product-detail-row">
                <div class="col-md-6">
                    <img src="/HouseholdGoods_Group7_SWP391/images/<%= product.getImage()%>" class="product-image mb-3">
                </div>
                <div class="col-md-6 product-info">
                    <h2><%= product.getProductName()%></h2>
                    <p><%= product.getDescription()%></p>
                    <div class="price"><%= String.format("%,d", product.getPrice())%>₫</div>
                    <%
                        List<Attribute> attrs = product.getAttributes();
                        if (attrs != null && !attrs.isEmpty()) {
                    %>
                    <div class="attribute-list">
                        <ul>
                            <% for (Attribute attr : attrs) {%>
                            <li><strong><%= attr.getAttributeName()%>:</strong> <%= attr.getAttributeValue()%></li>
                            <% } %>
                        </ul>
                    </div>
                    <% }%>
                    <hr>
                    <ul>
                        <li><strong>Brand:</strong> 100% recycled polyester</li>
                        <li><strong>Category:</strong></li>
                    </ul>
                    <hr>
                    <div class="input-group mb-3 quantity-layout">
                        <button type="button" class="btn btn-outline-secondary" onclick="changeQty(-1)">-</button>
                        <input type="number" id="qty-display" class="form-control text-center" value="1" min="1" oninput="syncQty()" readonly="">
                        <button type="button" class="btn btn-outline-secondary" onclick="changeQty(1)">+</button>
                    </div>
                    <div class="form-layout">
                        <form action="<%= context %>/Cart" method="get" class="mt-3" onsubmit="syncQty(); return validateForm();">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productID" value="<%= product.getProductID()%>">
                            <input type="hidden" name="quantity" id="qty-addtocart" value="1">
                            <button type="submit" class="btn btn-outline-dark">Add to Cart</button>
                        </form>
                        <form action="<%= context %>/Cart" method="get" class="mt-3" onsubmit="syncQty(); return validateForm();">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productID" value="<%= product.getProductID()%>">
                            <input type="hidden" name="quantity" id="qty-buynow" value="1">
                            <button type="submit" class="btn btn-primary">Buy Now</button>             
                        </form>
                    </div>
                </div>
            </div>

            <!-- Phần dưới: Feedback -->
            <div class="feedback-section">
                <h3>Customer Feedback</h3>
                <%
                    List<Feedback> feedbacks = (List<Feedback>) request.getAttribute("feedbackList");
                    if (feedbacks != null && !feedbacks.isEmpty()) {
                        java.util.Collections.sort(feedbacks, new java.util.Comparator<Feedback>() {
                            public int compare(Feedback a, Feedback b) {
                                return b.getCreatedAt().compareTo(a.getCreatedAt());
                            }
                        });
                        for (Feedback fb : feedbacks) {
                %>
                <div class="feedback">
                    <p><strong><%= fb.getUserName() %>:</strong> <%= fb.getComment() %></p>
                   <p>
    Rating:
    <% 
        int rating = fb.getRating(); // ví dụ 4
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) { %>
                <span style="color: gold;">&#9733;</span> <% // sao vàng
            } else { %>
                <span style="color: #ccc;">&#9733;</span> <% // sao xám
            }
        }
    %>
</p>

                    <p class="text-muted" style="font-size:0.85rem;">Time: <%= fb.getCreatedAt() %></p>
                </div>
                <%
                        }
                    } else { 
                %>
                <p>No feedback yet.</p>
                <% } %>
            </div>
        </div>
        <%@ include file="footer.jsp" %>
    </body>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script>
        function changeQty(delta) {
            const display = document.getElementById("qty-display");
            let val = parseInt(display.value) || 1;
            val = Math.max(1, val + delta);
            display.value = val;
            syncQty();
        }
        function syncQty() {
            const val = document.getElementById("qty-display").value;
            document.getElementById("qty-addtocart").value = val;
            document.getElementById("qty-buynow").value = val;
        }
        function validateForm() {
            const qty = parseInt(document.getElementById("qty-display").value);
            if (isNaN(qty) || qty < 1) {
                alert("Số lượng không hợp lệ. Vui lòng chọn ít nhất 1 sản phẩm.");
                return false;
            }
            const maxQty = 100;
            if (qty > maxQty) {
                alert("Số lượng không được vượt quá " + maxQty);
                return false;
            }
            return true;
        }
    </script>
</html>
