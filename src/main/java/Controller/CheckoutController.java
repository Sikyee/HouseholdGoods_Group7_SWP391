package Controller;

import DAO.CartDAO;
import DAO.ProductDAO;
import DAO.UserDAO;
import DAO.AddressDAO;
import DAO.OrderDAO;
import DAO.OrderDetailDAO;
import DAO.VoucherDAO;
import Model.Address;
import Model.Cart;
import Model.Order;
import Model.OrderDetail;
import Model.OrderInfo;
import Model.Product;
import Model.Voucher;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@WebServlet("/Checkout")
public class CheckoutController extends HttpServlet {

    CartDAO cartDAO = new CartDAO();
    AddressDAO addressDAO = new AddressDAO();
    UserDAO userDAO = new UserDAO();
    ProductDAO productDAO = new ProductDAO();
    OrderDAO orderDAO = new OrderDAO();
    VoucherDAO voucherDAO;

    public CheckoutController() {
        try {
            this.voucherDAO = new VoucherDAO();
        } catch (Exception e) {
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String finalTotalParam = request.getParameter("finalTotal");
            String discountValueParam = request.getParameter("discountValue");

            if (finalTotalParam != null) {
                float finalTotal = Float.parseFloat(finalTotalParam);
                request.setAttribute("finalTotal", finalTotal);
            }
            if (discountValueParam != null) {
                float discountValue = Float.parseFloat(discountValueParam);
                request.setAttribute("discountValue", discountValue);
            }

            if ("add".equals(action)) {
                int productID = Integer.parseInt(request.getParameter("productID"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                Product product = productDAO.getProductById(productID);

                if (product == null) {
                    response.sendRedirect("Cart");
                    return;
                }

                List<Cart> cartList = new ArrayList<>();
                Cart temp = new Cart();
                temp.setProductID(productID);
                temp.setQuantity(quantity);
                temp.setProduct(product);
                cartList.add(temp);

                Address address = addressDAO.getDefaultAddress(user.getUserID());
                List<Voucher> allVouchers = voucherDAO.getAllVouchers(); // bạn đã có DAO sẵn
                List<Voucher> availableVouches = new ArrayList<>();
                for (Voucher promo : allVouchers) {
                    if (promo.isIsActive() && promo.getUsedCount() < promo.getMaxUsage()) {
                        availableVouches.add(promo);
                    }
                }

                request.setAttribute("cart", cartList);
                request.setAttribute("address", address);
                request.setAttribute("user", user);
                request.setAttribute("vouchers", availableVouches);
                request.setAttribute("isBuyNow", true); // FLAG để phân biệt
                request.getRequestDispatcher("checkout.jsp").forward(request, response);

            } else {
                int userID = user.getUserID();
                List<Cart> cartList = cartDAO.getProductInCart(userID);
                Address address = addressDAO.getDefaultAddress(userID);
                List<Voucher> allVouchers = voucherDAO.getAllVouchers(); // bạn đã có DAO sẵn
                List<Voucher> availableVouches = new ArrayList<>();
                for (Voucher promo : allVouchers) {
                    if (promo.isIsActive() && promo.getUsedCount() < promo.getMaxUsage()) {
                        availableVouches.add(promo);
                    }
                }

                request.setAttribute("cart", cartList);
                request.setAttribute("address", address);
                request.setAttribute("vouchers", availableVouches);
                request.setAttribute("user", user);
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi hiển thị trang Checkout: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) (session != null ? session.getAttribute("user") : null);
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            request.setCharacterEncoding("UTF-8");
            String action = request.getParameter("action"); // có thể là "fromCart" khi bấm CHECKOUT ở giỏ

            // ====== GIAI ĐOẠN: từ cart sang trang checkout (review) ======
            if ("fromCart".equals(action)) {
                String[] cartIds = request.getParameterValues("cartId");
                if (cartIds == null || cartIds.length == 0) {
                    // (tuỳ bạn) không chọn gì -> quay lại cart
                    response.sendRedirect(request.getContextPath() + "/Cart");
                    return;
                }

                List<String> errors = new ArrayList<>();
                List<Cart> selectedCarts = new ArrayList<>();

                for (String idStr : cartIds) {
                    int cartID = Integer.parseInt(idStr);
                    Cart c = cartDAO.getCartItemById(cartID);
                    if (c == null) {
                        continue;
                    }

                    // Hydrate product nếu thiếu
                    Product p = c.getProduct();
                    if (p == null) {
                        p = productDAO.getProductById(c.getProductID());
                        c.setProduct(p);
                    }

                    int qty = c.getQuantity();
                    int stock = (p != null ? p.getStonkQuantity() : 0);
                    if (qty > stock) {
                        errors.add("Sản phẩm \"" + (p != null ? p.getProductName() : ("#" + cartID))
                                + "\" chỉ còn " + stock + " sản phẩm, bạn đã chọn " + qty + ".");
                    }

                    selectedCarts.add(c);
                }

                if (!errors.isEmpty()) {
                    // Quay lại cart.jsp với lỗi
                    List<Cart> cartList = cartDAO.getProductInCart(user.getUserID());
                    // hydrate luôn để cart.jsp hiển thị đủ thông tin
                    for (Cart c : cartList) {
                        if (c.getProduct() == null) {
                            c.setProduct(productDAO.getProductById(c.getProductID()));
                        }
                    }
                    request.setAttribute("cart", cartList);
                    request.setAttribute("errors", errors);
                    request.getRequestDispatcher("cart.jsp").forward(request, response);
                    return;
                }

                // Tính subtotal server-side
                long subtotal = 0L;
                for (Cart c : selectedCarts) {
                    Product p = c.getProduct();
                    if (p != null) {
                        subtotal += (long) p.getPrice() * c.getQuantity();
                    }
                }

                // Chuẩn bị dữ liệu giống doGet()
                Address addr = addressDAO.getDefaultAddress(user.getUserID());
                List<Voucher> usable = new ArrayList<>();
                for (Voucher v : voucherDAO.getAllVouchers()) {
                    if (v.isIsActive() && v.getUsedCount() < v.getMaxUsage()) {
                        usable.add(v);
                    }
                }

                // (tuỳ chọn) lưu selection vào session nếu bạn dùng qua bước đặt hàng
                List<Integer> selectedIds = new ArrayList<>();
                for (String s : cartIds) {
                    selectedIds.add(Integer.parseInt(s));
                }
                session.setAttribute("checkout_cart_ids", selectedIds);

                // QUAN TRỌNG: luôn dùng cùng 1 tên attribute "cart"
                request.setAttribute("cart", selectedCarts);
                request.setAttribute("subtotal", subtotal);
                request.setAttribute("address", addr);
                request.setAttribute("vouchers", usable);
                request.setAttribute("user", user);
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
                return;
            }

            // ======== GIAI ĐOẠN 2: đặt hàng (submit từ checkout.jsp) ========
            String phone = request.getParameter("userPhone");
            String address = request.getParameter("userAddress");

            String pmRaw = request.getParameter("paymentMethod");
            if (pmRaw == null || pmRaw.isBlank()) {
                request.setAttribute("error", "Vui lòng chọn phương thức thanh toán.");
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
                return;
            }
            int paymentMethodID = Integer.parseInt(pmRaw);

            // build danh sách item đặt hàng
            boolean isBuyNow = request.getParameter("isBuyNow") != null;
            List<Cart> cartList;
            if (isBuyNow) {
                int productID = Integer.parseInt(request.getParameter("productID"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                Product p = productDAO.getProductById(productID);
                if (p == null || p.getStonkQuantity() < quantity) {
                    request.setAttribute("error", "Sản phẩm không đủ số lượng.");
                    request.getRequestDispatcher("checkout.jsp").forward(request, response);
                    return;
                }
                Cart c = new Cart();
                c.setProductID(productID);
                c.setQuantity(quantity);
                c.setProduct(p);
                cartList = new ArrayList<>();
                cartList.add(c);
            } else {
                // ưu tiên các cartId đã lưu ở bước review
                List<Integer> selectedIds = (List<Integer>) session.getAttribute("checkout_cart_ids");
                if (selectedIds != null && !selectedIds.isEmpty()) {
                    cartList = new ArrayList<>();
                    for (Cart c : cartDAO.getProductInCart(user.getUserID())) {
                        if (selectedIds.contains(c.getCartID())) {
                            cartList.add(c);
                        }
                    }
                } else {
                    cartList = cartDAO.getProductInCart(user.getUserID()); // fallback: all
                }
            }

            if (cartList == null || cartList.isEmpty()) {
                request.setAttribute("error", "Không có sản phẩm để đặt hàng.");
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
                return;
            }

            // subtotal server-side (không phụ thuộc client gửi 'total')
            long subtotal = 0L;
            for (Cart c : cartList) {
                Product p = c.getProduct() != null ? c.getProduct() : productDAO.getProductById(c.getProductID());
                if (p != null) {
                    subtotal += p.getPrice() * c.getQuantity();
                }
            }

            // voucher
            String promoIDStr = request.getParameter("promotionID");
            Voucher selectedVoucher = null;
            float discountValue = 0f;
            if (promoIDStr != null && !promoIDStr.isBlank()) {
                try {
                    int promoID = Integer.parseInt(promoIDStr);
                    selectedVoucher = voucherDAO.getVoucherById(promoID);
                    if (selectedVoucher != null && selectedVoucher.isIsActive()) {
                        if ("percentage".equalsIgnoreCase(selectedVoucher.getDiscountType())) {
                            discountValue = (float) (subtotal * selectedVoucher.getDiscountValue() / 100.0);
                        } else {
                            discountValue = selectedVoucher.getDiscountValue();
                        }
                        if (discountValue > subtotal) {
                            discountValue = (float) subtotal;
                        }
                    }
                } catch (NumberFormatException ignore) {
                }
            }

            float finalPrice = (float) subtotal - discountValue;
            if (finalPrice < 0) {
                finalPrice = 0;
            }

            // tạo order
            Timestamp orderDate = Timestamp.valueOf(java.time.LocalDateTime.now());
            OrderInfo order = new OrderInfo();
            order.setUserID(user.getUserID());
            order.setOrderStatusID(1);
            order.setOrderDate(orderDate);
            order.setPaymentMethodID(paymentMethodID);
            order.setVoucherID(selectedVoucher != null ? selectedVoucher.getVoucherID() : 0);
            order.setTotalPrice((float) subtotal);
            order.setFinalPrice(finalPrice);
            order.setFullName(user.getFullName());
            order.setDeliveryAddress(address);
            order.setPhone(phone);

            int orderID = orderDAO.createOrder(order);
            if (orderID < 1) {
                request.setAttribute("error", "Không thể tạo đơn hàng.");
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
                return;
            }

            List<OrderDetail> orderDetails = new ArrayList<>();
            for (Cart c : cartList) {
                Product p = c.getProduct() != null ? c.getProduct() : productDAO.getProductById(c.getProductID());
                if (p.getStonkQuantity() < c.getQuantity()) {
                    request.setAttribute("error", "Sản phẩm '" + p.getProductName() + "' không đủ số lượng.");
                    request.getRequestDispatcher("checkout.jsp").forward(request, response);
                    return;
                }
                OrderDetail d = new OrderDetail();
                d.setOrderID(orderID);
                d.setProductID(c.getProductID());
                d.setOrderName(p.getProductName());
                d.setQuantity(c.getQuantity());
                d.setTotalPrice(p.getPrice() * c.getQuantity());
                orderDetails.add(d);
            }
            new OrderDetailDAO().insertOrderDetails(orderDetails);

            if (selectedVoucher != null) {
                selectedVoucher.setUsedCount(selectedVoucher.getUsedCount() + 1);
                voucherDAO.updateVoucher(selectedVoucher);
            }

            if (paymentMethodID == 2) {
                // --- VNPAY PAYMENT LOGIC ---
                String bankCode = request.getParameter("bankCode");
                double amountDouble = finalPrice;

                String vnp_Version = "2.1.0";
                String vnp_Command = "pay";
                String orderType = "other";
                long amount = (long) (amountDouble * 100);
                String vnp_TxnRef = String.valueOf(orderID);
                String vnp_IpAddr = request.getRemoteAddr();

                String vnp_TmnCode = Config.vnp_TmnCode;

                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Version", vnp_Version);
                vnp_Params.put("vnp_Command", vnp_Command);
                vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
                vnp_Params.put("vnp_Amount", String.valueOf(amount));
                vnp_Params.put("vnp_CurrCode", "VND");

                if (bankCode != null && !bankCode.isEmpty()) {
                    vnp_Params.put("vnp_BankCode", bankCode);
                }

                vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
                vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn hàng: " + vnp_TxnRef);
                vnp_Params.put("vnp_OrderType", orderType);
                vnp_Params.put("vnp_Locale", "vn");
                vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
                vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

                Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String vnp_CreateDate = formatter.format(cld.getTime());
                vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

                cld.add(Calendar.MINUTE, 15);
                String vnp_ExpireDate = formatter.format(cld.getTime());
                vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

                List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
                Collections.sort(fieldNames);
                StringBuilder hashData = new StringBuilder();
                StringBuilder query = new StringBuilder();

                for (Iterator<String> it = fieldNames.iterator(); it.hasNext();) {
                    String fieldName = it.next();
                    String fieldValue = vnp_Params.get(fieldName);
                    if (fieldValue != null && fieldValue.length() > 0) {
                        hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                                .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                        if (it.hasNext()) {
                            hashData.append('&');
                            query.append('&');
                        }
                    }
                }

                String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
                query.append("&vnp_SecureHash=").append(vnp_SecureHash);
                String paymentUrl = Config.vnp_PayUrl + "?" + query.toString();

                @SuppressWarnings("unchecked")
                Map<Integer, List<Integer>> mapSel = (Map<Integer, List<Integer>>) session.getAttribute("selected_cart_by_order");
                if (mapSel == null) {
                    mapSel = new HashMap<>();
                }

// selectedIds: lấy từ session "checkout_cart_ids" (đã set ở bước review)
                List<Integer> selectedIds = (List<Integer>) session.getAttribute("checkout_cart_ids");
                if (selectedIds != null && !selectedIds.isEmpty()) {
                    mapSel.put(orderID, new ArrayList<>(selectedIds));
                    session.setAttribute("selected_cart_by_order", mapSel);
                }

                // Điều hướng sang VNPay
                response.sendRedirect(paymentUrl);
                return;
            } else {
                // COD: Trừ kho ở đây (nên làm trong transaction, nhưng ít nhất tách khỏi VNPay)
                for (OrderDetail d : orderDetails) {
                    boolean ok = productDAO.updateStockAfterPurchase(d.getProductID(), d.getQuantity());
                    if (!ok) {
                        request.setAttribute("error", "Sản phẩm không đủ số lượng.");
                        request.getRequestDispatcher("checkout.jsp").forward(request, response);
                        return;
                    }
                }
                if (!isBuyNow) {
                    List<Integer> selectedIds = (List<Integer>) session.getAttribute("checkout_cart_ids");
                    if (selectedIds != null && !selectedIds.isEmpty()) {
                        cartDAO.deleteCartItemsByIds(user.getUserID(), selectedIds);
                    } else {
                        // fallback cũ: cartDAO.clearCartByUser(user.getUserID()); // KHÔNG khuyến khích
                    }
                    session.removeAttribute("checkout_cart_ids");
                }

                session.setAttribute("successMessage", "Đơn hàng đã được đặt thành công!");
                response.sendRedirect(request.getContextPath() + "/");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi xử lý đơn hàng.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}
