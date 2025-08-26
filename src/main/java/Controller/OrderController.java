package Controller;

import Model.OrderInfo;
import Model.OrderStatus;
import DAO.OrderDAO;
import DAO.OrderStatusDAO;
import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/Order")
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // UTF-8 để tránh lỗi font nếu có tham số/filter
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // Chưa đăng nhập
            response.sendRedirect("login.jsp");
            return;
        }

        final int userID = user.getUserID();
        final int roleID = user.getRoleID(); // 1 = Admin (theo bạn đang dùng)

        OrderDAO orderDAO = new OrderDAO();
        OrderStatusDAO statusDAO = new OrderStatusDAO();

        // 1) Lấy list trạng thái theo thứ tự đã custom (Paid đứng thứ 2)
        List<OrderStatus> orderStatusList = null;
        try {
            orderStatusList = statusDAO.getAllOrderStatuses();
        } catch (Exception ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (orderStatusList == null) orderStatusList = new ArrayList<>();

        request.setAttribute("orderStatusList", orderStatusList);

        // 2) Lấy danh sách đơn theo từng trạng thái, nối lại theo đúng thứ tự hiển thị
        List<OrderInfo> orderList = new ArrayList<>();

        for (OrderStatus status : orderStatusList) {
            List<OrderInfo> ordersByStatus;

            if (roleID == 1) {
                // Admin: xem tất cả đơn theo từng trạng thái
                ordersByStatus = orderDAO.getAllOrdersByStatus(status.getOrderStatusID());
            } else {
                // User thường: chỉ xem đơn của mình theo từng trạng thái
                ordersByStatus = orderDAO.getOrdersByUserAndStatus(userID, status.getOrderStatusID());
            }

            if (ordersByStatus != null && !ordersByStatus.isEmpty()) {
                orderList.addAll(ordersByStatus);
            }
        }

        // 3) Đẩy sang view
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("manageOrder.jsp").forward(request, response);
    }
}
