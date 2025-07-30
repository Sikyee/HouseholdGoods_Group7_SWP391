/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.OrderDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 *
 * @author TriTM
 */
@WebServlet(name = "UpdateOrderStatusController", urlPatterns = {"/update-order-status"})
public class UpdateOrderStatusController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            int newStatusID = Integer.parseInt(request.getParameter("orderStatusID"));

            OrderDAO orderDAO = new OrderDAO();
            orderDAO.updateOrderStatus(orderID, newStatusID);

            // ✅ Redirect về lại trang chi tiết đơn hàng
            response.sendRedirect("order-detail?orderID=" + orderID);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("order.jsp"); // fallback nếu lỗi
        }
    }

}
