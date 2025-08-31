package Controller;

import DAO.OrderDAO;
import DAO.CancelReasonDAO;
import Model.CancelReason;
import Model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "CancelOrderController", urlPatterns = {"/cancelOrder", "/cancel-order"})
public class CancelOrderController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy session hiện có (không tạo mới)
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy user từ session (JSP cũng dùng session.getAttribute("user"))
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Chỉ cho Customer (roleID == 3) hủy ở flow này
        if (user.getRoleID() != 3) {
            String orderIdParam = request.getParameter("orderID");
            String back = (orderIdParam != null && !orderIdParam.isEmpty())
                    ? ("order-detail?orderID=" + orderIdParam + "&err=forbidden")
                    : "order.jsp";
            response.sendRedirect(back);
            return;
        }

        try {
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            String reasonText = request.getParameter("reason");
            if (reasonText == null || reasonText.trim().isEmpty()) {
                reasonText = "Khách hàng yêu cầu hủy";
            }

            // Lưu lý do hủy
            CancelReasonDAO reasonDAO = new CancelReasonDAO();
            reasonDAO.insertCancelReason(new CancelReason(0, orderID, reasonText.trim()));

            // Hủy đơn + hoàn kho
            OrderDAO orderDAO = new OrderDAO();
            boolean ok = orderDAO.cancelOrderAndRestock(orderID);

            if (ok) {
                session.setAttribute("flash", "Order cancelled and items restocked.");
                response.sendRedirect("order-detail?orderID=" + orderID + "&msg=cancelled");
            } else {
                session.setAttribute("flash", "Cancel failed or already cancelled.");
                response.sendRedirect("order-detail?orderID=" + orderID + "&err=cancel_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("order?err=internal");
        }
    }
}
