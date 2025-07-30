package Controller;

import DAO.CartDAO;
import DAO.ProductDAO;
import DAO.UserDAO;
import DAO.AddressDAO;
import DAO.OrderDAO;
import DAO.OrderDetailDAO;
import Model.Address;
import Model.Cart;
import Model.OrderInfo;
import Model.OrderDetail;
import Model.Product;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

                request.setAttribute("cart", cartList);
                request.setAttribute("address", address);
                request.setAttribute("user", user);
                request.setAttribute("isBuyNow", true); // FLAG để phân biệt
                request.getRequestDispatcher("checkout.jsp").forward(request, response);

            } else {
                int userID = user.getUserID();
                List<Cart> cartList = cartDAO.getProductInCart(userID);
                Address address = addressDAO.getDefaultAddress(userID);

                request.setAttribute("cart", cartList);
                request.setAttribute("address", address);
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
            Date orderDate = Date.valueOf(now);

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

            // Tạo Order
            OrderInfo order = new OrderInfo();
            order.setUserID(user.getUserID());
            order.setOrderStatusID(1);

            order.setOrderDate(orderDate);
            order.setPaymentMethodID(paymentMethodID);
            order.setVoucherID(0);
            order.setTotalPrice(total);
            order.setFinalPrice(total);

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

            if (!isBuyNow) {
                cartDAO.clearCartByUser(user.getUserID());
            }

            session.setAttribute("successMessage", "Đơn hàng đã được đặt thành công!");
            response.sendRedirect(request.getContextPath() + "/");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi xử lý đơn hàng.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}
