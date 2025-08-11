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
                List<Voucher> allPromotions = voucherDAO.getAllVouchers(); // bạn đã có DAO sẵn
                List<Voucher> availablePromotions = new ArrayList<>();
                for (Voucher promo : allPromotions) {
                    if (promo.isIsActive() && promo.getUsedCount() < promo.getMaxUsage()) {
                        availablePromotions.add(promo);
                    }
                }

                request.setAttribute("cart", cartList);
                request.setAttribute("address", address);
                request.setAttribute("user", user);
                request.setAttribute("vouchers", availablePromotions);
                request.setAttribute("isBuyNow", true); // FLAG để phân biệt
                request.getRequestDispatcher("checkout.jsp").forward(request, response);

            } else {
                int userID = user.getUserID();
                List<Cart> cartList = cartDAO.getProductInCart(userID);
                Address address = addressDAO.getDefaultAddress(userID);
                List<Voucher> allPromotions = voucherDAO.getAllVouchers(); // bạn đã có DAO sẵn
                List<Voucher> availablePromotions = new ArrayList<>();
                for (Voucher promo : allPromotions) {
                    if (promo.isIsActive() && promo.getUsedCount() < promo.getMaxUsage()) {
                        availablePromotions.add(promo);
                    }
                }

                request.setAttribute("cart", cartList);
                request.setAttribute("address", address);
                request.setAttribute("vouchers", availablePromotions);
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
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String phone = request.getParameter("userPhone");
            String address = request.getParameter("userAddress");
            int paymentMethodID = Integer.parseInt(request.getParameter("paymentMethod"));
            float total = Float.parseFloat(request.getParameter("total"));
            LocalDate now = LocalDate.now();
            Timestamp orderDate = Timestamp.valueOf(LocalDateTime.now());

            boolean isBuyNow = request.getParameter("isBuyNow") != null;

            List<Cart> cartList;
            if (isBuyNow) {
                int productID = Integer.parseInt(request.getParameter("productID"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                Product p = productDAO.getProductById(productID);
                if (p.getStonkQuantity() < quantity) {
                    request.setAttribute("error", "Sản phẩm '" + p.getProductName() + "' không đủ số lượng trong kho.");
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
                cartList = cartDAO.getProductInCart(user.getUserID());
            }

            // Xử lý voucher
            String promoIDStr = request.getParameter("promotionID");
            Voucher selectedVoucher = null;
            float discountValue = 0f;

            if (promoIDStr != null && !promoIDStr.isEmpty()) {
                try {
                    int promoID = Integer.parseInt(promoIDStr);
                    selectedVoucher = voucherDAO.getVoucherById(promoID);

                    if (selectedVoucher != null && selectedVoucher.isIsActive()) {
                        if ("percentage".equalsIgnoreCase(selectedVoucher.getDiscountType())) {
                            discountValue = total * selectedVoucher.getDiscountValue() / 100.0f;
                            System.out.println("Selected voucher khi discount:" + selectedVoucher.getDiscountValue());
                            System.out.println("Sau khi discount:" + discountValue);
                        } else {
                            discountValue = selectedVoucher.getDiscountValue();
                            System.out.println("Selected voucher khi discount:" + selectedVoucher.getDiscountValue());
                            System.out.println("Sau khi discount:" + discountValue);
                        }

                        if (discountValue > total) {
                            discountValue = total;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            float finalPrice = total - discountValue;
            if (finalPrice < 0) {
                finalPrice = 0;
            }

            // Tạo đơn hàng
            OrderInfo order = new OrderInfo();
            order.setUserID(user.getUserID());
            order.setOrderStatusID(1);
            order.setOrderDate(orderDate);
            order.setPaymentMethodID(paymentMethodID);
            order.setVoucherID(selectedVoucher != null ? selectedVoucher.getVoucherID() : 0);
            order.setTotalPrice(total);
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
                Product p = productDAO.getProductById(c.getProductID());
                if (p.getStonkQuantity() < c.getQuantity()) {
                    request.setAttribute("error", "Sản phẩm '" + p.getProductName() + "' không đủ số lượng.");
                    request.getRequestDispatcher("checkout.jsp").forward(request, response);
                    return;
                }

                OrderDetail detail = new OrderDetail();
                detail.setOrderID(orderID);
                detail.setProductID(c.getProductID());
                detail.setOrderName(p.getProductName());
                detail.setQuantity(c.getQuantity());
                detail.setTotalPrice(p.getPrice() * c.getQuantity());
                orderDetails.add(detail);

                productDAO.updateStockAfterPurchase(c.getProductID(), c.getQuantity());
            }

            new OrderDetailDAO().insertOrderDetails(orderDetails);

            // Cập nhật lượt dùng của mã giảm giá
            if (selectedVoucher != null) {
                selectedVoucher.setUsedCount(selectedVoucher.getUsedCount() + 1);
                voucherDAO.updateVoucher(selectedVoucher);
            }
            System.out.println("Final total: " + total);
            System.out.println("Final price (after discount): " + finalPrice);
            System.out.println("Payment method ID: " + paymentMethodID);
            System.out.println("Is Buy Now: " + isBuyNow);

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

                // Điều hướng sang VNPay
                response.sendRedirect(paymentUrl);
                return;
            } else {
                if (!isBuyNow) {
                    cartDAO.clearCartByUser(user.getUserID());
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
