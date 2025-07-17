///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
// */
//package Controller;
//
//import DAO.AddressDAO;
//import DAO.CartDAO;
//import DAO.UserDAO;
//import Model.Address;
//import Model.Cart;
//import Model.User;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
///**
// *
// * @author Admin
// */
//@WebServlet("/Checkout")
//public class CheckoutController extends HttpServlet {
//
//    CartDAO cartdao = new CartDAO();
//    AddressDAO addressdao = new AddressDAO();
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            HttpSession session = request.getSession(false);
//            Model.User user = (Model.User) session.getAttribute("user");
//
//            if (user == null) {
//                response.sendRedirect("login.jsp");
//                return;
//            }
//
//            int userID = user.getUserID();
//            List<Cart> cartList = cartdao.getProductInCart(userID);
//            Address address = addressdao.getAddressByID(userID);
//            request.setAttribute("cart", cartList);
//            request.setAttribute("address", address);
//            request.setAttribute("user", user);
//            System.out.println("Cart: " + cartList);
//            System.out.println("Address: " + address);
//            System.out.println("User: " + user);
//            request.getRequestDispatcher("checkout.jsp").forward(request, response);
//        } catch (Exception e) {
//            System.out.println("Error:" + e);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String name = request.getParameter("userName");
//        String email = request.getParameter("userEmail");
//        String phone = request.getParameter("userPhone");
//        String address = request.getParameter("userAddress");
//        String totalStr = request.getParameter("total");
//
//        // Ghi log test thử
//        System.out.println("Checkout: " + name + ", " + email + ", " + totalStr);
//
//        // Tạm redirect về trang cảm ơn
//        response.sendRedirect("thankyou.jsp");
//    }
//}
package Controller;

import DAO.CartDAO;
import DAO.ProductDAO;
import DAO.UserDAO;
import DAO.AddressDAO;
import DAO.OrderDAO;
import Model.Address;
import Model.Cart;
import Model.Order;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;



@WebServlet("/Checkout")
public class CheckoutController extends HttpServlet {

    CartDAO cartDAO = new CartDAO();
    AddressDAO addressDAO = new AddressDAO();
    UserDAO userDAO = new UserDAO();
    ProductDAO productDAO = new ProductDAO();
    OrderDAO orderDAO = new OrderDAO();
//    OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");

            if ("edit".equals(action)) {

            } else if ("add".equals(action)) {

            } else if ("delete".equals(action)) {

            } else {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");
                
                System.out.println("User in session: " + user);

                if (user == null) {
                    response.sendRedirect("login.jsp");
                    return;
                }

                int userID = user.getUserID();
                List<Cart> cartList = cartDAO.getProductInCart(userID);
                Address address = addressDAO.getDefaultAddress(userID);
                System.out.println("Address from controller:" + address);

                request.setAttribute("cart", cartList);
                request.setAttribute("address", address);
                request.setAttribute("user", user);
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Khong hien thi checkout");
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
            float total = Float.parseFloat(request.getParameter("total"));

            // Cập nhật lại thông tin user
//            user.setUserPhone(phone);
//            user.setUserAddress(address);
//            userDAO.Update(user);
            // Tạo Order
            LocalDate now = LocalDate.now();
            Date orderDate = Date.valueOf(now);

            Order order = new Order();
            order.setUserID(user.getUserID());
            order.setOrderDate(orderDate);
            order.setOrderStatusID(1); // 1 = Pending
            order.setTotalPrice(total);
            order.setFinalPrice(total); // có thể trừ voucher sau
            order.setFullName(user.getFullName());
            order.setDeliveryAddress(address);
            order.setPhone(phone);

            orderDAO.createOrder(order);

//            // Tạo OrderDetails
//            List<Cart> cartList = cartDAO.getProductInCart(user.getUserID());
//            List<OrderDetail> orderDetails = new ArrayList<>();
//
//            for (Cart c : cartList) {
//                Product p = productDAO.getProductById(c.getProductID());
//                OrderDetail detail = new OrderDetail();
//                detail.setOrderID(orderID);
//                detail.setProductID(c.getProductID());
//                detail.setQuantity(c.getQuantity());
//                detail.setProductPrice(p.getPrice() * c.getQuantity());
//                detail.setSize(c.getSize());
//                orderDetails.add(detail);
//
//                // Giảm số lượng tồn kho (nếu cần)
////                productDAO.updateStockAfterPurchase(c.getProductID(), c.getQuantity());
//            }
//
//            orderDetailDAO.insertOrderDetails(orderDetails);
            // Xóa giỏ hàng sau khi mua
//            cartDAO.clearCartByUser(user.getUserID());
            // Điều hướng sang trang cảm ơn
            response.sendRedirect("thankyou.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi đặt hàng.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}
