package Controller;

import DAO.CancelReasonDAO;
import DAO.OrderDAO;
import Model.CancelReason;
import Model.OrderInfo;
import Model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

        // Dùng session hiện có; nếu không có thì yêu cầu đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // JSP đang lưu user vào session với key "user"
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Chỉ cho Admin(1) hoặc Staff(2)
        int roleId = user.getRoleID();
        String orderIdParam = request.getParameter("orderID");
        if (roleId != 1 && roleId != 2) {
            String back = (orderIdParam != null && !orderIdParam.isEmpty())
                    ? ("order-detail?orderID=" + orderIdParam + "&err=forbidden")
                    : "order.jsp";
            response.sendRedirect(back);
            return;
        }

        try {
            int orderID = Integer.parseInt(orderIdParam);
            int newStatus = Integer.parseInt(request.getParameter("orderStatusID"));

            OrderDAO orderDAO = new OrderDAO();

            // Lấy trạng thái hiện tại (để biết trước đó có phải Paid hay không)
            OrderInfo oi = orderDAO.getOrderByID(orderID);
            int oldStatus = (oi != null ? oi.getOrderStatusID() : 0);

            String flash;

            switch (newStatus) {
                case 6: { // Cancelled + hoàn kho
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
                case 5: { // Completed do khách trả hàng (từ Processing) + hoàn kho
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
                        flash = (oldStatus == 7)
                                ? "Return completed. Inventory restocked and payment refunded."
                                : "Return completed. Inventory restocked.";
                    } else {
                        flash = "Complete return failed (order must be in Processing).";
                    }
                    break;
                }
                case 2: // Processing
                case 3: // Shipping
                case 4: // Delivered
                case 7: // Paid
                case 1: // Pending
                default: {
                    orderDAO.updateOrderStatus(orderID, newStatus);
                    if (newStatus == 2)      flash = "Moved to Processing.";
                    else if (newStatus == 3) flash = "Moved to Shipping. Estimated delivery in ~3 days.";
                    else if (newStatus == 4) flash = "Marked as Delivered.";
                    else if (newStatus == 7) flash = "Marked as Paid.";
                    else if (newStatus == 1) flash = "Back to Pending.";
                    else                      flash = "Order status updated.";
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
