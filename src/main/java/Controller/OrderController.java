package Controller;

import Model.OrderInfo;
import Model.OrderStatus;
import DAO.OrderDAO;
import DAO.OrderStatusDAO;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order")
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userID = user.getUserID();
        int role = user.getRoleID();

        OrderDAO dao = new OrderDAO();
        OrderStatusDAO statusDAO = new OrderStatusDAO();

        List<OrderStatus> orderStatusList = statusDAO.getAllOrderStatuses();
        if (orderStatusList == null) {
            orderStatusList = new ArrayList<>();
        }
        request.setAttribute("orderStatusList", orderStatusList);

        List<OrderInfo> orderList = new ArrayList<>();

        for (OrderStatus status : orderStatusList) {
            List<OrderInfo> ordersByStatus;

            if (role == 1) {
                ordersByStatus = dao.getAllOrdersByStatus(status.getOrderStatusID());
            } else {
                ordersByStatus = dao.getOrdersByUserAndStatus(userID, status.getOrderStatusID());
            }

            orderList.addAll(ordersByStatus);
        }

        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("order.jsp").forward(request, response);
    }
}
