<%@page import="Model.Cart"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.Product" %>
<%
    HttpSession s = request.getSession(false);
    Model.User u = (Model.User) (s != null ? s.getAttribute("user") : null);
    if (u == null || (u.getRoleID() != 3)) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Your Shopping Cart</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .cart-item-img {
                width: 80px;
                height: 80px;
                object-fit: cover;
            }

            .product-name {
                /*                max-width: 180px;
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                                display: inline-block;*/
            }

            .quantity-layout {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .quantity-layout span {
                min-width: 64px;         /* đảm bảo đủ chỗ cho 2 chữ số */
                height: 64px;            /* cố định chiều cao */
                line-height: 64px;       /* căn giữa chữ theo chiều dọc */
                text-align: center;      /* căn giữa ngang */
                /*                border: 1px solid #ccc;   tùy chọn: đường viền nhẹ 
                                border-radius: 4px;       bo góc nhẹ */
                background-color: #fff;  /* nền trắng */
                display: inline-block;
            }
            .total {
                min-width: 90px;
                text-align: right;
                display: inline-block;
            }
        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>
        <div class="container mt-5">
            <div class="row">
                <!-- Left side: Cart items -->
                <div class="col-md-8">
                    <h4 class="mb-4">🛒 Your Shopping Cart</h4>
                    <%
                        List<Cart> cart = (List<Cart>) request.getAttribute("cart");
                        String context = request.getContextPath();
                        if (cart == null || cart.isEmpty()) {
                    %>
                    <div class="alert alert-info">Your cart is empty.</div>
                    <%
                    } else {
                        int totalAll = 0;
                    %>
                    <div id="cart-container">
                        <ul class="list-group mb-3">
                            <%
                                for (Cart item : cart) {
                                    Product p = item.getProduct();
                                    long total = p.getPrice() * item.getQuantity();
                                    totalAll += total;
                            %>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <!-- Left: image + info -->
                                <div class="d-flex align-items-center" style="flex: 1">
                                    <img src="/HouseholdGoods_Group7_SWP391/images/<%= p.getImage()%>" class="cart-item-img me-3" alt="<%= p.getProductName()%>"/>
                                    <div>
                                        <strong class="product-name"><%= p.getProductName()%></strong><br>
                                        <small><%= String.format("%,d", p.getPrice())%>₫</small>
                                    </div>
                                </div>

                                <!-- Right: quantity + total + delete -->
                                <div class="d-flex align-items-center gap-4" style="flex-shrink: 0; min-width: 320px;">
                                    <div class="quantity-layout">
                                        <button class="btn btn-outline-secondary btn-sm quantity-btn" data-action="decrease" data-cart-id="<%= item.getCartID()%>">−</button>
                                        <span class="quantity-<%= item.getCartID()%>"><%= item.getQuantity()%></span>
                                        <button class="btn btn-outline-secondary btn-sm quantity-btn" data-action="increase" data-cart-id="<%= item.getCartID()%>">+</button>
                                    </div>
                                    <strong class="total text-nowrap" style="min-width: 90px; text-align: right;">
                                        <%= String.format("%,d", total)%>₫
                                    </strong>
                                    <a href="<%= context%>/Cart?action=delete&id=<%= item.getCartID()%>" 
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Are you sure you want to delete this product in cart?');">
                                        <i class="fas fa-trash"></i> Delete
                                    </a>
                                </div>
                            </li>
                            <% }%>
                        </ul>
                    </div>
                </div>

                <!-- Right side: Summary -->
                <div class="col-md-4">
                    <div class="summary-box" id="summary-box">
                        <div class="summary-box" id="summary-box">
                            <h5 class="mb-3">Summary</h5>
                            <p>ITEMS <span class="float-end"><%= String.format("%,d", totalAll)%>₫</span></p>

                            <label for="code">COUPON CODE</label>
                            <input type="text" id="code" class="form-control mb-3" placeholder="Enter your code">

                            <div class="total-line">
                                <strong>TOTAL PRICE</strong>
                                <span class="float-end"><%= String.format("%,d", totalAll)%>₫</span>
                            </div>

                            <button class="btn btn-dark w-100 mt-3">CHECKOUT</button>
                        </div>
                    </div>
                </div>
            </div>
            <% }%>
        </div>
        <%@ include file="footer.jsp" %>
    </body>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script>
                                           $(document).ready(function () {
                                               $(document).on("click", ".quantity-btn", function () {
                                                   const button = $(this);
                                                   const action = button.data("action");
                                                   const cartId = button.data("cart-id");

                                                   $.ajax({
                                                       url: "Cart",
                                                       method: "POST",
                                                       data: {
                                                           action: action,
                                                           cartID: cartId
                                                       },
                                                       success: function (data) {
                                                           // Cập nhật phần giỏ hàng
                                                           $("#cart-container").html($(data).find("#cart-container").html());

                                                           // Cập nhật phần tóm tắt đơn hàng
                                                           $("#summary-box").html($(data).find("#summary-box").html());
                                                       },
                                                       error: function () {
                                                           alert("Error updating cart.");
                                                       }
                                                   });
                                               });
                                           });
    </script>
</html>
