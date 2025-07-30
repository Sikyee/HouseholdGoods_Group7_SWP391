/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.OrderDAO;
import DAO.CancelReasonDAO;
import Model.CancelReason;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
/**
 *
 * @author TriTM
 */


@WebServlet(name = "CancelOrderController", urlPatterns = {"/cancel-order"})
public class CancelOrderController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer role = (Integer) session.getAttribute("role");
        Integer userID = (Integer) session.getAttribute("userID");

        if (role == null || role != 3 || userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            String reasonText = request.getParameter("reason");

            // Lưu lý do hủy
            CancelReasonDAO reasonDAO = new CancelReasonDAO();
            CancelReason reason = new CancelReason(0, orderID, reasonText);
            reasonDAO.insertCancelReason(reason);

            // Update trạng thái đơn hàng sang Canceled (6)
            OrderDAO orderDAO = new OrderDAO();
            orderDAO.updateOrderStatus(orderID, 6);

            response.sendRedirect("order");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("order");
        }
    }
}
