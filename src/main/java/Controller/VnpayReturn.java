/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.CartDAO;
import DAO.OrderDAO;
import DAO.OrderDetailDAO;
import DAO.ProductDAO;
import DAO.UserDAO;
import Model.Cart;
import Model.Order;
import Model.OrderDetail;
import Model.OrderInfo;
import Model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
@WebServlet("/vnpayReturn")
public class VnpayReturn extends HttpServlet {

    OrderDAO orderDao = new OrderDAO();
    CartDAO cartDAO = new CartDAO();
    UserDAO userDAO = new UserDAO();
    ProductDAO productDAO = new ProductDAO();
    OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            Map fields = new HashMap();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            String signValue = Config.hashAllFields(fields);
            if (signValue.equals(vnp_SecureHash)) {
                String paymentCode = request.getParameter("vnp_TransactionNo");

                String orderId = request.getParameter("vnp_TxnRef");

                OrderInfo order = new OrderInfo();
                order.setOrderID(Integer.parseInt(orderId));

                boolean transSuccess = false;
                if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                    // Chuyển orderId sang int để dùng thống nhất
                    int orderIdInt = Integer.parseInt(orderId);

                    // 1) Cập nhật trạng thái đã thanh toán
                    order.setOrderStatusID(7);
                    transSuccess = true;
                    orderDao.updateOrderStatus(order);

                    // 2) TRỪ KHO THEO ORDER DETAIL (KHÔNG theo giỏ)
                    List<OrderDetail> ods = orderDetailDAO.getOrderDetailsByOrderID(orderIdInt);
                    boolean ok = true;
                    for (OrderDetail d : ods) {
                        // YÊU CẦU: ProductDAO.updateStockAfterPurchase trả về boolean, dùng WHERE stonk_Quantity >= ?
                        boolean deducted = productDAO.updateStockAfterPurchase(d.getProductID(), d.getQuantity());
                        if (!deducted) {
                            ok = false;
                            break;
                        }
                    }
                    if (!ok) {
                        // Hết hàng bất ngờ -> revert trạng thái, báo thất bại
                        order.setOrderStatusID(6);
                        orderDao.updateOrderStatus(order);
                        request.setAttribute("transResult", false);
                        request.getRequestDispatcher("paymentResult.jsp").forward(request, response);
                        return;
                    }

                    // 3) CLEAR CÁC CART ITEM ĐÃ CHỌN CHO ĐƠN NÀY
                    @SuppressWarnings("unchecked")
                    Map<Integer, List<Integer>> mapSel = (Map<Integer, List<Integer>>) session.getAttribute("selected_cart_by_order");
                    List<Integer> selectedIds = (mapSel != null ? mapSel.get(orderIdInt) : null);

                    if (selectedIds != null && !selectedIds.isEmpty()) {
                        // Xoá theo cartID đã chọn
                        cartDAO.deleteCartItemsByIds(user.getUserID(), selectedIds);
                        // Dọn map để tránh clear nhầm lần sau
                        mapSel.remove(orderIdInt);
                        session.setAttribute("selected_cart_by_order", mapSel);
                    } else {
                        // Fallback: xoá theo productID trong OrderDetail (khi session mất)
                        List<Integer> productIds = new ArrayList<Integer>();
                        for (OrderDetail od : ods) {
                            int pid = od.getProductID();
                            if (!productIds.contains(pid)) {
                                productIds.add(pid);
                            }
                        }
                        // PHẢI dùng hàm xoá theo productID
                        cartDAO.deleteCartItemsByIds(user.getUserID(), productIds);
                    }
                } else {
                    order.setOrderStatusID(6);
                    orderDao.updateOrderStatus(order);
                }

                request.setAttribute("transResult", transSuccess);
                request.getRequestDispatcher("paymentResult.jsp").forward(request, response);
            } else {
                //RETURN PAGE ERROR
                System.out.println("GD KO HOP LE (invalid signature)");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(VnpayReturn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(VnpayReturn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
