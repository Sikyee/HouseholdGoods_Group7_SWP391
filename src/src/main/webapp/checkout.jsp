<%@page import="Model.User"%>
<%@page import="Model.Address"%>
<%@page import="Model.Cart"%>
<%@page import="Model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>

<%
    HttpSession s = request.getSession(false);
    Model.User u = (Model.User) (s != null ? s.getAttribute("user") : null);
    if (u == null || (u.getRoleID() != 3)) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Checkout</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <style>
            body {
                background: linear-gradient(to right, #f0f2f5, #e9ecef);
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            .checkout-wrapper {
                max-width: 1200px;
                margin: 40px auto;
                background: #fff;
                border-radius: 15px;
                box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
                padding: 40px;
            }
            h2 {
                font-weight: 700;
                color: #343a40;
                text-transform: uppercase;
                letter-spacing: 1px;
            }
            .form-control {
                border-radius: 10px;
                padding: 12px;
                border: 1px solid #ced4da;
                transition: 0.3s;
            }
            .form-control:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
            }
            .btn-primary {
                background: #007bff;
                border-radius: 10px;
                font-weight: 600;
                padding: 10px 25px;
                transition: 0.3s ease;
            }
            .btn-primary:hover {
                background: #0056b3;
                transform: translateY(-1px);
            }
            .btn-secondary {
                border-radius: 10px;
                font-weight: 600;
                padding: 10px 25px;
            }
            .cart-box {
                background: #f8f9fa;
                border-radius: 12px;
                padding: 20px;
                box-shadow: inset 0 0 10px rgba(0,0,0,0.05);
            }
            .cart-title {
                font-size: 20px;
                font-weight: 700;
                color: #495057;
                border-bottom: 2px solid #dee2e6;
                padding-bottom: 8px;
                margin-bottom: 15px;
            }
            .list-group-item {
                border: none;
                border-bottom: 1px solid #e9ecef;
                padding: 15px 10px;
                font-size: 15px;
                background: transparent;
            }
            .list-group-item:last-child {
                border-bottom: none;
            }
            .total-price {
                font-size: 20px;
                color: #e63946;
                font-weight: bold;
            }
            .payment-options label {
                margin-right: 15px;
                font-weight: 500;
                cursor: pointer;
            }
            .payment-options input[type="radio"] {
                margin-right: 6px;
            }
        </style>
    </head>
    <body>
        <%@ include file="header.jsp" %>

        <div class="checkout-wrapper">
            <h2 class="text-center mb-5">Checkout</h2>

            <%
                List<Cart> cart = (List<Cart>) request.getAttribute("cart");
                User user = (User) request.getAttribute("user");
                Address address = (Address) request.getAttribute("address");
                System.out.println("Address:" + address);
                int totalAll = 0;
            %>

            <div class="row">
                <!-- Information -->
                <div class="col-md-7">
                    <div class="mb-3">
                        <h5 class="cart-title">Your Information</h5>
                    </div>
                    <form action="Checkout" method="post">
                        <input type="hidden" name="total" value="<%= totalAll%>">

                        <div class="form-group">
                            <label><strong>Full Name</strong></label>
                            <input type="text" class="form-control" name="userName"
                                   value="<%= user != null ? user.getFullName() : ""%>" readonly>
                        </div>

                        <div class="form-group">
                            <label><strong>Email</strong></label>
                            <input type="email" class="form-control" name="userEmail"
                                   value="<%= user != null ? user.getEmail() : ""%>" readonly>
                        </div>

                        <div class="form-group">
                            <label><strong>Phone</strong></label>
                            <input type="text" class="form-control" name="userPhone"
                                   value="<%= address != null ? address.getPhone() : ""%>" required>
                        </div>

                        <div class="form-group">
                            <label><strong>Address</strong></label>
                            <input type="text" class="form-control" name="userAddress"
                                   value="<%= address != null ? address.getAddressDetail() : ""%>" required>
                        </div>
                        
                        <div class="form-group payment-options">
                            <label><strong>Payment Method</strong></label><br>
                            <label>
                                <input type="radio" name="paymentMethod" value="1" checked> Cash on Delivery (COD)
                            </label>
                            <label>
                                <input type="radio" name="paymentMethod" value="2"> Banking Transfer
                            </label>
                            <label>
                                <input type="radio" name="paymentMethod" value="3"> Credit Card
                            </label>
                        </div>

                        <div class="mt-4">
                            <button type="submit" class="btn btn-primary mr-2">Confirm & Checkout</button>
                            <a href="Cart" class="btn btn-secondary">Back to Cart</a>
                        </div>
                    </form>
                </div>

                <!-- Cart -->       
                <div class="col-md-5">
                    <div class="cart-box">
                        <h5 class="cart-title">Your Cart</h5>
                        <ul class="list-group mb-3">
                            <% if (cart != null) {
                                    for (Cart c : cart) {
                                        Product p = c.getProduct();
                                        long total = (long) (p.getPrice() * c.getQuantity());
                                        totalAll += total;
                            %>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <div>
                                    <%= p.getProductName()%> 
                                    <small class="text-muted">x <%= c.getQuantity()%></small>
                                </div>
                                <span><%= String.format("%,d", total)%>₫</span>
                            </li>
                            <% }
                                }%>
                            <li class="list-group-item d-flex justify-content-between">
                                <strong>Total</strong>
                                <span class="total-price"><%= String.format("%,d", totalAll)%>₫</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <%@ include file="footer.jsp" %>
    </body>
</html>
