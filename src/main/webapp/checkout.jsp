<%@page import="java.util.HashMap"%>
<%@page import="Model.Promotion"%>
<%@page import="DAO.PromotionDAO"%>
<%@page import="java.util.Map"%>
<%@page import="java.time.LocalDate"%>
<%@page import="Model.Voucher"%>
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
                background: #fff;
                border-radius: 15px;
                padding: 25px 30px;
            }

            .cart-title {
                font-size: 20px;
                font-weight: 700;
                color: #343a40;
                margin-bottom: 20px;
            }

            .list-group-item {
                border: none !important;
                padding: 12px 0;
                font-size: 15px;
                background: transparent;
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

            select.form-control:disabled {
                background-color: #e9ecef;
                color: #6c757d;
            }

            #promoSelect {
                padding: 5px 12px;
                border-radius: 8px;
                border: 1px solid #ced4da;
                font-size: 14px;
                color: #495057;
                background-color: #fff;
                transition: border-color 0.3s, box-shadow 0.3s;
            }

            #promoSelect option{
                width: 10px;
            }

            #promoSelect:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
            }

            label[for="promoSelect"] {
                font-size: 15px;
                color: #343a40;
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
                List<Voucher> vouchers = (List<Voucher>) request.getAttribute("vouchers");
                Integer selectedPromotionID = (Integer) request.getAttribute("selectedPromotionID");
                System.out.println("Address:" + address);
                Float finalTotal = (Float) request.getAttribute("finalTotal");
                Float discountValue = (Float) request.getAttribute("discountValue");
                System.out.println("discount value:" + selectedPromotionID);

                // subtotal sau promotion từ server (ưu tiên)
                Long subtotalAttr = (Long) request.getAttribute("subtotal");
                long totalAll = (subtotalAttr != null) ? subtotalAttr.longValue() : 0L;

                // build map promotion theo brand (để hiển thị old/new price)
                Map<Integer, java.util.List<Promotion>> promosByBrand = new HashMap<>();
                try {
                    PromotionDAO promoDao = new PromotionDAO();
                    java.util.List<Promotion> promos = promoDao.getAllPromotions();

                    // Java 7: thay groupingBy bằng vòng for
                    promosByBrand = new java.util.HashMap<Integer, java.util.List<Promotion>>();
                    for (Promotion pr : promos) {
                        Integer brandId = pr.getBrandID();
                        if (brandId == null) {
                            continue;
                        }

                        java.util.List<Promotion> list = promosByBrand.get(brandId);
                        if (list == null) {
                            list = new java.util.ArrayList<Promotion>();
                            promosByBrand.put(brandId, list);
                        }
                        list.add(pr);
                    }
                } catch (Exception e) {
                    // Tránh "nuốt" lỗi hoàn toàn để còn lần theo khi cần
                    // Ít nhất nên log, và gán map rỗng để không NPE ở nơi khác
                    // java.util.logging (có sẵn JDK):
                    java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.WARNING,
                            "Failed to build promosByBrand", e);

                    promosByBrand = new java.util.HashMap<Integer, java.util.List<Promotion>>();
                }
            %>
            <%!
                // helper: tính đơn giá đã áp KM tốt nhất theo brand
                long bestUnit(long base, Integer brandId, java.util.Map<Integer, java.util.List<Model.Promotion>> promosByBrand) {
                    if (brandId == null || promosByBrand == null) {
                        return base;
                    }
                    java.util.List<Model.Promotion> list = promosByBrand.get(brandId);
                    if (list == null || list.isEmpty()) {
                        return base;
                    }
                    long best = base;
                    for (Model.Promotion pr : list) {
                        long discounted = base;
                        String t = pr.getDiscountType();
                        if ("percentage".equalsIgnoreCase(t)) {
                            discounted = Math.round(base * (100.0 - pr.getDiscountValue()) / 100.0);
                        } else if ("fixed".equalsIgnoreCase(t)) {
                            discounted = Math.max(0L, base - pr.getDiscountValue());
                        }
                        if (discounted < best) {
                            best = discounted;
                        }
                    }
                    return best;
                }
            %>
            <div class="row">
                <!-- Cart -->       
                <div class="col-md-5">
                    <div class="cart-box">
                        <h5 class="cart-title">Your Cart</h5>
                        <ul class="list-group mb-3">
                            <%                                if (cart != null) {
                                    for (Cart c : cart) {
                                        Product p = c.getProduct();
                                        long base = p.getPrice();
                                        Integer brandId = p.getBrandID();
                                        long unitEff = bestUnit(base, brandId, promosByBrand);
                                        long lineTotal = unitEff * c.getQuantity();
                            %>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <div>
                                    <%= p.getProductName()%>
                                    <small class="text-muted">x <%= c.getQuantity()%></small>
                                    <div>
                                        <% if (unitEff < base) {%>
                                        <small class="text-muted" style="text-decoration: line-through;">
                                            <%= String.format("%,d", base)%>₫
                                        </small>
                                        <small class="text-danger font-weight-bold">
                                            <%= String.format("%,d", unitEff)%>₫
                                        </small>
                                        <% } else {%>
                                        <small><%= String.format("%,d", base)%>₫</small>
                                        <% }%>
                                    </div>
                                </div>
                                <span><%= String.format("%,d", lineTotal)%>₫</span>
                            </li>
                            <%
                                    }
                                }
                            %>
                            <li class="list-group-item">
                                <label for="promoSelect" class="font-weight-bold mb-2 d-block">🎁 Apply Voucher</label>
                                <select id="promoSelect" name="promotionID" class="form-control">
                                    <option disabled <%= (selectedPromotionID == null) ? "selected" : ""%>>-- Select a voucher --</option>
                                    <%
                                        // today as LocalDate
                                        LocalDate today = LocalDate.now();

                                        if (vouchers != null) {
                                            for (Voucher voucher : vouchers) {
                                                java.sql.Date endSqlDate = voucher.getEndDate(); // có thể null
                                                LocalDate end = (endSqlDate != null) ? endSqlDate.toLocalDate() : null;

                                                boolean validTotal = (totalAll >= voucher.getMinOrderValue());
                                                boolean validDate = (end != null) && !today.isAfter(end); // today <= end

                                                boolean isValid = validTotal && validDate;

                                                boolean isSelected = (selectedPromotionID != null)
                                                        && (selectedPromotionID == voucher.getVoucherID());
                                    %>
                                    <option value="<%= voucher.getVoucherID()%>"
                                            data-discount="<%= voucher.getDiscountValue()%>"
                                            data-discount-type="<%= voucher.getDiscountType()%>"
                                            <%= !isValid ? "disabled style='color:gray;'" : ""%>
                                            <%= isSelected ? "selected" : ""%>>
                                        <%= voucher.getDescription()%>
                                    </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </li>

                            <!-- Add hr here -->
                            <li class="list-group-item px-0">
                                <hr class="my-3">
                            </li>

                            <%
                                long discount = 0;
                                if (discountValue
                                        != null) {
                                    discount = discountValue.longValue();
                                } else {
                                    discount = 0;
                                }
                                long finalAmount = totalAll - discount;
                                if (finalAmount < 0)
                                    finalAmount = 0;
                            %>


                            <li class="list-group-item d-flex justify-content-between">
                                <strong class="text-success">Discount</strong>
                                <span class="text-success">-<%= String.format("%,d", discount)%>₫</span>
                            </li>


                            <li class="list-group-item d-flex justify-content-between">
                                <strong>Total</strong>
                                <span class="total-price"><%= String.format("%,d", finalAmount)%>₫</span>
                            </li>
                        </ul>
                    </div>
                </div>

                <!-- Information -->
                <div class="col-md-7">
                    <div class="mb-3">
                        <h5 class="cart-title">Your Information</h5>
                    </div>
                    <form action="Checkout" method="post">
                        <% Boolean isBuyNow = (Boolean) request.getAttribute("isBuyNow"); %>
                        <% if (isBuyNow != null && isBuyNow) {%>
                        <input type="hidden" name="isBuyNow" value="true" />
                        <input type="hidden" name="productID" value="<%= cart.get(0).getProductID()%>" />
                        <input type="hidden" name="quantity" value="<%= cart.get(0).getQuantity()%>" />

                        <% }%>
                        <input type="hidden" id="selectedPromoInput" name="promotionID" value="<%= selectedPromotionID != null ? selectedPromotionID : ""%>">
                        <input type="hidden" name="discountValue" value="<%= discount%>">
                        <input type="hidden" name="finalTotal" id="finalTotal" value="<%= finalAmount%>">
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
                        </div>

                        <div class="mt-4">
                            <button type="submit" class="btn btn-primary mr-2">Confirm & Checkout</button>
                            <a href="Cart" class="btn btn-secondary">Back to Cart</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%@ include file="footer.jsp" %>
    </body>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const promoSelect = document.getElementById("promoSelect");
            const discountSpan = document.querySelector(".list-group-item .text-success:last-child");
            const totalSpan = document.querySelector(".total-price");

            let originalTotal = <%= totalAll%>;
            console.log(originalTotal);



            promoSelect.addEventListener("change", function () {
                const selectedOption = this.options[this.selectedIndex];
                const discountType = selectedOption.getAttribute("data-discount-type");
                const discountID = selectedOption.getAttribute("value");
                const discountValue = parseFloat(selectedOption.getAttribute("data-discount")) || 0;

                let discount = 0;
                if (discountType === "percentage") {
                    discount = Math.floor(originalTotal * (discountValue / 100));
                } else if (discountType === "fixed") {
                    discount = Math.floor(discountValue);
                }

                let finalTotal = originalTotal - discount;
                if (finalTotal < 0)
                    finalTotal = 0;

                // Update UI
                discountSpan.textContent = "-" + discount.toLocaleString("vi-VN") + "₫";
                totalSpan.textContent = finalTotal.toLocaleString("vi-VN") + "₫";

                // Update hidden inputs
                document.getElementById("finalTotal").value = finalTotal;
                document.getElementById("selectedPromoInput").value = discountID;
            });
        });
    </script>
</html>
