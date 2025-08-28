<%@page import="Model.Cart"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.Product" %>
<%@page import="DAO.PromotionDAO"%>
<%@page import="Model.Promotion"%>
<%
    Map<Integer, List<Promotion>> promosByBrand = new HashMap<>();
    try {
        PromotionDAO promoDao = new PromotionDAO();
        List<Promotion> promos = promoDao.getAllPromotions(); // active
        for (Promotion pr : promos) {
            Integer bid = pr.getBrandID();
            if (bid != null) {
                List<Promotion> list = promosByBrand.get(bid);
                if (list == null) {
                    list = new ArrayList<Promotion>();
                    promosByBrand.put(bid, list);
                }
                list.add(pr);
            }
        }
    } catch (Exception e) {
        // optional: show alert
    }
%>

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

            .fa-trash{
                color: red;
            }
        </style>
    </head>
    <body>
        <% List<String> errors = (List<String>) request.getAttribute("errors");
            if (errors != null && !errors.isEmpty()) { %>
        <div class="alert alert-danger">
            <ul>
                <% for (String err : errors) {%>
                <li><%= err%></li>
                    <% } %>
            </ul>
        </div>
        <% }%>
        <%@ include file="header.jsp" %>
        <div class="container-fluid mt-5">
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
            <form id="checkout-selected-form" action="<%= request.getContextPath()%>/Checkout" method="post">
                <input type="hidden" name="action" value="fromCart" />

                <div class="row">
                    <div class="col-md-8" style="display:flex;flex-direction:column;">
                        <div class="checkBox-all mb-2">
                            <input type="checkbox" id="checkAll">
                            <label for="checkAll">Choose All</label>
                        </div>

                        <div id="cart-container">
                            <ul class="list-group mb-3">
                                <%
                                    for (Cart item : cart) {
                                        Product p = item.getProduct();
                                        long baseUnit = p.getPrice();
                                        long effectiveUnit = baseUnit;
                                        Promotion bestPromo = null;

                                        Integer brandId = null;
                                        try {
                                            brandId = (Integer) Product.class.getMethod("getBrandID").invoke(p);
                                        } catch (Exception ignore) {
                                        }

                                        if (brandId != null) {
                                            List<Promotion> brandPromos = promosByBrand.get(brandId);
                                            if (brandPromos != null) {
                                                long best = baseUnit;
                                                for (Promotion pr : brandPromos) {
                                                    long discounted = baseUnit;
                                                    if ("percentage".equalsIgnoreCase(pr.getDiscountType())) {
                                                        discounted = Math.round(baseUnit * (100.0 - pr.getDiscountValue()) / 100.0);
                                                    } else if ("fixed".equalsIgnoreCase(pr.getDiscountType())) {
                                                        discounted = Math.max(0L, baseUnit - pr.getDiscountValue());
                                                    }
                                                    if (discounted < best) {
                                                        best = discounted;
                                                        bestPromo = pr;
                                                    }
                                                }
                                                effectiveUnit = best;
                                            }
                                        }

                                        long lineTotal = effectiveUnit * item.getQuantity();
                                %>
                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                    <!-- Checkbox chọn -->
                                    <input
                                        type="checkbox"
                                        class="select-item"
                                        name="cartId"                           
                                        value="<%= item.getCartID()%>"
                                        data-unit="<%= effectiveUnit%>"
                                        data-qty ="<%= item.getQuantity()%>"
                                        />

                                    <div class="d-flex align-items-center" style="flex:1">
                                        <img src="/HouseholdGoods_Group7_SWP391/images/<%= p.getImage()%>" class="cart-item-img me-3" alt="<%= p.getProductName()%>"/>
                                        <div>
                                            <div style="display:flex;gap:10px">
                                                <strong class="product-name"><%= p.getProductName()%></strong>
                                                <span>(Quantity: <%= p.getStonkQuantity()%>)</span>
                                            </div>
                                            <% if (bestPromo != null && effectiveUnit < baseUnit) {
                                                    String promoLabel = "percentage".equalsIgnoreCase(bestPromo.getDiscountType())
                                                            ? ("-" + bestPromo.getDiscountValue() + "%")
                                                            : ("-" + String.format("%,d", bestPromo.getDiscountValue()) + "₫");
                                            %>
                                            <div class="d-flex align-items-center gap-2">
                                                <small class="text-decoration-line-through text-muted"><%= String.format("%,d", baseUnit)%>₫</small>
                                                <small class="fw-bold text-danger"><%= String.format("%,d", effectiveUnit)%>₫</small>
                                                <span class="badge bg-danger promo-badge"><%= promoLabel%></span>
                                            </div>
                                            <% } else {%>
                                            <small><%= String.format("%,d", baseUnit)%>₫</small>
                                            <% }%>
                                        </div>
                                    </div>

                                    <div class="d-flex align-items-center gap-4" style="flex-shrink:0;min-width:320px;">
                                        <!-- Trong list item -->
                                        <div class="quantity-layout">
                                            <button type="button"
                                                    class="btn btn-outline-secondary btn-sm quantity-btn btn-decrease"
                                                    data-action="decrease"
                                                    data-cart-id="<%= item.getCartID()%>"
                                                    <%= (item.getQuantity() <= 1 ? "disabled" : "")%>>
                                                −
                                            </button>

                                            <span class="quantity-<%= item.getCartID()%>"><%= item.getQuantity()%></span>

                                            <button type="button"
                                                    class="btn btn-outline-secondary btn-sm quantity-btn"
                                                    data-action="increase"
                                                    data-cart-id="<%= item.getCartID()%>">+</button>
                                        </div>

                                        <strong class="total text-nowrap" style="min-width: 90px; text-align:right;">
                                            <%= String.format("%,d", lineTotal)%>₫
                                        </strong>
                                        <a href="<%= context%>/Cart?action=delete&id=<%= item.getCartID()%>"
                                           onclick="return confirm('Are you sure you want to delete this product in cart?');">
                                            <i class="fas fa-trash"></i>
                                        </a>
                                    </div>
                                </li>
                                <% }%>
                            </ul>
                        </div>
                    </div>

                    <!-- Summary -->
                    <div class="col-md-4">
                        <div class="summary-box" id="summary-box">
                            <h4 class="mb-3">Summary</h4>
                            <p>ITEMS SELECTED
                                <span class="float-end" id="selected-subtotal">0₫</span>
                            </p>
                            <div class="total-line">
                                <strong>TOTAL PRICE</strong>
                                <span class="float-end" id="selected-total">0₫</span>
                            </div>

                            <button type="submit" class="btn btn-dark w-100 mt-3" id="checkout-btn">
                                CHECKOUT
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            <% }%>
        </div>
        <%@ include file="footer.jsp" %>
    </body>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script>
                                               // ====== Format & Tính tổng ======
                                               function formatVND(n) { return n.toLocaleString('vi-VN') + '₫'; }

                                               function recalcSelected() {
                                               let subtotal = 0;
                                               document.querySelectorAll('.select-item:checked').forEach(cb => {
                                               const unit = parseInt(cb.dataset.unit || '0', 10);
                                               const qty = parseInt(cb.dataset.qty || '0', 10);
                                               subtotal += unit * qty;
                                               });
                                               document.getElementById('selected-subtotal').textContent = formatVND(subtotal);
                                               document.getElementById('selected-total').textContent = formatVND(subtotal);
                                               // Không disable nút nữa vì giờ chỉ còn một nút checkout
                                               }

                                               // ====== Chọn tất cả ======
                                               const checkAll = document.getElementById('checkAll');
                                               checkAll?.addEventListener('change', function () {
                                               document.querySelectorAll('.select-item').forEach(cb => cb.checked = checkAll.checked);
                                               recalcSelected();
                                               });
                                               // Chọn từng món
                                               document.querySelectorAll('.select-item').forEach(cb => {
                                               cb.addEventListener('change', function () {
                                               const items = document.querySelectorAll('.select-item');
                                               const checked = document.querySelectorAll('.select-item:checked');
                                               checkAll.checked = (items.length > 0 && checked.length === items.length);
                                               recalcSelected();
                                               });
                                               });
                                               // ====== Trước khi submit checkout: nếu không chọn gì -> chọn tất cả ======
                                               document.getElementById('checkout-selected-form')?.addEventListener('submit', function (e) {
                                               const checked = document.querySelectorAll('.select-item:checked');
                                               if (checked.length === 0) {
                                               document.querySelectorAll('.select-item').forEach(cb => cb.checked = true);
                                               }
                                               // Không cần gì thêm, server /Checkout vẫn nhận mảng "cartId"
                                               });
                                               // ====== AJAX tăng/giảm số lượng ======
                                               $(document).on('click', '.quantity-btn', function (e) {
                                               e.preventDefault();
                                               const $btn = $(this);
                                               const id = $btn.data('cart-id');
                                               const action = $btn.data('action');
                                               $.post('<%= request.getContextPath()%>/Cart', {
                                               action: action,
                                                       cartID: id,
                                                       ajax: '1'
                                               }, function (res) {
                                               if (!res || !res.ok) {
                                               alert(res?.message || 'Cập nhật thất bại');
                                               return;
                                               }

                                               const $row = $btn.closest('li.list-group-item');
                                               const $cb = $row.find('.select-item');
                                               const $dec = $row.find('.btn-decrease');
                                               // Quantity
                                               $('.quantity-' + id).text(res.quantity);
                                               $cb.attr('data-qty', res.quantity);
                                               // Cập nhật unit sau KM (để summary dùng đúng)
                                               if (typeof res.unit === 'number') {
                                               $cb.attr('data-unit', res.unit);
                                               }

                                               // Line total hiển thị
                                               $row.find('.total').text(res.itemTotalFmt);
                                               // Disable/enable nút giảm
                                               if (res.canDecrease === false) {
                                               $dec.prop('disabled', true);
                                               } else {
                                               $dec.prop('disabled', false);
                                               }

                                               recalcSelected();
                                               }, 'json').fail(function () {
                                               alert('Có lỗi mạng khi cập nhật số lượng');
                                               });
                                               });
                                               // Khởi tính
                                               recalcSelected();
    </script>
</html>