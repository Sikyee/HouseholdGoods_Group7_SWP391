/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.OrderDAO;
import DAO.CancelReasonDAO;
import Model.CancelReason;
import Model.OrderInfo;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Flow:
 *  - Pending(1) or Paid(7): chọn Processing(2) hoặc Cancelled(6)
 *      + nếu Cancelled: hoàn kho ngay
 *  - Processing(2): chọn Shipping(3) hoặc Customer return -> Completed(5)
 *      + nếu Completed do return: hoàn kho ngay, nếu trước đó Paid thì hiển thị flash "đã hoàn tiền"
 *  - Shipping(3): KHÔNG cho update ở UI; auto 3 ngày -> Delivered(4)
 *  - Delivered(4): auto thêm 3 ngày -> Completed(5)
 */
@WebServlet(name = "UpdateOrderStatusController", urlPatterns = {"/update-order-status"})
public class UpdateOrderStatusController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer role = (Integer) session.getAttribute("role");
        Integer userID = (Integer) session.getAttribute("userID");

        // Chỉ cho Admin (1) hoặc Staff (2)
        if (role == null || (role != 1 && role != 2) || userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            int newStatus = Integer.parseInt(request.getParameter("orderStatusID"));

            OrderDAO orderDAO = new OrderDAO();

            // Lấy trạng thái hiện tại để biết có phải Paid trước đó không
            OrderInfo oi = orderDAO.getOrderByID(orderID);
            int oldStatus = (oi != null ? oi.getOrderStatusID() : 0);

            String flash = null;

            switch (newStatus) {
                case 6: { // Cancelled
                    // Lưu lý do nếu có (vd: Out of stock)
                    String reason = request.getParameter("cancelReason");
                    if (reason != null && !reason.trim().isEmpty()) {
                        try {
                            CancelReasonDAO reasonDAO = new CancelReasonDAO();
                            reasonDAO.insertCancelReason(new CancelReason(0, orderID, reason.trim()));
                        } catch (Exception ignore) {}
                    }
                    boolean ok = orderDAO.cancelOrderAndRestock(orderID);
                    flash = ok ? "Order cancelled and items restocked." : "Cancel failed or already cancelled.";
                    break;
                }
                case 5: { // Completed do khách trả hàng (từ Processing)
                    // Lưu lý do trả hàng
                    String returnReason = request.getParameter("returnReason");
                    if (returnReason == null || returnReason.trim().isEmpty()) {
                        returnReason = "Customer returned the item.";
                    }
                    try {
                        CancelReasonDAO reasonDAO = new CancelReasonDAO();
                        reasonDAO.insertCancelReason(new CancelReason(0, orderID, returnReason));
                    } catch (Exception ignore) {}

                    boolean ok = orderDAO.completeReturnAndRestock(orderID);
                    if (ok) {
                        if (oldStatus == 7) {
                            flash = "Return completed. Inventory restocked and payment refunded.";
                        } else {
                            flash = "Return completed. Inventory restocked.";
                        }
                    } else {
                        flash = "Complete return failed (order must be in Processing).";
                    }
                    break;
                }
                case 2: // Processing
                case 3: // Shipping
                case 4: // Delivered (không nên set tay ở UI, nhưng vẫn handle cho an toàn)
                case 7: // Paid (hiếm khi update tay)
                case 1: // Pending
                default: {
                    orderDAO.updateOrderStatus(orderID, newStatus);
                    if (newStatus == 2)      flash = "Moved to Processing.";
                    else if (newStatus == 3) flash = "Moved to Shipping. Estimated delivery in ~3 days.";
                    else if (newStatus == 4) flash = "Marked as Delivered.";
                    else if (newStatus == 7) flash = "Marked as Paid.";
                    else if (newStatus == 1) flash = "Back to Pending.";
                    break;
                }
            }

            session.setAttribute("flash", flash);
            response.sendRedirect("order-detail?orderID=" + orderID);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("order?err=internal");
        }
    }
}
