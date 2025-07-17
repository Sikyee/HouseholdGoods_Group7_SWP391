<%@page import="Model.Attribute"%>
<%@page import="java.util.List"%>
<%@page import="Model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f8f9fa;
                color: #333;
            }
            
            .container .row {
                display: flex;
                justify-content: center;
            }

            /* Row chính chứa ảnh và thông tin */
            .product-detail-row {
                display: flex; /* để hai cột (ảnh + info) nằm ngang */
                justify-content: center; /* căn giữa nội dung bên trong */
                align-items: center;

                background-color: #fff;
                padding: 40px;
                border-radius: 16px;
                box-shadow: 0 6px 24px rgba(0,0,0,0.08);

                max-width: 900px; /* chỉnh theo ý muốn (800-1200px) */
                width: 100%;
                margin: 0 auto; /* căn giữa container */
            }

            /* Ảnh sản phẩm */
            .product-image {
                width: 50%;
                max-width: 500px;
                border-radius: 10px;
                object-fit: cover;
                box-shadow: 0 2px 12px rgba(0,0,0,0.1);
            }

            /* Thông tin sản phẩm */
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

            /* Badge đánh giá */
            .badge {
                font-size: 0.9rem;
                padding: 6px 12px;
                border-radius: 20px;
            }

            /* Giá sản phẩm */
            .price {
                font-size: 1.8rem;
                font-weight: bold;
                color: #e60023;
                margin: 15px 0;
            }

            /* Layout chọn số lượng */
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

            /* Nút submit form */
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

            /* Nút Buy Now */
            form .btn-primary {
                background-color: #e60023;
                color: #fff;
            }

            form .btn-primary:hover {
                background-color: #cc001f;
            }

            /* Nút Add to Cart */
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

            /* Tag sản phẩm */
            .tag {
                display: inline-block;
                background-color: #eee;
                padding: 6px 12px;
                margin-right: 10px;
                border-radius: 20px;
                font-size: 0.85rem;
                color: #555;
            }
        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>
        <%
            Product product = (Product) request.getAttribute("productDetail");
            String context = request.getContextPath();
        %>
        <div class="container mt-5" style="display: flex; justify-content: center">
            <div class="row product-detail-row">
                <!-- Product Image -->
                <div class="col-md-6">
                    <img src="/HouseholdGoods_Group7_SWP391/images/<%= product.getImage()%>" class="product-image mb-3">
                    <!--                <div class="d-flex">
                                        images list
                                    </div>-->
                </div>

                <!-- Product Info -->
                <div class="col-md-6">
                    <h2><%= product.getProductName()%></h2>
                    <p><%= product.getDescription()%></p>
                    <!--                <div class="mb-2">
                                        <span class="badge bg-warning text-dark">★ 4.5</span> (89 reviews)
                                    </div>-->
                    <div class="price"><%= String.format("%,d", product.getPrice())%>₫</div>
                    <%
                        List<Attribute> attrs = product.getAttributes();
//                        System.out.println("DEBUG ATTR SIZE = " + (attrs == null ? "null" : attrs.size()));
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
                    <!--                    <p>
                                            <span class="tag">Women</span>
                                            <span class="tag">Blazers</span>
                                            <span class="tag">Formal</span>
                                        </p>-->
                    <div class="input-group mb-3 quantity-layout">
                        <button type="button" class="btn btn-outline-secondary" onclick="changeQty(-1)">-</button>
                        <input type="number" id="qty-display" class="form-control text-center" value="1" min="1" oninput="syncQty()" readonly="">
                        <button type="button" class="btn btn-outline-secondary" onclick="changeQty(1)">+</button>
                    </div>
                    <div class="form-layout">
                        <form action="<%= request.getContextPath()%>/Cart" method="get" class="mt-3" onsubmit="syncQty(); return validateForm();">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productID" value="<%= product.getProductID()%>">
                            <input type="hidden" name="quantity" id="qty-addtocart" value="1">
                            <button type="submit" class="btn btn-outline-dark">Add to Cart</button>
                        </form>
                        <form action="<%= request.getContextPath()%>/Cart" method="get" class="mt-3" onsubmit="syncQty(); return validateForm();">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productID" value="<%= product.getProductID()%>">
                            <input type="hidden" name="quantity" id="qty-buynow" value="1">
                            <button type="submit" class="btn btn-primary">Buy Now</button>             
                        </form>
                    </div>
                </div>
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
                                syncQty(); // Đồng bộ sau khi thay đổi
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
                                    return false; // Ngăn submit form
                                }

                                // ✅ Nếu bạn muốn giới hạn tồn kho (ví dụ max = 100)
                                const maxQty = 100;
                                if (qty > maxQty) {
                                    alert("Số lượng không được vượt quá " + maxQty);
                                    return false;
                                }

                                return true; // Cho phép submit
                            }

    </script>
</html>
